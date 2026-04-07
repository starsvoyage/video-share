package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.reaction.ReactResponse;
import edu.arizona.videoshare.dto.reaction.ReactionCountResponse;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.*;
import edu.arizona.videoshare.model.enums.NotificationType;
import edu.arizona.videoshare.model.enums.ReactionAction;
import edu.arizona.videoshare.model.enums.ReactionType;
import edu.arizona.videoshare.model.enums.SourceType;
import edu.arizona.videoshare.repository.CommentRepository;
import edu.arizona.videoshare.repository.ReactionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService {

        private final ReactionRepository reactionRepository;
        private final UserRepository userRepository;
        private final CommentRepository commentRepository;
        private final VideoRepository videoRepository;
        private final NotificationService notificationService;

        @Transactional
        public ReactResponse reactToVideo(Long videoId, Long userId, ReactionType type) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

                if (!videoRepository.existsById(videoId)) {
                        throw new NotFoundException("Video not found: " + videoId);
                }

                Optional<Reaction> existing = reactionRepository.findByUser_IdAndVideoId(userId, videoId);

                if (existing.isPresent()) {
                        Reaction r = existing.get();
                        if (r.getType() == type) {
                                reactionRepository.delete(r);
                                return ReactResponse.builder()
                                                .id(r.getId())
                                                .userId(userId)
                                                .videoId(videoId)
                                                .commentId(null)
                                                .type(type)
                                                .action(ReactionAction.REMOVED)
                                                .createdAt(r.getCreatedAt())
                                                .build();
                        }

                        else {
                                r.setType(type);
                                Reaction saved = reactionRepository.save(r);
                                return ReactResponse.builder()
                                                .id(saved.getId())
                                                .userId(userId)
                                                .videoId(saved.getVideoId())
                                                .commentId(null)
                                                .type(saved.getType())
                                                .action(ReactionAction.TOGGLED)
                                                .createdAt(saved.getCreatedAt())
                                                .build();
                        }
                }

                Reaction reaction = Reaction.builder()
                                .user(user)
                                .videoId(videoId)
                                .type(type)
                                .build();
                Reaction saved = reactionRepository.save(reaction);

                if (type == ReactionType.LIKE) {
                        Video video = videoRepository.findById(videoId).orElse(null);
                        if (video != null && video.getOwner() != null) {
                                notificationService.notify(video.getOwner(), user, NotificationType.LIKE_VIDEO,
                                                SourceType.REACTION,
                                                user.getDisplayName() + " liked your video \"" + video.getTitle()
                                                                + "\"");
                        }
                }

                return ReactResponse.builder()
                                .id(saved.getId())
                                .userId(userId)
                                .videoId(saved.getVideoId())
                                .commentId(null)
                                .type(saved.getType())
                                .action(ReactionAction.CREATED)
                                .createdAt(saved.getCreatedAt())
                                .build();
        }

        @Transactional
        public ReactResponse reactToComment(Long commentId, Long userId, ReactionType type) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

                Comment comment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));

                Optional<Reaction> existing = reactionRepository.findByUser_IdAndComment_Id(userId, commentId);

                if (existing.isPresent()) {
                        Reaction r = existing.get();
                        if (r.getType() == type) {
                                reactionRepository.delete(r);
                                return ReactResponse.builder()
                                                .id(r.getId())
                                                .userId(userId)
                                                .videoId(null)
                                                .commentId(commentId)
                                                .type(type)
                                                .action(ReactionAction.REMOVED)
                                                .createdAt(r.getCreatedAt())
                                                .build();
                        }

                        else {
                                r.setType(type);
                                Reaction saved = reactionRepository.save(r);
                                return ReactResponse.builder()
                                                .id(saved.getId())
                                                .userId(userId)
                                                .videoId(null)
                                                .commentId(saved.getComment() != null ? saved.getComment().getId()
                                                                : null)
                                                .type(saved.getType())
                                                .action(ReactionAction.TOGGLED)
                                                .createdAt(saved.getCreatedAt())
                                                .build();
                        }
                }

                Reaction reaction = Reaction.builder()
                                .user(user)
                                .comment(comment)
                                .type(type)
                                .build();
                Reaction saved = reactionRepository.save(reaction);

                if (type == ReactionType.LIKE && comment.getUser() != null
                                && !comment.getUser().getId().equals(userId)) {
                        notificationService.notify(
                                        comment.getUser(),
                                        user,
                                        NotificationType.LIKE_COMMENT,
                                        SourceType.REACTION,
                                        user.getDisplayName() + " liked your comment");
                }

                return ReactResponse.builder()
                                .id(saved.getId())
                                .userId(userId)
                                .videoId(null)
                                .commentId(saved.getComment() != null ? saved.getComment().getId() : null)
                                .type(saved.getType())
                                .action(ReactionAction.CREATED)
                                .createdAt(saved.getCreatedAt())
                                .build();
        }

        @Transactional(readOnly = true)
        public ReactionCountResponse getVideoReactionCounts(Long videoId) {
                if (!videoRepository.existsById(videoId)) {
                        throw new NotFoundException("Video not found: " + videoId);
                }

                long likes = reactionRepository.countByVideoIdAndType(videoId, ReactionType.LIKE);
                long dislikes = reactionRepository.countByVideoIdAndType(videoId, ReactionType.DISLIKE);

                return ReactionCountResponse.builder()
                                .videoId(videoId)
                                .likes(likes)
                                .dislikes(dislikes)
                                .build();
        }

        @Transactional(readOnly = true)
        public ReactionCountResponse getCommentReactionCounts(Long commentId) {
                if (!commentRepository.existsById(commentId)) {
                        throw new NotFoundException("Comment not found: " + commentId);
                }

                long likes = reactionRepository.countByComment_IdAndType(commentId, ReactionType.LIKE);
                long dislikes = reactionRepository.countByComment_IdAndType(commentId, ReactionType.DISLIKE);

                return ReactionCountResponse.builder()
                                .videoId(null)
                                .commentId(commentId)
                                .likes(likes)
                                .dislikes(dislikes)
                                .build();
        }
}