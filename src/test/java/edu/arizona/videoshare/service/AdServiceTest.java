package edu.arizona.videoshare.service;

import edu.arizona.videoshare.model.entity.Ad;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.UserCredentials;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.repository.AdRepository;
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

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AdServiceTest {

    @Autowired AdService adService;
    @Autowired AdRepository adRepository;
    @Autowired UserRepository userRepository;
    @Autowired ChannelRepository channelRepository;
    @Autowired VideoRepository videoRepository;

    @Test
    void getByUserIdReturnsOnlyAdsForThatUsersVideos() {
        Channel targetChannel = saveChannel("ad-owner", "ad-owner-channel");
        Channel otherChannel = saveChannel("other-owner", "other-owner-channel");

        Video targetVideo = saveVideo(targetChannel, "Target Video");
        Video otherVideo = saveVideo(otherChannel, "Other Video");

        saveAd(targetVideo, "Target Ad");
        saveAd(otherVideo, "Other Ad");

        List<Ad> ads = adService.getByUserId(targetChannel.getUser().getId());

        assertEquals(1, ads.size());
        assertEquals("Target Ad", ads.get(0).getTitle());
        assertEquals(targetVideo.getId(), ads.get(0).getVideo().getId());
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

    private Video saveVideo(Channel channel, String title) {
        Video video = new Video();
        video.setTitle(title);
        video.setOwner(channel.getUser());
        video.setChannel(channel);
        video.setVisibility(VideoVisibility.PUBLIC);
        return videoRepository.save(video);
    }

    private Ad saveAd(Video video, String title) {
        Ad ad = new Ad();
        ad.setTitle(title);
        ad.setMediaUrl("https://example.com/" + title.replace(" ", "-").toLowerCase());
        ad.setDuration(15);
        ad.setActive(true);
        ad.setVideo(video);
        return adRepository.save(ad);
    }
}
