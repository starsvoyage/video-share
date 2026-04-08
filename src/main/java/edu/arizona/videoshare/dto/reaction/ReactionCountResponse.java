package edu.arizona.videoshare.dto.reaction;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionCountResponse {
    private long likes;
    private long dislikes;
    private Long videoId;
    private Long commentId;
}