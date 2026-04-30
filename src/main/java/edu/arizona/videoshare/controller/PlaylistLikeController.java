package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.service.PlaylistLikeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/playlists")
public class PlaylistLikeController {

    private final PlaylistLikeService service;

    @PostMapping("/{playlistId}/like")
    public Map<String, Object> toggleLike(@PathVariable Long playlistId, HttpSession session) {

        Long userId = (Long) session.getAttribute("loggedInUserId");

        boolean liked = service.toggleLike(userId, playlistId);
        long count = service.getLikeCount(playlistId);

        return Map.of(
                "liked", liked,
                "likeCount", count
        );
    }

    @GetMapping("/{playlistId}/like-status")
    public Map<String, Object> getStatus(@PathVariable Long playlistId, HttpSession session) {

        Long userId = (Long) session.getAttribute("loggedInUserId");

        return Map.of(
                "liked", service.isLiked(userId, playlistId),
                "likeCount", service.getLikeCount(playlistId)
        );
    }
}