package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserIdAndVideoId(Long userId, Long videoId);

    Optional<Reaction> findByUserIdAndCommentId(Long userId, Long commentId);
}