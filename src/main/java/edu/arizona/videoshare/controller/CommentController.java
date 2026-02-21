package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.comment.CreateCommentRequest;
import edu.arizona.videoshare.model.entity.Comment;
import edu.arizona.videoshare.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/videos/{videoId}/comments")
public class CommentController {

        private final CommentService commentService;

        @PostMapping
        public Comment create(@PathVariable Long videoId, @RequestBody CreateCommentRequest req) {
                return commentService.addComment(videoId, req.getUserId(), req.getContent(), req.getParentId());
        }

        @DeleteMapping("/{commentId}")
        public void delete(@PathVariable Long videoId, @PathVariable Long commentId) {
                commentService.removeComment(commentId);
        }
}