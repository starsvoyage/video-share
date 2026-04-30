package edu.arizona.videoshare.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.arizona.videoshare.dto.subscription.SubscribeRequest;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Subscription;
import edu.arizona.videoshare.model.entity.Subscription.SubscriptionStatus;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.service.SubscriptionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @ResponseBody
    @PostMapping
    public Subscription subscribe(@RequestParam Long subscriberId, @RequestParam Long channelId) {
        User user = userRepository.findById(subscriberId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        if (channel.getUser().getId().equals(subscriberId)) {
            throw new RuntimeException("You cannot subscribe to your own channel.");
        }

        boolean alreadySubscribed = subscriptionRepository.existsBySubscriberIdAndChannelIdAndStatus(
                subscriberId, channelId, SubscriptionStatus.ACTIVE);

        if (alreadySubscribed) {
            throw new RuntimeException("You are already subscribed to this channel.");
        }

        Subscription sub = new Subscription();
        sub.setSubscriber(user);
        sub.setChannel(channel);
        sub.setStatus(SubscriptionStatus.ACTIVE);

        channel.setSubscriberCount(channel.getSubscriberCount() + 1);
        channelRepository.save(channel);

        return subscriptionRepository.save(sub);
    }

    @PostMapping("/channels/{channelId}")
    public String toggleSubscription(@PathVariable Long channelId,
                                    HttpSession session,
                                    @RequestHeader(value = "Referer", required = false) String referer) {

        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        if (channel.getUser().getId().equals(loggedInUserId)) {
            return "redirect:/" + channel.getUser().getUsername() + "/channel/" + channel.getName();
        }

        boolean isSubscribed = subscriptionService.isSubscribed(loggedInUserId, channelId);

        if (isSubscribed) {
            subscriptionService.unsubscribe(loggedInUserId, channelId);
        } else {
            SubscribeRequest request = new SubscribeRequest();
            request.setSubscriberId(loggedInUserId);
            request.setChannelId(channelId);
            subscriptionService.subscribe(request);
        }

        return "redirect:" + (referer != null ? referer : "/");
    }

    @ResponseBody
    @GetMapping("/users/{userId}/subscriptions")
    public List<Subscription> getUserSubscriptions(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return subscriptionRepository.findBySubscriber(user);
    }

    @ResponseBody
    @DeleteMapping("/{subscriptionId}")
    public void cancelSubscription(@PathVariable Long subscriptionId) {
        subscriptionRepository.deleteById(subscriptionId);
    }
}