package edu.arizona.videoshare.dto.reaction;

import edu.arizona.videoshare.model.entity.ReactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactRequest {
    private Long userId;
    private ReactionType type;
}
