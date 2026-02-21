package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.reaction.ReactRequest;
import edu.arizona.videoshare.dto.reaction.ReactResponse;
import edu.arizona.videoshare.service.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping("/videos/{videoId}/reactions")
    public ReactResponse reactToVideo(@PathVariable Long videoId,
            @Valid @RequestBody ReactRequest req) {
        return reactionService.reactToVideo(videoId, req.getUserId(), req.getType());
    }

    @PostMapping("/comments/{commentId}/reactions")
    public ReactResponse reactToComment(@PathVariable Long commentId,
            @Valid @RequestBody ReactRequest req) {
        return reactionService.reactToComment(commentId, req.getUserId(), req.getType());
    }
}