package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Video;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public Video create(Long channelId, MultipartFile file, String title) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new NotFoundException("Channel not found"));
        Video video = new Video();
        if (title == null || title.isEmpty()) {
            video.setTitle("Untitled Video");
        } else {
            video.setTitle(title);
        }
        video.setChannel(channel);

        //Saving file to local storage
        String fileName = "video_" + System.currentTimeMillis() + ".mp4";
        String filePath = "uploads/videos/" + fileName;
        video.setFilePath(filePath);

        try {
            //Create directory if needed
            java.io.File dir = new java.io.File("uploads/videos");
            if (!dir.exists()) dir.mkdirs();
            
            // Save the file
            Files.copy(file.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        return videoRepository.save(video);
    }

    public Video get(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Video not found"));
    }

    public Video getPublic(Long id) {
        return videoRepository.findByIdAndVisibility(id, VideoVisibility.PUBLIC)
                .orElseThrow(() -> new NotFoundException("Video not found"));
    }

    public List<Video> getAll() {
        return videoRepository.findAll();
    }

    public List<Video> getAllPublic() {
        return videoRepository.findAllByVisibilityOrderByCreatedAtDesc(VideoVisibility.PUBLIC);
    }

    public List<Video> getPublicVideosForChannel(Long channelId) {
        return videoRepository.findAllByChannelIdAndVisibilityOrderByCreatedAtDesc(channelId, VideoVisibility.PUBLIC);
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
        video.setVisibility(VideoVisibility.PUBLIC);

        return videoRepository.save(video);
    }
}
