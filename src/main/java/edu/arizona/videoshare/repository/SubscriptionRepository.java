package edu.arizona.videoshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.arizona.videoshare.model.entity.Subscription;
import edu.arizona.videoshare.model.entity.User;
import java.util.List;


@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findBySubscriber(User subscriber);
} 
