package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.comment.CreateCommentResponse;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.*;
import edu.arizona.videoshare.repository.CommentRepository;
import edu.arizona.videoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

        private final CommentRepository commentRepository;
        private final UserRepository userRepository;

        @Transactional
        public CreateCommentResponse addComment(Long videoId, Long userId, String content, Long parentId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

                Comment parent = null;
                if (parentId != null) {
                        parent = commentRepository.findById(parentId)
                                        .orElseThrow(() -> new NotFoundException(
                                                        "Parent comment not found: " + parentId));
                }

                Comment comment = Comment.builder()
                                .user(user)
                                .videoId(videoId)
                                .parent(parent)
                                .content(content)
                                .status(CommentStatus.ACTIVE)
                                .build();

                Comment saved = commentRepository.save(comment);

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
        public void removeComment(Long commentId) {
                Comment c = commentRepository.findById(commentId)
                                .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));

                c.setStatus(CommentStatus.REMOVED);
                commentRepository.save(c);
        }
}