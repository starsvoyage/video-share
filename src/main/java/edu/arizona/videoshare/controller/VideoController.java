package edu.arizona.videoshare.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import java.util.List;

import edu.arizona.videoshare.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import edu.arizona.videoshare.exception.ForbiddenException;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.repository.ChannelRepository;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final ChannelRepository channelRepository;

    private void requireAdmin(HttpServletRequest request) {
        Object roleObj = request.getSession().getAttribute("loggedInRole");

        if (roleObj == null) {
            throw new ForbiddenException("Authentication required");
        }

        if (!roleObj.toString().equals("ADMIN")) {
            throw new ForbiddenException("Admin access required");
        }
    }

    @PostMapping("/channel/{channelId}/videos")
    public Video create(
            @PathVariable Long channelId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            HttpServletRequest request
    ) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        Long userId = (Long) request.getSession().getAttribute("loggedInUserId");
        if (userId == null) {
            throw new ForbiddenException("Authentication required");
        }

        if (!channel.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You are not the owner of this channel");
        }

        return videoService.create(channelId, file, title);
    }

    @GetMapping("/{id}")
    public Video get(@PathVariable Long id) {
        return videoService.getPublic(id);
    }

    @GetMapping
    public List<Video> getAll() {
        return videoService.getAllPublic();
    }

    @GetMapping("/search")
    public List<Video> search(@RequestParam(name = "q", defaultValue = "") String query) {
        return videoService.searchPublicByTitle(query);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        videoService.delete(id);
    }
}