package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.PlaylistVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * PlaylistVideoRepository
 */
public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, Long> {

    List<PlaylistVideo> findByPlaylistIdOrderByPositionAsc(Long playlistId);

    Optional<PlaylistVideo> findByPlaylistIdAndVideoId(Long playlistId, Long videoId);
}
