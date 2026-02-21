package edu.arizona.videoshare.service;

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
        public Comment addComment(Long videoId, Long userId, String content, Long parentId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

                Comment parent = null;
                if (parentId != null) {
                        parent = commentRepository.findById(parentId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Parent comment not found: " + parentId));
                }

                Comment comment = Comment.builder()
                                .user(user)
                                .videoId(videoId) // ✅ changed
                                .parent(parent)
                                .content(content)
                                .status(CommentStatus.ACTIVE)
                                .build();

                return commentRepository.save(comment);
        }

        @Transactional
        public void removeComment(Long commentId) {
                Comment c = commentRepository.findById(commentId)
                                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

                c.setStatus(CommentStatus.REMOVED);
                commentRepository.save(c);
        }
}