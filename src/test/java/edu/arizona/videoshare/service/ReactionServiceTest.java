package edu.arizona.videoshare.service;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Playlist;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.UserCredentials;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.ReactionType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ReactionServiceTest {

    @Autowired ReactionService reactionService;
    @Autowired PlaylistService playlistService;
    @Autowired UserRepository userRepository;
    @Autowired ChannelRepository channelRepository;
    @Autowired VideoRepository videoRepository;

    @Test
    void reactToVideoCreatesLikedVideosPlaylistAndAddsVideo() {
        User liker = saveUser("liked-playlist-user");
        Channel ownerChannel = saveChannel("liked-playlist-owner", "owner-channel");
        Video video = saveVideo(ownerChannel, "Liked Video", VideoVisibility.PUBLIC);

        reactionService.reactToVideo(video.getId(), liker.getId(), ReactionType.LIKE);

        Playlist likedVideos = playlistService.findLikedVideosPlaylist(liker.getId()).orElse(null);

        assertNotNull(likedVideos);
        assertEquals(PlaylistService.LIKED_VIDEOS_PLAYLIST_NAME, likedVideos.getName());
        assertEquals(Visibility.PRIVATE, likedVideos.getVisibility());
        assertEquals(1, likedVideos.getItems().size());
        assertEquals(video.getId(), likedVideos.getItems().get(0).getVideo().getId());
    }

    @Test
    void removingLikeRemovesVideoFromLikedVideosPlaylist() {
        User liker = saveUser("liked-playlist-toggle");
        Channel ownerChannel = saveChannel("liked-toggle-owner", "toggle-owner-channel");
        Video video = saveVideo(ownerChannel, "Toggle Like Video", VideoVisibility.PUBLIC);

        reactionService.reactToVideo(video.getId(), liker.getId(), ReactionType.LIKE);
        reactionService.reactToVideo(video.getId(), liker.getId(), ReactionType.LIKE);

        Playlist likedVideos = playlistService.findLikedVideosPlaylist(liker.getId()).orElse(null);

        assertNotNull(likedVideos);
        assertTrue(likedVideos.getItems().isEmpty());
    }

    @Test
    void dislikingAfterLikeRemovesVideoFromLikedVideosPlaylist() {
        User liker = saveUser("liked-playlist-dislike");
        Channel ownerChannel = saveChannel("liked-dislike-owner", "dislike-owner-channel");
        Video video = saveVideo(ownerChannel, "Disliked Video", VideoVisibility.PUBLIC);

        reactionService.reactToVideo(video.getId(), liker.getId(), ReactionType.LIKE);
        reactionService.reactToVideo(video.getId(), liker.getId(), ReactionType.DISLIKE);

        Playlist likedVideos = playlistService.findLikedVideosPlaylist(liker.getId()).orElse(null);

        assertNotNull(likedVideos);
        assertTrue(likedVideos.getItems().isEmpty());
    }

    private User saveUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setDisplayName(username);

        UserCredentials credentials = new UserCredentials();
        credentials.setPasswordHash("$2a$10$dummyDummyDummyDummyDummyDummyDummyDummyDummyDummy");
        user.attachCredentials(credentials);

        return userRepository.save(user);
    }

    private Channel saveChannel(String username, String channelName) {
        User user = saveUser(username);

        Channel channel = new Channel();
        channel.setName(channelName);
        channel.setUser(user);

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
