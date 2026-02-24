package edu.arizona.videoshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.arizona.videoshare.model.Ad;

public interface AdRepository extends JpaRepository<Ad, Long> {
}
