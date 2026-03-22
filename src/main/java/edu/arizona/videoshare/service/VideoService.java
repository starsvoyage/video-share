package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.Video;

import edu.arizona.videoshare.repository.ChannelRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import edu.arizona.videoshare.repository.VideoRepository;


@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final ChannelRepository channelRepository;

    public Video create(Video video) {
        if (video.getChannel() == null || video.getChannel().getId() == null) {
            throw new NotFoundException("Channel is required");
        }

        Channel channel = channelRepository.findById(video.getChannel().getId())
                .orElseThrow(() -> new NotFoundException(
                        "Channel not found: " + video.getChannel().getId()));

        video.setChannel(channel);
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
