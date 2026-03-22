package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.repository.ChannelRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final ChannelRepository channelRepository;

    @ModelAttribute("channels")
    public List<Channel> channels(HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return Collections.emptyList();
        }

        return channelRepository.findByUserId(loggedInUserId);
    }
}