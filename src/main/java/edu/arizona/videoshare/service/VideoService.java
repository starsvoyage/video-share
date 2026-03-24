package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import edu.arizona.videoshare.model.entity.Video;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public Video create(Video video) {
        if (video.getChannel() == null || video.getChannel().getId() == null) {
            throw new NotFoundException("Channel is required");
        }

        Channel channel = channelRepository.findById(video.getChannel().getId())
                .orElseThrow(() -> new NotFoundException(
                        "Channel not found: " + video.getChannel().getId()));

        video.setChannel(channel);
        return videoRepository.save(video);
    }

    public Video get(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Video not found"));
    }

    public List<Video> getAll() {
        return videoRepository.findAll();
    }

    public void delete(Long id) {
        videoRepository.deleteById(id);
    }

    public Video createVideoForUser(Long userId, Long channelId, String title) {
        if (userId == null) {
            throw new IllegalArgumentException("You must be logged in to upload a video.");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("Please select a channel.");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Video title is required.");
        }

        if (title.trim().length() > 200) {
            throw new IllegalArgumentException("Video title must be 200 characters or fewer.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("You must verify your account before uploading videos.");
        }

        if (user.getRole() != UserRole.CREATOR) {
            throw new IllegalArgumentException("Only creators can upload videos.");
        }

        if (!channel.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only upload videos to your own channel.");
        }

        Video video = new Video();
        video.setTitle(title.trim());
        video.setOwner(user);
        video.setChannel(channel);

        return videoRepository.save(video);
    }
}