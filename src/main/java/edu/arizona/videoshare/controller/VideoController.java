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

    @PostMapping("/channel/{channelId}/videos")
    public edu.arizona.videoshare.model.entity.Video create(@PathVariable Long channelId, @RequestParam("file") MultipartFile file, @RequestParam("title") String title, HttpServletRequest request) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("Channel not found"));

        //Checking if user is logged in and is the owner of the channel
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
    public edu.arizona.videoshare.model.entity.Video get(@PathVariable Long id) {
        return videoService.get(id);
    }

    @GetMapping
    public List<edu.arizona.videoshare.model.entity.Video> getAll() {
        return videoService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        videoService.delete(id);
    }
}