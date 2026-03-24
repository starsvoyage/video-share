package edu.arizona.videoshare.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

import edu.arizona.videoshare.service.VideoService;
import edu.arizona.videoshare.model.entity.Video;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public edu.arizona.videoshare.model.entity.Video create(@RequestBody Video video) {
        return videoService.create(video);
    }

    @GetMapping("/{id}")
    public edu.arizona.videoshare.model.entity.Video get(@PathVariable Long id) {
        return videoService.getPublic(id);
    }

    @GetMapping
    public List<edu.arizona.videoshare.model.entity.Video> getAll() {
        return videoService.getAllPublic();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        videoService.delete(id);
    }
}
