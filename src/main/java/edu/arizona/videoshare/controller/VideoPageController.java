package edu.arizona.videoshare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import edu.arizona.videoshare.exception.ForbiddenException;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.service.PlaylistService;
import edu.arizona.videoshare.service.SubscriptionService;
import edu.arizona.videoshare.service.VideoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VideoPageController {

    private final VideoService videoService;
    private final PlaylistService playlistService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/videos/{videoId}")
    public String showVideoPage(
            @PathVariable Long videoId,
            HttpSession session,
            Model model) {
        Video video = videoService.get(videoId);

        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (video.getVisibility() == VideoVisibility.PRIVATE) {
            Long ownerId = video.getOwner() != null
                    ? video.getOwner().getId()
                    : video.getChannel().getUser().getId();
            if (loggedInUserId == null || !loggedInUserId.equals(ownerId)) {
                throw new ForbiddenException("This video is private");
            }
        }

        model.addAttribute("video", video);
        model.addAttribute("loggedInUserId", loggedInUserId);

        if (loggedInUserId != null) {
            model.addAttribute("playlists", playlistService.getByUser(loggedInUserId));
            boolean isSubscribed = subscriptionService.isSubscribed(loggedInUserId, video.getChannel().getId());
            model.addAttribute("isSubscribed", isSubscribed);
        }

        return "video";
    }
}
