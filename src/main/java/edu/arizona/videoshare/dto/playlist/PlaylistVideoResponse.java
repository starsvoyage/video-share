package edu.arizona.videoshare.dto.playlist;

import edu.arizona.videoshare.model.entity.PlaylistVideo;
import lombok.Data;

/**
 * PlaylistVideoResponse
 *
 * Represents one row in playlist_videos.
 */
@Data
public class PlaylistVideoResponse {
    public Long id;
    public int position;
    public Long videoId;
    public String videoTitle;

    public static PlaylistVideoResponse of(PlaylistVideo pv) {
        var r = new PlaylistVideoResponse();
        r.id = pv.getId();
        r.position = pv.getPosition();
        r.videoId = pv.getVideo().getId();
        r.videoTitle = pv.getVideo().getTitle();
        return r;
    }
}