package edu.arizona.videoshare.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.arizona.videoshare.model.Channel;
import edu.arizona.videoshare.model.Subscription;
import edu.arizona.videoshare.model.Subscription.SubscriptionStatus;
import edu.arizona.videoshare.model.User;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RequiredArgsConstructor
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @PostMapping
    public Subscription subscribe(@RequestParam Long subscriberId, @RequestParam Long channelId) {
        User user = userRepository.findById(subscriberId).orElseThrow(() -> new RuntimeException("User not found"));
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("Channel not found"));

        Subscription sub = new Subscription();
        sub.setSubscriber(user);
        sub.setChannel(channel);
        sub.setStatus(SubscriptionStatus.ACTIVE);

        channel.setSubscriberCount(channel.getSubscriberCount() + 1);

        return subscriptionRepository.save(sub);

    }

    @GetMapping("/users/{userId}/subscriptions")
    public List<Subscription> getUserSubscriptions(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return subscriptionRepository.findBySubscriber(user);
    }

    @DeleteMapping("/subscriptions/{subscriptionId}")
    public void cancelSubscription(@PathVariable Long subscriptionId) {

        subscriptionRepository.deleteById(subscriptionId);
    }
    
}
