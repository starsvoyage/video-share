package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Subscription;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.entity.ViewEvent;
import edu.arizona.videoshare.service.SubscriptionService;
import edu.arizona.videoshare.service.VideoService;
import edu.arizona.videoshare.service.ViewEventService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LibraryPageController {

    private final VideoService videoService;
    private final ViewEventService viewEventService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/subscriptions/videos")
    public String showSubscribedVideosPage(
            @RequestParam(required = false) Long channelId,
            HttpSession session,
            Model model
    ) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        List<Video> videos = videoService.getSubscribedVideos(loggedInUserId);

        if (channelId != null) {
            videos = videos.stream()
                    .filter(video -> video.getChannel() != null
                            && video.getChannel().getId().equals(channelId))
                    .toList();
        }

        Map<Channel, List<Video>> videosByChannel = new LinkedHashMap<>();

        for (Video video : videos) {
            if (video.getChannel() == null) {
                continue;
            }

            videosByChannel.computeIfAbsent(video.getChannel(), c -> new java.util.ArrayList<>())
                    .add(video);
        }

        List<Subscription> subscriptions = subscriptionService.getActiveSubscriptionsForUser(loggedInUserId);

        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("videosByChannel", videosByChannel);
        model.addAttribute("selectedChannelId", channelId);
        model.addAttribute("loggedInUserId", loggedInUserId);

        return "subscribed-videos";
    }

    @GetMapping("/history")
    public String showWatchHistoryPage(HttpSession session, Model model) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        List<ViewEvent> history = viewEventService.getUserHistory(loggedInUserId);

        model.addAttribute("history", history);
        model.addAttribute("loggedInUserId", loggedInUserId);

        return "watch-history";
    }
}