package edu.arizona.videoshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.arizona.videoshare.model.entity.Channel;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long>{

    List<Channel> findByUserId(Long userId);

    List<Channel> findAllByOrderByNameAsc();

    List<Channel> findByNameContainingIgnoreCaseOrderByNameAsc(String keyword);

    Optional<Channel> findByUserIdAndNameIgnoreCase(Long userId, String name);

    Optional<Channel> findByUserUsernameIgnoreCaseAndNameIgnoreCase(String username, String name);
} 
