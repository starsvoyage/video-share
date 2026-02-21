package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.reaction.ReactRequest;
import edu.arizona.videoshare.model.entity.Reaction;
import edu.arizona.videoshare.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping("/videos/{videoId}/reactions")
    public Reaction reactToVideo(@PathVariable Long videoId, @RequestBody ReactRequest req) {
        return reactionService.reactToVideo(videoId, req.getUserId(), req.getType());
    }

    @PostMapping("/comments/{commentId}/reactions")
    public Reaction reactToComment(@PathVariable Long commentId, @RequestBody ReactRequest req) {
        return reactionService.reactToComment(commentId, req.getUserId(), req.getType());
    }
}