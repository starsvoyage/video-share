package edu.arizona.videoshare.dto.subscription;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SubscribeRequest {
    @NotNull
    private Long subscriberId;

    @NotNull
    private Long channelId;
}
