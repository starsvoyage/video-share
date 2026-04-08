package edu.arizona.videoshare.service;

import java.util.List;

import edu.arizona.videoshare.dto.viewEvent.CreateViewEventRequest;
import edu.arizona.videoshare.dto.viewEvent.CreateViewEventResponse;
import edu.arizona.videoshare.dto.viewEvent.ViewEventResponse;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import edu.arizona.videoshare.model.entity.ViewEvent;
import edu.arizona.videoshare.repository.ViewEventRepository;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ViewEventService {
    private final ViewEventRepository viewEventRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public CreateViewEventResponse createViewEvent(CreateViewEventRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.getUserId()));

        Video video = videoRepository.findById(request.getVideoId())
                .orElseThrow(() -> new NotFoundException("Video not found: " + request.getVideoId()));

        ViewEvent viewEvent = viewEventRepository.findByUserIdAndVideoId(user.getId(), video.getId())
                .orElseGet(() -> {
                    ViewEvent newViewEvent = new ViewEvent();
                    newViewEvent.setUser(user);
                    newViewEvent.setVideo(video);
                    newViewEvent.setWatchDuration(0);
                    newViewEvent.setCompleted(false);
                    return newViewEvent;
                });

        viewEvent.setWatchedAt(LocalDateTime.now());

        ViewEvent saved = viewEventRepository.save(viewEvent);

        return toCreateResponse(saved);
    }

    @Transactional
    public ViewEventResponse updateWatchDuration(Long viewEventId, int watchDuration) {
        ViewEvent viewEvent = viewEventRepository.findById(viewEventId)
                .orElseThrow(() -> new NotFoundException("ViewEvent not found: " + viewEventId));

        viewEvent.setWatchDuration(watchDuration);

        Video video = viewEvent.getVideo();
        if (video.getDuration() > 0 && watchDuration >= video.getDuration()) {
            viewEvent.setCompleted(true);
        }

        ViewEvent saved = viewEventRepository.save(viewEvent);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ViewEvent> getUserHistory(Long userId) {
        return viewEventRepository.findByUserIdOrderByWatchedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<ViewEvent> getVideoViews(Long videoId) {
        return viewEventRepository.findByVideoId(videoId);
    }

    private CreateViewEventResponse toCreateResponse(ViewEvent ve) {
        return CreateViewEventResponse.builder()
                .id(ve.getId())
                .userId(ve.getUser().getId())
                .videoId(ve.getVideo().getId())
                .watchedAt(ve.getWatchedAt())
                .watchDuration(ve.getWatchDuration())
                .completed(ve.isCompleted())
                .build();
    }

    private ViewEventResponse toResponse(ViewEvent ve) {
        return ViewEventResponse.builder()
                .id(ve.getId())
                .userId(ve.getUser().getId())
                .videoId(ve.getVideo().getId())
                .watchedAt(ve.getWatchedAt())
                .watchDuration(ve.getWatchDuration())
                .completed(ve.isCompleted())
                .build();
    }
}