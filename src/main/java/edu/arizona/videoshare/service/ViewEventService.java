package edu.arizona.videoshare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.arizona.videoshare.model.entity.ViewEvent;
import edu.arizona.videoshare.repository.ViewEventRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViewEventService {

    private final ViewEventRepository viewEventRepository;

    public ViewEvent createViewEvent(ViewEvent viewEvent) {
        return viewEventRepository.save(viewEvent);
    }

    public List<ViewEvent> getUserHistory(Long userId) {
        return viewEventRepository.findByUserId(userId);
    }

    public List<ViewEvent> getVideoViews(Long videoId) {
        return viewEventRepository.findByVideoId(videoId);
    }
}