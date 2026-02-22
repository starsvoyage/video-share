package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.Playlist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * PlaylistRepository
 */
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    /** All playlists owned by one user */
    @EntityGraph(attributePaths = {"user", "items", "items.video"})
    List<Playlist> findByUserId(Long userId);

    /** Playlist with user + items + item videos loaded (prevents LazyInitialization problems in controllers). */
    @EntityGraph(attributePaths = {"user", "items", "items.video"})
    Optional<Playlist> findWithItemsById(Long id);
}
