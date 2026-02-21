package edu.arizona.videoshare.service;

import edu.arizona.videoshare.model.entity.*;
import edu.arizona.videoshare.repository.CommentRepository;
import edu.arizona.videoshare.repository.ReactionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReactionService {

        private final ReactionRepository reactionRepository;
        private final UserRepository userRepository;
        private final CommentRepository commentRepository;

        @Transactional
        public Reaction reactToVideo(Long videoId, Long userId, ReactionType type) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

                // One reaction per user per video (upsert)
                Reaction reaction = reactionRepository.findByUserIdAndVideoId(userId, videoId)
                                .orElseGet(() -> Reaction.builder()
                                                .user(user)
                                                .videoId(videoId) // ✅ changed
                                                .build());

                reaction.setType(type);
                return reactionRepository.save(reaction);
        }

        @Transactional
        public Reaction reactToComment(Long commentId, Long userId, ReactionType type) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

                Comment comment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

                Reaction reaction = reactionRepository.findByUserIdAndCommentId(userId, commentId)
                                .orElseGet(() -> Reaction.builder()
                                                .user(user)
                                                .comment(comment)
                                                .build());

                reaction.setType(type);
                return reactionRepository.save(reaction);
        }
}