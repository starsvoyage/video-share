package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findAllByOrderByCreatedAtDesc();

    List<Video> findAllByVisibilityOrderByCreatedAtDesc(VideoVisibility visibility);

    Optional<Video> findByIdAndVisibility(Long id, VideoVisibility visibility);

    List<Video> findAllByChannelIdAndVisibilityOrderByCreatedAtDesc(Long channelId, VideoVisibility visibility);

    @Query("""
                SELECT v
                FROM Video v
                JOIN FETCH v.channel c
                JOIN Subscription s ON s.channel.id = c.id
                WHERE s.subscriber.id = :subscriberId
                  AND s.status = edu.arizona.videoshare.model.entity.Subscription$SubscriptionStatus.ACTIVE
                  AND v.visibility = :visibility
                  AND c.user.id <> :subscriberId
                ORDER BY v.createdAt DESC
            """)
    List<Video> findSubscribedVideosBySubscriberIdAndVisibility(Long subscriberId, VideoVisibility visibility);

    List<Video> findByTitleContainingIgnoreCaseAndVisibilityOrderByCreatedAtDesc(String keyword, VideoVisibility visibility);
}