package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Subscription;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.Subscription.SubscriptionStatus;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;
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
    public String toggleSubscription(@PathVariable Long channelId, HttpSession session) {

        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        // prevent self-subscription
        if (channel.getUser().getId().equals(loggedInUserId)) {
            return "redirect:/" + channel.getUser().getUsername() + "/channel/" + channel.getName();
        }

        // check if already subscribed
        var existing = subscriptionRepository.findBySubscriberIdAndChannelIdAndStatus(
                loggedInUserId,
                channelId,
                Subscription.SubscriptionStatus.ACTIVE);

        if (existing.isPresent()) {
            subscriptionRepository.delete(existing.get());

            channel.setSubscriberCount(Math.max(0, channel.getSubscriberCount() - 1));
        } else {
            Subscription sub = new Subscription();
            sub.setSubscriber(user);
            sub.setChannel(channel);
            sub.setStatus(Subscription.SubscriptionStatus.ACTIVE);

            subscriptionRepository.save(sub);
            channel.setSubscriberCount(channel.getSubscriberCount() + 1);
        }

        channelRepository.save(channel);

        return "redirect:/" + channel.getUser().getUsername() + "/channel/" + channel.getName();
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