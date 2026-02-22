package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * VideoRepository
 *
 * Persistence abstraction for Video entities.
 */
public interface VideoRepository extends JpaRepository<Video, Long> { 
}
