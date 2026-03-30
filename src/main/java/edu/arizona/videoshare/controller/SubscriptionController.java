package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.subscription.SubscribeRequest;
import edu.arizona.videoshare.dto.subscription.SubscriptionResponse;
import edu.arizona.videoshare.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Subscription;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.Subscription.SubscriptionStatus;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionResponse subscribe(@Valid @RequestBody SubscribeRequest request) {
        return subscriptionService.subscribe(request);
    }

    @DeleteMapping
    public SubscriptionResponse unsubscribe(
            @RequestParam Long subscriberId,
            @RequestParam Long channelId) {
        return subscriptionService.unsubscribe(subscriberId, channelId);
    }

    @GetMapping("/users/{userId}")
    public List<Subscription> getUserSubscriptions(@PathVariable Long userId) {
        return subscriptionService.getUserSubscriptions(userId);
    }

    @GetMapping("/check")
    public boolean isSubscribed(
            @RequestParam Long subscriberId,
            @RequestParam Long channelId) {
        return subscriptionService.isSubscribed(subscriberId, channelId);
    }
}
