package edu.arizona.videoshare.dto.comment;

import edu.arizona.videoshare.model.enums.CommentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private Long userId;
    private String username;
    private String displayName;
    private Long videoId;
    private Long parentId;
    private String content;
    private CommentStatus status;
    private long likeCount;
    private long dislikeCount;
    private int replyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
