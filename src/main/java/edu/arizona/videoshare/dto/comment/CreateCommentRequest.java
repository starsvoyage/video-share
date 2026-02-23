package edu.arizona.videoshare.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequest {

    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 2000)
    private String content;

    private Long parentId;
}