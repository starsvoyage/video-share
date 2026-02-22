package edu.arizona.videoshare.dto.playlist;

import edu.arizona.videoshare.model.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * PlaylistCreateRequest DTO
 *
 * Incoming payload for creating a playlist.
 */
@Data
public class PlaylistCreateRequest {

    /** Owner user id */
    @NotNull
    public Long userId;

    @NotBlank
    @Size(max = 120)
    public String name;

    @Size(max = 500)
    public String description;

    /** Optional: defaults to PRIVATE if not provided */
    public Visibility visibility;
}
