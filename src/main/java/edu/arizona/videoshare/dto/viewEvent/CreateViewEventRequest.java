package edu.arizona.videoshare.dto.viewEvent;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateViewEventRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long videoId;
}
