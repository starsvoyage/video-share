package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.PlaylistLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, Long> {

    Optional<PlaylistLike> findByUserIdAndPlaylistId(Long userId, Long playlistId);

    long countByPlaylistId(Long playlistId);

    void deleteByUserIdAndPlaylistId(Long userId, Long playlistId);
}