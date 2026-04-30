package edu.arizona.videoshare.dto.userMembership;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaybackAccessResponse {
    private boolean allowed;
    private String message;
    private String planCode;
    private boolean adFree;
    private boolean hd4KPlayback;
}