package edu.arizona.videoshare.dto.playlist;

import edu.arizona.videoshare.model.entity.Playlist;
import edu.arizona.videoshare.model.enums.Visibility;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PlaylistResponse
 *
 * Outbound DTO for a playlist + its items.
 */
@Data
public class PlaylistResponse {
    public Long id;
    public Long userId;
    public String name;
    public String description;
    public Visibility visibility;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public List<PlaylistVideoResponse> items;

    public static PlaylistResponse of(Playlist p) {
        var r = new PlaylistResponse();
        r.id = p.getId();
        r.userId = p.getUser().getId();
        r.name = p.getName();
        r.description = p.getDescription();
        r.visibility = p.getVisibility();
        r.createdAt = p.getCreatedAt();
        r.updatedAt = p.getUpdatedAt();
        r.items = p.getItems().stream().map(PlaylistVideoResponse::of).toList();
        return r;
    }
}
