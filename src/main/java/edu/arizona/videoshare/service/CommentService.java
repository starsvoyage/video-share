package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.comment.CommentResponse;
import edu.arizona.videoshare.dto.comment.CreateCommentResponse;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.exception.ForbiddenException;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.*;
import edu.arizona.videoshare.model.enums.CommentStatus;
import edu.arizona.videoshare.model.enums.NotificationType;
import edu.arizona.videoshare.model.enums.SourceType;
import edu.arizona.videoshare.repository.CommentRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
        private final CommentRepository commentRepository;
        private final UserRepository userRepository;
        private final VideoRepository videoRepository;
        private final NotificationService notificationService;

        @Transactional
        public CreateCommentResponse addComment(Long videoId, Long userId, String content, Long parentId) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("User not found: " + userId));
                Video video = videoRepository.findById(videoId)
                        .orElseThrow(() -> new NotFoundException("Video not found: " + videoId));

                Comment parent = null;
                if (parentId != null) {
                        parent = commentRepository.findById(parentId)
                                        .orElseThrow(() -> new NotFoundException("Comment not found: "
                                                                                  + parentId));

                        if (!parent.getVideoId().equals(videoId)) {
                                throw new ConflictException("Comment " + parentId
                                                             + " does not belong to video " + videoId);
                        }

                        if (parent.getStatus() == CommentStatus.REMOVED) {
                                throw new ConflictException("Cannot reply to removed comment");
                        }

                        if (parent.getParent() != null) {
                                throw new ConflictException("Nested replies are not allowed; "
                                        + "reply to the first in chain comment instead");
                        }
                }

                Comment comment = Comment.builder()
                                .user(user)
                                .videoId(videoId)
                                .parent(parent)
                                .content(content)
                                .status(CommentStatus.ACTIVE)
                                .build();

                Comment saved = commentRepository.save(comment);

                if (parent != null) {
                        notificationService.notify(
                                parent.getUser(), user,
                                NotificationType.REPLY, SourceType.COMMENT,user.getDisplayName() +
                                        " replied to your comment");
                }

                else {
                        
                        if (video != null && video.getOwner() != null) {
                                notificationService.notify(video.getOwner(), user, NotificationType.COMMENT,
                                        SourceType.COMMENT,user.getDisplayName() +
                                                " commented on your video \"" + video.getTitle() + "\"");
                        }
                }

                return CreateCommentResponse.builder()
                                .id(saved.getId())
                                .userId(saved.getUser().getId())
                                .videoId(saved.getVideoId())
                                .parentId(saved.getParent() != null ? saved.getParent().getId() : null)
                                .content(saved.getContent())
                                .createdAt(saved.getCreatedAt())
                                .updatedAt(saved.getUpdatedAt())
                                .build();
        }

        @Transactional
        public void removeComment(Long commentId, Long userId, boolean isModerator) {
                Comment c = commentRepository.findById(commentId)
                        .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));

                
                if (c.getUser().getId().equals(userId) || isModerator) {
                        c.setStatus(CommentStatus.REMOVED);
                        commentRepository.save(c);
                } else {
                        throw new ForbiddenException("You cannot remove someone else's comment");
                }
        }

        @Transactional
        public CommentResponse updateCommentStatus(Long commentId, CommentStatus newStatus) {
                Comment c = commentRepository.findById(commentId)
                        .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));

                c.setStatus(newStatus);
                Comment saved = commentRepository.save(c);
                return toResponse(saved);
        }

        @Transactional(readOnly = true)
        public List<CommentResponse> getTopLevelComments(Long videoId) {
                if (!videoRepository.existsById(videoId)) {
                        throw new NotFoundException("Video not found: " + videoId);
                }

                return commentRepository
                        .findByVideoIdAndParentIsNullOrderByCreatedAtDesc(videoId)
                        .stream()
                        .filter(c -> c.getStatus() == CommentStatus.ACTIVE)
                        .map(this::toResponse)
                        .toList();
        }

        @Transactional(readOnly = true)
        public List<CommentResponse> getReplies(Long videoId, Long commentId) {
                if (!videoRepository.existsById(videoId)) {
                        throw new NotFoundException("Video not found: " + videoId);
                }

                Comment parent = commentRepository.findById(commentId)
                        .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));

                if (!parent.getVideoId().equals(videoId)) {
                        throw new ConflictException("Comment " + commentId
                                                    + " does not belong to video " + videoId);
                }

                return commentRepository
                        .findByParent_IdOrderByCreatedAtAsc(commentId)
                        .stream()
                        .filter(c -> c.getStatus() == CommentStatus.ACTIVE)
                        .map(this::toResponse)
                        .toList();
        }


        @Transactional
        public CreateCommentResponse editComment(Long commentId, Long userId, String newContent) {
                Comment c = commentRepository.findById(commentId)
                        .orElseThrow(() -> new NotFoundException("Comment not found"));

                //Only the author can edit their comment
                if (!c.getUser().getId().equals(userId)) {
                        throw new ForbiddenException("You cannot edit someone else's comment");
                }

                c.setContent(newContent);
                Comment saved = commentRepository.save(c);
                
                return toCreateResponse(saved);
        }

        private CommentResponse toResponse(Comment c) {
                return CommentResponse.builder()
                        .id(c.getId())
                        .userId(c.getUser().getId())
                        .username(c.getUser().getUsername())
                        .displayName(c.getUser().getDisplayName())
                        .videoId(c.getVideoId())
                        .parentId(c.getParent() != null ? c.getParent().getId() : null)
                        .content(c.getContent())
                        .status(c.getStatus())
                        .likeCount(c.getLikeCount())
                        .dislikeCount(c.getDislikeCount())
                        .replyCount(c.getReplies() != null
                                ? (int) c.getReplies().stream()
                                .filter(r -> r.getStatus() == CommentStatus.ACTIVE)
                                .count()
                                : 0)
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build();
        }

        private CreateCommentResponse toCreateResponse(Comment c) {
                return CreateCommentResponse.builder()
                        .id(c.getId())
                        .userId(c.getUser().getId())
                        .videoId(c.getVideoId())
                        .parentId(c.getParent() != null ? c.getParent().getId() : null)
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build();
                }
}