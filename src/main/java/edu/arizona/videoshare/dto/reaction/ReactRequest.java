package edu.arizona.videoshare.dto.reaction;

import edu.arizona.videoshare.model.enums.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactRequest {

    @NotNull
    private Long userId;

    @NotNull
    private ReactionType type;
}