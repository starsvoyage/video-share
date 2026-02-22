package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.playlist.PlaylistAddVideoRequest;
import edu.arizona.videoshare.dto.playlist.PlaylistCreateRequest;
import edu.arizona.videoshare.dto.playlist.PlaylistResponse;
import edu.arizona.videoshare.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public PlaylistResponse getById(@PathVariable Long id) {
        return PlaylistResponse.of(service.getById(id));
    }

    /**
     * GET /api/playlists/user/{userId}
     * Fetch all playlists owned by a user.
     */
    @GetMapping("/user/{userId}")
    public List<PlaylistResponse> getByUser(@PathVariable Long userId) {
        return service.getByUser(userId).stream().map(PlaylistResponse::of).toList();
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
}
