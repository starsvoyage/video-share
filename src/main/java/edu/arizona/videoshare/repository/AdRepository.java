package edu.arizona.videoshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.arizona.videoshare.model.entity.Ad;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {
    // Get all ads for a video
    List<Ad> findByVideoId(Long videoId);
}
