package edu.arizona.videoshare.service;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Transactional(readOnly = true)
    public List<Channel> getChannelsByUserId(Long userId) {
        return channelRepository.findByUserId(userId);
    }

    @Transactional
    public Channel createChannelForUser(Long userId, String name, String description, MultipartFile avatarUrl) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Your account must be verified before creating a channel.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Channel name is required.");
        }
        if (channelRepository.findByUserIdAndNameIgnoreCase(userId, name.trim()).isPresent()) {
            throw new IllegalArgumentException("You already have a channel with that name.");
        }

        Channel channel = new Channel();
        channel.setName(name.trim());
        channel.setDescription(description != null && !description.trim().isEmpty() ? description.trim() : null);
        channel.setSubscriberCount(0L);
        channel.setUser(user);

        if (avatarUrl != null && !avatarUrl.isEmpty()) {

            long maxSize = 5 * 1024 * 1024;
            if (avatarUrl.getSize() > maxSize) {
                throw new IllegalArgumentException("Avatar image must be smaller than 5MB.");
            }

            String contentType = avatarUrl.getContentType();
            if (contentType == null ||
                    !(contentType.equals("image/png")
                            || contentType.equals("image/jpeg")
                            || contentType.equals("image/jpg")
                            || contentType.equals("image/webp"))) {
                throw new IllegalArgumentException("Only PNG, JPG, JPEG, or WEBP images are allowed.");
            }

            String originalFilename = avatarUrl.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;

            Path uploadDir = Paths.get(
                    System.getProperty("user.dir"),
                    "uploads",
                    "channel-avatars"
            ).toAbsolutePath().normalize();

            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(fileName);
            avatarUrl.transferTo(filePath.toFile());

            channel.setAvatarUrl("/uploads/channel-avatars/" + fileName);
        }

        Channel saved = channelRepository.save(channel);

        if (user.getRole() == UserRole.VIEWER) {
            user.setRole(UserRole.CREATOR);
            userRepository.save(user);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public Channel getChannelById(Long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));
    }

    @Transactional(readOnly = true)
    public Channel getChannelByUsernameAndName(String username, String channelName) {
        return channelRepository.findByUserUsernameIgnoreCaseAndNameIgnoreCase(username, channelName)
                .orElseThrow(() -> new RuntimeException("Channel not found"));
    }

    @Transactional(readOnly = true)
    public List<Video> getChannelVideos(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        return videoRepository.findAllByChannelIdAndVisibilityOrderByCreatedAtDesc(
                channel.getId(),
                edu.arizona.videoshare.model.enums.VideoVisibility.PUBLIC);
    }

    @Transactional
    public Channel updateChannel(Long channelId, Channel updatedChannel) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        channel.setName(updatedChannel.getName());
        channel.setDescription(updatedChannel.getDescription());
        channel.setAvatarUrl(updatedChannel.getAvatarUrl());

        return channelRepository.save(channel);
    }

    @Transactional
    public void deleteChannel(Long channelId) {
        channelRepository.deleteById(channelId);
    }
}
