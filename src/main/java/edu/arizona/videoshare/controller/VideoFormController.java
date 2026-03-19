package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.service.VideoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class VideoFormController {

    private final VideoService videoService;

    @PostMapping("/videos/create")
    public String createVideo(
            @RequestParam String title,
            @RequestParam Long channelId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        try {
            Video createdVideo = videoService.createVideoForUser(
                    loggedInUserId,
                    channelId,
                    title);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Video \"" + createdVideo.getTitle() + "\" created successfully.");

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("openUploadVideoModal", true);
            redirectAttributes.addFlashAttribute("videoTitleValue", title);
            redirectAttributes.addFlashAttribute("videoChannelIdValue", channelId);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create video.");
            redirectAttributes.addFlashAttribute("openUploadVideoModal", true);
            redirectAttributes.addFlashAttribute("videoTitleValue", title);
            redirectAttributes.addFlashAttribute("videoChannelIdValue", channelId);
        }

        return "redirect:/you";
    }
}