package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.reaction.ReactResponse;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.*;
import edu.arizona.videoshare.model.enums.ReactionType;
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
        public ReactResponse reactToVideo(Long videoId, Long userId, ReactionType type) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

                Reaction reaction = reactionRepository.findByUser_IdAndVideoId(userId, videoId)
                                .orElseGet(() -> Reaction.builder()
                                                .user(user)
                                                .videoId(videoId)
                                                .build());

                reaction.setType(type);
                Reaction saved = reactionRepository.save(reaction);

                return ReactResponse.builder()
                                .id(saved.getId())
                                .userId(saved.getUser().getId())
                                .videoId(saved.getVideoId())
                                .commentId(saved.getComment() != null ? saved.getComment().getId() : null)
                                .type(saved.getType())
                                .createdAt(saved.getCreatedAt())
                                .build();
        }

        @Transactional
        public ReactResponse reactToComment(Long commentId, Long userId, ReactionType type) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

                Comment comment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));

                Reaction reaction = reactionRepository.findByUser_IdAndComment_Id(userId, commentId)
                                .orElseGet(() -> Reaction.builder()
                                                .user(user)
                                                .comment(comment)
                                                .build());

                reaction.setType(type);
                Reaction saved = reactionRepository.save(reaction);

                return ReactResponse.builder()
                                .id(saved.getId())
                                .userId(saved.getUser().getId())
                                .videoId(saved.getVideoId())
                                .commentId(saved.getComment() != null ? saved.getComment().getId() : null)
                                .type(saved.getType())
                                .createdAt(saved.getCreatedAt())
                                .build();
        }
}