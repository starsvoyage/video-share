package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.playlist.PlaylistAddVideoRequest;
import edu.arizona.videoshare.dto.playlist.PlaylistCreateRequest;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Playlist;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.UserCredentials;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.VideoVisibility;
import edu.arizona.videoshare.model.enums.Visibility;
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
class PlaylistServiceTest {

    @Autowired PlaylistService playlistService;
    @Autowired UserRepository userRepository;
    @Autowired ChannelRepository channelRepository;
    @Autowired VideoRepository videoRepository;

    @Test
    void addVideoAllowsVideosFromOtherCreatorsChannels() {
        Channel playlistOwnerChannel = saveChannel("playlist-owner", "owner-channel");
        Channel guestChannel = saveChannel("guest-creator", "guest-channel");

        PlaylistCreateRequest createRequest = new PlaylistCreateRequest();
        createRequest.userId = playlistOwnerChannel.getUser().getId();
        createRequest.name = "Mixed Creator Picks";
        createRequest.visibility = Visibility.PUBLIC;

        Playlist playlist = playlistService.create(createRequest);
        Video guestVideo = saveVideo(guestChannel, "Guest Feature", VideoVisibility.PUBLIC);

        PlaylistAddVideoRequest addRequest = new PlaylistAddVideoRequest();
        addRequest.videoId = guestVideo.getId();

        Playlist updated = playlistService.addVideo(playlist.getId(), addRequest);

        assertEquals(1, updated.getItems().size());
        assertEquals(guestVideo.getId(), updated.getItems().get(0).getVideo().getId());
        assertEquals(guestChannel.getId(), updated.getItems().get(0).getVideo().getChannel().getId());
    }

    @Test
    void getPublicByUserReturnsOnlyPublicPlaylists() {
        Channel channel = saveChannel("playlist-publics", "public-channel");

        savePlaylist(channel.getUser().getId(), "Public List", Visibility.PUBLIC);
        savePlaylist(channel.getUser().getId(), "Private List", Visibility.PRIVATE);

        List<Playlist> playlists = playlistService.getPublicByUser(channel.getUser().getId());

        assertEquals(1, playlists.size());
        assertEquals("Public List", playlists.get(0).getName());
        assertEquals(Visibility.PUBLIC, playlists.get(0).getVisibility());
    }

    private Playlist savePlaylist(Long userId, String name, Visibility visibility) {
        PlaylistCreateRequest request = new PlaylistCreateRequest();
        request.userId = userId;
        request.name = name;
        request.visibility = visibility;
        return playlistService.create(request);
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
