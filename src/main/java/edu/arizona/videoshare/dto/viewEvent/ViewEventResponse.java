package edu.arizona.videoshare.dto.viewEvent;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ViewEventResponse {
    private Long id;
    private Long userId;
    private Long videoId;
    private LocalDateTime watchedAt;
    private int watchDuration;
    private boolean completed;
}
