package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Video;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import edu.arizona.videoshare.repository.ChannelRepository;
import edu.arizona.videoshare.repository.VideoRepository;


@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final ChannelRepository channelRepository;

    public Video create(Long channelId, MultipartFile file, String title) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new NotFoundException("Channel not found"));
        Video video = new Video();
        if (title == null || title.isEmpty()) {
            video.setTitle("Untitled Video");
        } else {
            video.setTitle(title);
        }
        video.setChannel(channel);

        //Saving file to local storage
        String fileName = "video_" + System.currentTimeMillis() + ".mp4";
        String filePath = "uploads/videos/" + fileName;
        video.setFilePath(filePath);

        try {
            //Create directory if needed
            java.io.File dir = new java.io.File("uploads/videos");
            if (!dir.exists()) dir.mkdirs();
            
            // Save the file
            Files.copy(file.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

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
