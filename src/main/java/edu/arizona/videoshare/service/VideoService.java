package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Subscription;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.NotificationType;
import edu.arizona.videoshare.model.enums.SourceType;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.SubscriptionRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SubscriptionRepository subscriptionRepository;

    private static final long MAX_VIDEO_SIZE_BYTES = 50L * 1024L * 1024L;

    public Video create(Long channelId, MultipartFile file, String title) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));

        Video video = new Video();
        video.setTitle((title == null || title.isBlank()) ? "Untitled Video" : title.trim());
        video.setChannel(channel);
        video.setOwner(channel.getUser());
        video.setVisibility(VideoVisibility.PUBLIC);

        attachUploadedVideo(video, file);

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
        return videoRepository.findAllByChannelIdAndVisibilityOrderByCreatedAtDesc(
                channelId, VideoVisibility.PUBLIC);
    }

    public void delete(Long id) {
        videoRepository.deleteById(id);
    }

    public Video createVideoForUser(Long userId, Long channelId, String title, MultipartFile file) {
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

        attachUploadedVideo(video, file);

        Video saved = videoRepository.save(video);

        List<Subscription> subs = subscriptionRepository.findByChannelIdAndStatus(
                channelId, Subscription.SubscriptionStatus.ACTIVE);

        for (Subscription s : subs) {
            notificationService.notify(
                    s.getSubscriber(),
                    user,
                    NotificationType.UPLOAD,
                    SourceType.VIDEO,
                    user.getDisplayName() + " uploaded \"" + title.trim() + "\" to " + channel.getName());
        }

        return saved;
    }

    private void attachUploadedVideo(Video video, MultipartFile file) {
        validateVideoFile(file);

        String originalName = file.getOriginalFilename() == null
                ? "video.mp4"
                : file.getOriginalFilename();

        String extension = extractExtension(originalName);
        String fileName = UUID.randomUUID() + extension;

        Path uploadDir = Paths.get("uploads", "videos").toAbsolutePath().normalize();
        Path target = uploadDir.resolve(fileName);

        try {
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save uploaded video.", e);
        }

        video.setFilePath(target.toString());
        video.setMediaUrl("/uploads/videos/" + fileName);
    }

    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please choose a video file to upload.");
        }

        if (file.getSize() > MAX_VIDEO_SIZE_BYTES) {
            throw new IllegalArgumentException("Video file must be 6 MB or smaller.");
        }

        String contentType = file.getContentType();
        String originalName = file.getOriginalFilename() == null
                ? ""
                : file.getOriginalFilename().toLowerCase();

        boolean looksLikeVideo = (contentType != null && contentType.startsWith("video/"))
                || originalName.endsWith(".mp4")
                || originalName.endsWith(".webm")
                || originalName.endsWith(".ogg");

        if (!looksLikeVideo) {
            throw new IllegalArgumentException("Please upload an MP4, WebM, or OGG video file.");
        }
    }

    private String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return ".mp4";
        }

        String extension = filename.substring(dotIndex).toLowerCase();
        return switch (extension) {
            case ".mp4", ".webm", ".ogg" -> extension;
            default -> ".mp4";
        };
    }
}