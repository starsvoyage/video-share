package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.playlist.PlaylistAddVideoRequest;
import edu.arizona.videoshare.dto.playlist.PlaylistCreateRequest;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.exception.ForbiddenException;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Playlist;
import edu.arizona.videoshare.model.entity.PlaylistVideo;
import edu.arizona.videoshare.model.enums.Visibility;
import edu.arizona.videoshare.repository.PlaylistRepository;
import edu.arizona.videoshare.repository.PlaylistVideoRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * PlaylistService
 *
 * Implements the business use-cases for Playlists and PlaylistVideo.
 */
@RequiredArgsConstructor
@Service
public class PlaylistService {

    private final PlaylistRepository playlists;
    private final PlaylistVideoRepository playlistVideos;
    private final UserRepository users;
    private final VideoRepository videos;

    /** CREATE playlist */
    @Transactional
    public Playlist create(PlaylistCreateRequest req) {
        var owner = users.findById(req.userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + req.userId));

        Playlist p = new Playlist();
        p.setUser(owner);
        p.setName(req.name.trim());
        p.setDescription(req.description != null ? req.description.trim() : null);
        p.setVisibility(req.visibility != null ? req.visibility : Visibility.PRIVATE);

        Playlist saved = playlists.save(p);

        // Return with associations loaded (helps controllers safely build DTOs)
        return playlists.findWithItemsById(saved.getId()).orElse(saved);
    }

    /** READ playlist */
    @Transactional(readOnly = true)
    public Playlist getById(Long playlistId) {
        return playlists.findWithItemsById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found: " + playlistId));
    }

    /** READ playlists by user */
    @Transactional(readOnly = true)
    public List<Playlist> getByUser(Long userId) {
        if (!users.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        return playlists.findByUserId(userId);
    }

    /**
     * ADD video to playlist
     *
     * - If req.position is null: append
     * - If req.position is provided: insert and shift others
     */
    @Transactional
    public Playlist addVideo(Long playlistId, PlaylistAddVideoRequest req) {
        Playlist p = playlists.findWithItemsById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found: " + playlistId));

        var v = videos.findById(req.videoId)
                .orElseThrow(() -> new NotFoundException("Video not found: " + req.videoId));

        if (playlistVideos.findByPlaylistIdAndVideoId(playlistId, req.videoId).isPresent()) {
            throw new ConflictException("Video already exists in playlist");
        }

        int insertPos;
        int currentSize = p.getItems().size();

        if (req.position == null) {
            insertPos = currentSize;
        } else {
            insertPos = Math.max(0, Math.min(req.position, currentSize));
        }

        // Shift positions of existing items if inserting in the middle
        for (PlaylistVideo existing : p.getItems()) {
            if (existing.getPosition() >= insertPos) {
                existing.setPosition(existing.getPosition() + 1);
            }
        }

        PlaylistVideo pv = new PlaylistVideo();
        pv.setVideo(v);
        pv.setPosition(insertPos);
        p.addItem(pv);

        // Saving playlist cascades to PlaylistVideo because of cascade=ALL
        Playlist saved = playlists.save(p);
        return playlists.findWithItemsById(saved.getId()).orElse(saved);
    }

    public void removeVideo(Long userId, Long playlistId, Long videoId) {
        var playlist = getById(playlistId);

        if (!playlist.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You cannot modify this playlist");
        }

        var item = playlistVideos.findByPlaylistIdAndVideoId(playlistId, videoId)
                .orElseThrow(() -> new NotFoundException("Video is not in this playlist"));

        playlistVideos.delete(item);
    }

    @Transactional
    public void removeItem(Long playlistId, Long playlistVideoId) {
        Playlist p = playlists.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found: " + playlistId));

        PlaylistVideo pv = playlistVideos.findById(playlistVideoId)
                .orElseThrow(() -> new NotFoundException("PlaylistVideo not found: " + playlistVideoId));

        // Safety check: make sure the item belongs to that playlist
        if (!pv.getPlaylist().getId().equals(playlistId)) {
            throw new ConflictException("That playlist item does not belong to this playlist");
        }

        int removedPos = pv.getPosition();
        p.removeItem(pv);
        playlists.save(p);

        // Close the "gap" so ordering stays clean.
        List<PlaylistVideo> remaining = playlistVideos.findByPlaylistIdOrderByPositionAsc(playlistId);
        for (PlaylistVideo item : remaining) {
            if (item.getPosition() > removedPos) {
                item.setPosition(item.getPosition() - 1);
            }
        }
        playlistVideos.saveAll(remaining);
    }


    /**
     * REORDER a playlist item.
     */
    @Transactional
    public Playlist reorderItem(Long playlistId, Long playlistVideoId, int newPosition) {
        Playlist playList = playlists.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found: " + playlistId));

        PlaylistVideo playlistVideo = playlistVideos.findById(playlistVideoId)
                .orElseThrow(() -> new NotFoundException("PlaylistVideo not found: " + playlistVideoId));

        // Safety check: make sure the item belongs to that playlist
        if (!playlistVideo.getPlaylist().getId().equals(playlistId)) {
            throw new ConflictException("That playlist item does not belong to this playlist");
        }

        int currentPos = playlistVideo.getPosition();
        newPosition = Math.max(0, Math.min(newPosition, playList.getItems().size() - 1));

        if (currentPos == newPosition) {
            return playList;
        }

        // Shift positions of existing items
        for (PlaylistVideo existing : playList.getItems()) {
            if (existing.getPosition() >= Math.min(currentPos, newPosition) && existing.getPosition() <= Math.max(currentPos, newPosition)) {
                if (existing.getPosition() == currentPos) {
                    existing.setPosition(newPosition);
                } else {
                    existing.setPosition(existing.getPosition() + (currentPos < newPosition ? -1 : 1));
                }
            }
        }

        Playlist saved = playlists.save(playList);
        return playlists.findWithItemsById(saved.getId()).orElse(saved);
    }

}
