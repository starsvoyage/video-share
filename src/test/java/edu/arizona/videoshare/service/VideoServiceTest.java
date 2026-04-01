package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.UserCredentials;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class VideoServiceTest {

    @Autowired VideoService videoService;
    @Autowired VideoRepository videoRepository;
    @Autowired UserRepository userRepository;
    @Autowired ChannelRepository channelRepository;

    @Test
    void getAllPublicReturnsOnlyPublicVideos() {
        Channel channel = saveChannel("creator-publics", "publics-channel");

        saveVideo(channel, "Public Video", VideoVisibility.PUBLIC);
        saveVideo(channel, "Private Video", VideoVisibility.PRIVATE);

        List<Video> videos = videoService.getAllPublic();

        assertEquals(1, videos.size());
        assertEquals("Public Video", videos.get(0).getTitle());
        assertEquals(VideoVisibility.PUBLIC, videos.get(0).getVisibility());
    }

    @Test
    void getPublicRejectsPrivateVideo() {
        Channel channel = saveChannel("creator-private", "private-channel");
        Video privateVideo = saveVideo(channel, "Hidden Video", VideoVisibility.PRIVATE);

        assertThrows(NotFoundException.class, () -> videoService.getPublic(privateVideo.getId()));
    }

    private Channel saveChannel(String username, String channelName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setDisplayName(username);

        UserCredentials credentials = new UserCredentials();
        credentials.setPasswordHash("$2a$10$dummyDummyDummyDummyDummyDummyDummyDummyDummyDummy");
        user.attachCredentials(credentials);

        User savedUser = userRepository.save(user);

        Channel channel = new Channel();
        channel.setName(channelName);
        channel.setUser(savedUser);

        return channelRepository.save(channel);
    }

    private Video saveVideo(Channel channel, String title, VideoVisibility visibility) {
        Video video = new Video();
        video.setTitle(title);
        video.setOwner(channel.getUser());
        video.setChannel(channel);
        video.setVisibility(visibility);
        return videoRepository.save(video);
    }
}
