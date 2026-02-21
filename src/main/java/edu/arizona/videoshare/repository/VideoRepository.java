package edu.arizona.videoshare.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import edu.arizona.videoshare.model.Video;


public interface VideoRepository extends JpaRepository<Video, Long> {
    
}
