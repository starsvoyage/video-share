package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.exception.ForbiddenException;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Ad;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.AdPlacement;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.service.VideoService;
import edu.arizona.videoshare.service.AdService;
import edu.arizona.videoshare.service.PlaylistService;
import edu.arizona.videoshare.service.UserMembershipService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class VideoPageController {

    private final VideoService videoService;
    private final PlaylistService playlistService;
    private final AdService adService;
    private final UserMembershipService userMembershipService;

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

        boolean isAdFree = false;

        if (loggedInUserId != null) {
            model.addAttribute("playlists", playlistService.getByUser(loggedInUserId));

            try {
                isAdFree = userMembershipService.getCurrentMembership(loggedInUserId) != null;
            } catch (NotFoundException e) {
                
            }

        }

        if (!isAdFree) {
                Ad preRollAd = adService.selectActiveAd(AdPlacement.Pre_roll);
                Ad midRollAd = adService.selectActiveAd(AdPlacement.Mid_roll);
                Ad bannerAd = adService.selectActiveAd(AdPlacement.banner);
                Ad postRollAd = adService.selectActiveAd(AdPlacement.Post_roll);
                model.addAttribute("postRollAd", postRollAd);
                model.addAttribute("bannerAd", bannerAd);
                model.addAttribute("ad", preRollAd);
                model.addAttribute("midRollAd", midRollAd);
            }

        return "video";
    }
}
