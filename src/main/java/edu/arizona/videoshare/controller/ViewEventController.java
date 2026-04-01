package edu.arizona.videoshare.controller;

import java.util.List;

import edu.arizona.videoshare.dto.viewEvent.CreateViewEventRequest;
import edu.arizona.videoshare.dto.viewEvent.CreateViewEventResponse;
import edu.arizona.videoshare.dto.viewEvent.UpdateWatchDurationRequest;
import edu.arizona.videoshare.dto.viewEvent.ViewEventResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import edu.arizona.videoshare.model.entity.ViewEvent;
import edu.arizona.videoshare.service.ViewEventService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/views")
@RequiredArgsConstructor
public class ViewEventController {

    private final ViewEventService viewEventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateViewEventResponse create(@Valid @RequestBody CreateViewEventRequest request) {
        return viewEventService.createViewEvent(request);
    }

    @PatchMapping("/{id}/duration")
    public ViewEventResponse updateDuration(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWatchDurationRequest request) {
        return viewEventService.updateWatchDuration(id, request.getWatchDuration());
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
