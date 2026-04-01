package edu.arizona.videoshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.arizona.videoshare.model.entity.Subscription;
import edu.arizona.videoshare.model.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

        List<Subscription> findBySubscriber(User subscriber);

        Optional<Subscription> findBySubscriberIdAndChannelId(Long subscriberId, Long channelId);

        long countByChannelIdAndStatus(Long channelId, Subscription.SubscriptionStatus status);

        List<Subscription> findByChannelIdAndStatus(Long channelId, Subscription.SubscriptionStatus status);

        Optional<Subscription> findBySubscriberIdAndChannelIdAndStatus(
                        Long subscriberId,
                        Long channelId,
                        Subscription.SubscriptionStatus status);

        boolean existsBySubscriberIdAndChannelIdAndStatus(
                        Long subscriberId,
                        Long channelId,
                        Subscription.SubscriptionStatus status);
}