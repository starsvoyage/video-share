package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.playlist.PlaylistAddVideoRequest;
import edu.arizona.videoshare.dto.playlist.PlaylistCreateRequest;
import edu.arizona.videoshare.dto.playlist.PlaylistResponse;
import edu.arizona.videoshare.exception.ForbiddenException;
import edu.arizona.videoshare.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import edu.arizona.videoshare.model.entity.Playlist;
import edu.arizona.videoshare.model.enums.Visibility;
import edu.arizona.videoshare.repository.PlaylistRepository;

import java.util.List;

/**
 * PlaylistController
 *
 * REST endpoints for Playlist + PlaylistVideo.
 *
 * Base path: /api/playlists
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService service;
    private final PlaylistRepository playlist;

    /**
     * POST /api/playlists
     * Create a playlist.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistResponse create(@Valid @RequestBody PlaylistCreateRequest req) {
        return PlaylistResponse.of(service.create(req));
    }

    /**
     * GET /api/playlists/{id}
     * Fetch a playlist by id.
     */
    @GetMapping("/{id}")
    public PlaylistResponse getById(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        Playlist play = service.getById(id);

        if (play.getVisibility() == Visibility.PRIVATE) {
            if (currentUserId == null || !play.getUser().getId().equals(currentUserId)) {
                throw new ForbiddenException("This playlist is private.");
            }
        }

        return PlaylistResponse.of(play);
    }

    /**
     * GET /api/playlists/user/{userId}
     * Fetch all playlists owned by a user.
     */
    @GetMapping("/user/{userId}")
    public List<PlaylistResponse> getByUser(@PathVariable Long userId) {
        List<Playlist> allPlayLists = playlist.findByUserId(userId);
        List<PlaylistResponse> filtered = new java.util.ArrayList<>();

        for (Playlist p : allPlayLists) {
            if (p.getVisibility() == Visibility.PUBLIC) {
                filtered.add(PlaylistResponse.of(p));
            }
        }

        return filtered;
    }

    /**
     * POST /api/playlists/{playlistId}/videos
     * Add one video to a playlist.
     */
    @PostMapping("/{playlistId}/videos")
    public PlaylistResponse addVideo(
            @PathVariable Long playlistId,
            @Valid @RequestBody PlaylistAddVideoRequest req
    ) {
        return PlaylistResponse.of(service.addVideo(playlistId, req));
    }

    /**
     * DELETE /api/playlists/{playlistId}/videos/{playlistVideoId}
     * Remove one playlist item.
     */
    @DeleteMapping("/{playlistId}/videos/{playlistVideoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeVideo(
            @PathVariable Long playlistId,
            @PathVariable Long playlistVideoId
    ) {
        service.removeItem(playlistId, playlistVideoId);
    }


    /**
     * PATCH /api/playlists/{playlistId}/videos/{playlistVideoId}/reorder
     * Change the position of a video in the playlist.
     */
    @PatchMapping("/{playlistId}/videos/{playlistVideoId}/reorder")
    public PlaylistResponse reorderVideo(
            @PathVariable Long playlistId,
            @PathVariable Long playlistVideoId,
            @RequestParam int newPosition
    ) {
        return PlaylistResponse.of(service.reorderItem(playlistId, playlistVideoId, newPosition));

    }
}
