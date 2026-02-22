package edu.arizona.videoshare.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.arizona.videoshare.model.Channel;
import edu.arizona.videoshare.model.User;
import edu.arizona.videoshare.model.Video;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RequiredArgsConstructor
@RestController
@RequestMapping("/channels")
public class ChannelController {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final VideoRepository videoRepository;
    
    @PostMapping
    public Channel createChannel(@RequestParam String channelName, @RequestParam String description, @RequestParam Long ownerUserId) {
        User owner = userRepository.findById(ownerUserId).orElseThrow(() -> new RuntimeException("User not found"));

        Channel newChannel = new Channel();
        newChannel.setName(channelName);
        newChannel.setDescription(description);
        newChannel.setUser(owner);
        newChannel.setSubscriberCount(0L);
        
        return channelRepository.save(newChannel);

    }
    
    @GetMapping("/{channelId}")
    public Channel getChannel(@PathVariable Long channelId) { 
        return channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("Channel not found"));
    }

    @GetMapping("/{channelId}/videos")
    public List<Video> getChannelVideos(@PathVariable Long channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("Channel not found"));

        return videoRepository.findByChannel(channel);
    }

    @PutMapping("/{channelId}")
    public Channel updateChannel(@PathVariable Long channelId, @RequestBody Channel updatedChannel) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("Channel not found"));

        channel.setName(updatedChannel.getName());
        channel.setDescription(updatedChannel.getDescription());
        channel.setAvatarUrl(updatedChannel.getAvatarUrl());

        return channelRepository.save(channel);
    }

    @DeleteMapping("/{channelId}")
    public void deleteChannel(@PathVariable Long channelId) {
        channelRepository.deleteById(channelId);

    }
    
    
}
