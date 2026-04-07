package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.playlist.PlaylistResponse;
import edu.arizona.videoshare.exception.ForbiddenException;
import edu.arizona.videoshare.model.enums.Visibility;
import edu.arizona.videoshare.service.PlaylistService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PlaylistPageController {

    private final PlaylistService playlistService;

    @GetMapping("/playlists/{playlistId}")
    public String showPlaylistPage(
            @PathVariable Long playlistId,
            HttpSession session,
            Model model) {

        PlaylistResponse playlist = PlaylistResponse.of(playlistService.getById(playlistId));
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (playlist.getVisibility() == Visibility.PRIVATE) {
            if (loggedInUserId == null || !loggedInUserId.equals(playlist.getUserId())) {
                throw new ForbiddenException("This playlist is private");
            }
        }

        model.addAttribute("playlist", playlist);
        model.addAttribute("loggedInUserId", loggedInUserId);

        return "playlist";
    }
}