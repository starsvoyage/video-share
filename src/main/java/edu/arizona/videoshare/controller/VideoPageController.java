package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.repository.VideoRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class VideoPageController {

    private final VideoRepository videoRepository;

    @GetMapping("/videos/{videoId}")
    public String showVideoPage(
            @PathVariable Long videoId,
            HttpSession session,
            Model model) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        model.addAttribute("video", video);
        model.addAttribute("loggedInUserId", loggedInUserId);

        return "video";
    }
}