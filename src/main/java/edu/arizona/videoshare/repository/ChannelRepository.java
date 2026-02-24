package edu.arizona.videoshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.arizona.videoshare.model.entity.Channel;


@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long>{


} 
