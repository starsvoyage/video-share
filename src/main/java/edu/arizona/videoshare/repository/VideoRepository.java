package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * VideoRepository
 *
 * Persistence abstraction for Video entities.
 */
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findAllByOrderByCreatedAtDesc();
    List<Video> findAllByVisibilityOrderByCreatedAtDesc(VideoVisibility visibility);
    Optional<Video> findByIdAndVisibility(Long id, VideoVisibility visibility);
    List<Video> findAllByChannelIdAndVisibilityOrderByCreatedAtDesc(Long channelId, VideoVisibility visibility);
}
