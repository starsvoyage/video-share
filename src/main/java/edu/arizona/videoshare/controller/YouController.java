package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.service.ChannelService;
import edu.arizona.videoshare.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class YouController {

    private final UserService userService;
    private final ChannelService channelService;

    @GetMapping("/you")
    public String showYouPage(HttpSession session, Model model) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            return "redirect:/login";
        }

        User user = userService.getById(loggedInUserId);
        List<Channel> channels = channelService.getChannelsByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("channels", channels);
        model.addAttribute("isVerified", user.getStatus() == UserStatus.ACTIVE);
        model.addAttribute("canCreateChannel", user.getStatus() == UserStatus.ACTIVE);
        model.addAttribute("isCreator", user.getRole() == UserRole.CREATOR);

        return "you";
    }
}