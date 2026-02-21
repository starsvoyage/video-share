package edu.arizona.videoshare.dto.comment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequest {
    private Long userId;
    private String content;
    private Long parentId;
}
