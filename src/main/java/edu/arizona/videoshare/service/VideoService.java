package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Video;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import edu.arizona.videoshare.repository.VideoRepository;


@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    public Video create(Video video) {
        return videoRepository.save(video);
    }

    public Video get(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Video not found"));
    }

    public List<Video> getAll() {
        return videoRepository.findAll();
    }

    public void delete(Long id) {
        videoRepository.deleteById(id);
    }
}
