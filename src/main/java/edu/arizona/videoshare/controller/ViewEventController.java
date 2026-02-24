package edu.arizona.videoshare.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.arizona.videoshare.model.entity.ViewEvent;
import edu.arizona.videoshare.service.ViewEventService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/views")
@RequiredArgsConstructor
public class ViewEventController {

    private final ViewEventService viewEventService;

    @PostMapping
    public ViewEvent create(@RequestBody ViewEvent viewEvent) {
        return viewEventService.createViewEvent(viewEvent);
    }

    @GetMapping("/user/{userId}")
    public List<ViewEvent> getUserHistory(@PathVariable Long userId) {
        return viewEventService.getUserHistory(userId);
    }

    @GetMapping("/video/{videoId}")
    public List<ViewEvent> getVideoViews(@PathVariable Long videoId) {
        return viewEventService.getVideoViews(videoId);
    }
}
