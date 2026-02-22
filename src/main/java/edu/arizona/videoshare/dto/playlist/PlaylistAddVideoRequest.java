package edu.arizona.videoshare.dto.playlist;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * PlaylistAddVideoRequest
 *
 * Add one existing Video to one existing Playlist.
 */
@Data
public class PlaylistAddVideoRequest {

    @NotNull
    public Long videoId;

    /**
     * Where to insert. If null, we append to the end.
     */
    public Integer position;
}
