package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Playlist;
import edu.arizona.videoshare.model.entity.PlaylistLike;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.enums.NotificationType;
import edu.arizona.videoshare.model.enums.SourceType;
import edu.arizona.videoshare.repository.PlaylistLikeRepository;
import edu.arizona.videoshare.repository.PlaylistRepository;
import edu.arizona.videoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaylistLikeService {

    private final PlaylistLikeRepository repository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final NotificationService notificationService;

    @Transactional
    public boolean toggleLike(Long userId, Long playlistId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found"));

        return repository.findByUserIdAndPlaylistId(userId, playlistId)
                .map(existing -> {
                    repository.delete(existing);
                    return false;
                })
                .orElseGet(() -> {
                    PlaylistLike like = new PlaylistLike();
                    like.setUser(user);
                    like.setPlaylist(playlist);
                    repository.save(like);

                    notificationService.notify(
                            playlist.getUser(),
                            user,
                            NotificationType.LIKE_PLAYLIST,
                            SourceType.PLAYLIST,
                            user.getDisplayName() + " liked your playlist: " + playlist.getName(),
                            "/playlists/" + playlist.getId()
                    );

                    return true;
                });
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long playlistId) {
        return repository.countByPlaylistId(playlistId);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, Long playlistId) {
        return repository.findByUserIdAndPlaylistId(userId, playlistId).isPresent();
    }
}