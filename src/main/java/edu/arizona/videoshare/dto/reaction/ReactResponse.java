package edu.arizona.videoshare.dto.reaction;

import edu.arizona.videoshare.model.enums.ReactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactResponse {
    private Long id;
    private Long userId;
    private Long videoId;
    private Long commentId;
    private ReactionType type;
    private LocalDateTime createdAt;
}