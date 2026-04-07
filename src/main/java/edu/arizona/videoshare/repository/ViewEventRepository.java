package edu.arizona.videoshare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.arizona.videoshare.model.entity.ViewEvent;

public interface ViewEventRepository extends JpaRepository<ViewEvent, Long> {
    List<ViewEvent> findByUserIdOrderByWatchedAtDesc(Long userId);

    List<ViewEvent> findByVideoId(Long videoId);

    Optional<ViewEvent> findByUserIdAndVideoId(Long userId, Long videoId);
}
