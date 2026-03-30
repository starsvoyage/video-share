package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.subscription.SubscribeRequest;
import edu.arizona.videoshare.dto.subscription.SubscriptionResponse;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Subscription;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    public SubscriptionResponse subscribe(SubscribeRequest request) {
        User subscriber = userRepository.findById(request.getSubscriberId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.getSubscriberId()));

        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new NotFoundException("Channel not found: " + request.getChannelId()));

        if (channel.getUser().getId().equals(subscriber.getId())) {
            throw new ConflictException("Cannot subscribe to self owned channel");
        }

        Optional<Subscription> existing = subscriptionRepository.findBySubscriberIdAndChannelId
                (subscriber.getId(), channel.getId());

        Subscription sub;

        if (existing.isPresent()) {
            sub = existing.get();

            if (sub.getStatus() == Subscription.SubscriptionStatus.ACTIVE) {
                throw new ConflictException("Already subscribed to this channel");
            }

            sub.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        }

        else {
            sub = new Subscription();
            sub.setSubscriber(subscriber);
            sub.setChannel(channel);
            sub.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        }

        Subscription saved = subscriptionRepository.save(sub);

        syncSubscriberCount(channel);

        return toResponse(saved, channel);
    }

    @Transactional
    public SubscriptionResponse unsubscribe(Long subscriberId, Long channelId) {
        Subscription sub = subscriptionRepository.findBySubscriberIdAndChannelId(subscriberId, channelId)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        if (sub.getStatus() == Subscription.SubscriptionStatus.CANCELLED) {
            throw new ConflictException("You have alread unsubscribed from this channel");
        }

        sub.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        Subscription saved = subscriptionRepository.save(sub);

        Channel channel = saved.getChannel();
        syncSubscriberCount(channel);

        return toResponse(saved, channel);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getUserSubscriptions(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found: " + userId));
        return subscriptionRepository.findBySubscriber(user);
    }

    @Transactional(readOnly = true)
    public boolean isSubscribed(Long subscriberId, Long channelId) {
        Optional<Subscription> sub = subscriptionRepository.findBySubscriberIdAndChannelId(subscriberId, channelId);
        return sub.isPresent() && sub.get().getStatus() == Subscription.SubscriptionStatus.ACTIVE;
    }

    private void syncSubscriberCount(Channel channel) {
        long activeCount = subscriptionRepository.countByChannelIdAndStatus(channel.getId(),
                Subscription.SubscriptionStatus.ACTIVE);
        channel.setSubscriberCount(activeCount);
        channelRepository.save(channel);
    }

    private SubscriptionResponse toResponse(Subscription sub, Channel channel) {
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .subscriberId(sub.getSubscriber().getId())
                .subscriberUsername(sub.getSubscriber().getUsername())
                .channelId(channel.getId())
                .channelName(channel.getName())
                .status(sub.getStatus())
                .createdAt(sub.getCreatedAt())
                .subscriberCount(channel.getSubscriberCount())
                .build();
    }
}
