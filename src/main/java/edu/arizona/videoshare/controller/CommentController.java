package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.comment.CommentResponse;
import edu.arizona.videoshare.dto.comment.CreateCommentRequest;
import edu.arizona.videoshare.dto.comment.CreateCommentResponse;
import edu.arizona.videoshare.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/videos/{videoId}/comments")
public class CommentController {

        private final CommentService commentService;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public CreateCommentResponse create(@PathVariable Long videoId,
                                            @Valid @RequestBody CreateCommentRequest req) {
                return commentService.addComment(videoId, req.getUserId(), req.getContent(), req.getParentId());
        }

        @DeleteMapping("/{commentId}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable Long videoId, @PathVariable Long commentId) {
                commentService.removeComment(commentId);
        }

        @GetMapping
        public List<CommentResponse> getComments(@PathVariable Long videoId) {
                return commentService.getTopLevelComments(videoId);
        }

        @GetMapping("/{commentId}/replies")
        public List<CommentResponse> getReplies(
                @PathVariable Long videoId,
                @PathVariable Long commentId) {
                return commentService.getReplies(videoId, commentId);
        }
}