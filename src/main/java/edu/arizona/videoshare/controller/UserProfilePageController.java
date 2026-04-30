package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.service.ChannelService;
import edu.arizona.videoshare.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserProfilePageController {

    private final UserService userService;
    private final ChannelService channelService;

    @GetMapping("/users/{username}")
    public String showUserProfile(
            @PathVariable String username,
            HttpSession session,
            Model model
    ) {
        User user = userService.getByUsername(username);
        List<Channel> channels = channelService.getChannelsByUserId(user.getId());

        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        model.addAttribute("profileUser", user);
        model.addAttribute("profileChannels", channels);
        model.addAttribute("loggedInUserId", loggedInUserId);

        return "user-profile";
    }
}