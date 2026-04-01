package edu.arizona.videoshare.dto.viewEvent;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateViewEventResponse {
    private Long id;
    private Long userId;
    private Long videoId;
    private LocalDateTime watchedAt;
    private int watchDuration;
    private boolean completed;
}
