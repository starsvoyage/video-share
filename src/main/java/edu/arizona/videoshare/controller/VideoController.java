package edu.arizona.videoshare.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import edu.arizona.videoshare.service.VideoService;
import edu.arizona.videoshare.model.Video;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor

public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public Video create(@RequestBody Video video) {
        return videoService.create(video);
    }

    @GetMapping("/{id}")
    public Video get(@PathVariable Long id) {
        return videoService.get(id);
    }

    @GetMapping
    public List<Video> getAll() {
        return videoService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        videoService.delete(id);
    }
}