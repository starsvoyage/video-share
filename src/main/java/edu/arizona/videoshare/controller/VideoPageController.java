package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.service.VideoService;
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

    @GetMapping("/videos/{videoId}")
    public String showVideoPage(
            @PathVariable Long videoId,
            HttpSession session,
            Model model) {
        Video video = videoService.getPublic(videoId);

        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        model.addAttribute("video", video);
        model.addAttribute("loggedInUserId", loggedInUserId);

        return "video";
    }
}
