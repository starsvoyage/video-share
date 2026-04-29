package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Subscription;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.service.ChannelService;
import edu.arizona.videoshare.service.VideoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ChannelPageController {

    private final ChannelService channelService;
    private final VideoService videoService;
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping("/{username}/channel/{channelName}")
    public String showChannelPage(
            @PathVariable String username,
            @PathVariable String channelName,
            HttpSession session,
            Model model) {

        Channel channel = channelService.getChannelByUsernameAndName(username, channelName);

        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");
        boolean isOwner = loggedInUserId != null && loggedInUserId.equals(channel.getUser().getId());

        boolean isSubscribed = false;
        if (loggedInUserId != null && !isOwner) {
            isSubscribed = subscriptionRepository.existsBySubscriberIdAndChannelIdAndStatus(
                    loggedInUserId,
                    channel.getId(),
                    Subscription.SubscriptionStatus.ACTIVE);
        }

        model.addAttribute("channel", channel);
        model.addAttribute("loggedInUserId", loggedInUserId);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isSubscribed", isSubscribed);
        model.addAttribute("videos", isOwner
                ? channel.getVideosOnChannel()
                : videoService.getPublicVideosForChannel(channel.getId()));

        return "channel";
    }
}