package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.service.ChannelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.exception.ForbiddenException;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;


@RequiredArgsConstructor
@Controller
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;

    @PostMapping("/create")
    public String createChannel(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile avatarUrl,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        try {
            Channel createdChannel = channelService.createChannelForUser(
                    loggedInUserId,
                    name,
                    description,
                    avatarUrl
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Channel \"" + createdChannel.getName() + "\" created successfully."
            );

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("openCreateChannelModal", true);
            redirectAttributes.addFlashAttribute("channelNameValue", name);
            redirectAttributes.addFlashAttribute("channelDescriptionValue", description);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload image.");
            redirectAttributes.addFlashAttribute("openCreateChannelModal", true);
            redirectAttributes.addFlashAttribute("channelNameValue", name);
            redirectAttributes.addFlashAttribute("channelDescriptionValue", description);
        }

        return "redirect:/you";
    }

    @ResponseBody
    @GetMapping("/{channelId}")
    public Channel getChannel(@PathVariable Long channelId) {
        return channelService.getChannelById(channelId);
    }

    @ResponseBody
    @GetMapping("/{channelId}/videos")
    public List<Video> getChannelVideos(@PathVariable Long channelId) {
        return channelService.getChannelVideos(channelId);
    }

    @ResponseBody
    @PutMapping("/{channelId}")
    public Channel updateChannel(@PathVariable Long channelId, @RequestBody Channel updatedChannel, HttpServletRequest request) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("Channel not found"));

        //Checking if user is logged in and is the owner of the channel
        Long userId = (Long) request.getSession().getAttribute("loggedInUserId");
        if (userId == null) {
            throw new ForbiddenException("Authentication required");
        }

        if (!channel.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You are not the owner of this channel");
        }

        channel.setName(updatedChannel.getName());
        channel.setDescription(updatedChannel.getDescription());
        channel.setAvatarUrl(updatedChannel.getAvatarUrl());

        return channelRepository.save(channel);
    }

    @ResponseBody
    @DeleteMapping("/{channelId}")
    public void deleteChannel(@PathVariable Long channelId, HttpServletRequest request) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("Channel not found"));

        //Checking if user is logged in and is the owner of the channel
        Long userId = (Long) request.getSession().getAttribute("loggedInUserId");
        if (userId == null) {
            throw new ForbiddenException("Authentication required");
        }

        if (!channel.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You are not the owner of this channel");
        }

        channelRepository.deleteById(channelId);

    }
    
    
}
