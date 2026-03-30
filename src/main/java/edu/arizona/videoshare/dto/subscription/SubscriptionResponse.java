package edu.arizona.videoshare.dto.subscription;

import edu.arizona.videoshare.model.entity.Subscription.SubscriptionStatus;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SubscriptionResponse {
    private Long id;
    private Long subscriberId;
    private String subscriberUsername;
    private Long channelId;
    private String channelName;
    private SubscriptionStatus status;
    private LocalDateTime createdAt;
    private long subscriberCount;
}
