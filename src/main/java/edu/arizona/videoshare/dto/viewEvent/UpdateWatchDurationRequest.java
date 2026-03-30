package edu.arizona.videoshare.dto.viewEvent;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UpdateWatchDurationRequest {
    @NotNull
    @Min(0)
    private Integer watchDuration;
}
