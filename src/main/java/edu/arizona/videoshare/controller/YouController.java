package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.model.entity.Channel;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.service.ChannelService;
import edu.arizona.videoshare.service.UserService;
import edu.arizona.videoshare.service.ViewEventService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class YouController {

    private final UserService userService;
    private final ChannelService channelService;
    private final ViewEventService viewEventService;

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
        model.addAttribute("history", viewEventService.getUserHistory(user.getId()));
        model.addAttribute("isVerified", user.getStatus() == UserStatus.ACTIVE);
        model.addAttribute("canCreateChannel", user.getStatus() == UserStatus.ACTIVE);
        model.addAttribute("isCreator", user.getRole() == UserRole.CREATOR);

        return "you";
    }

    @PostMapping("/you/deactivate")
    public String deactivateAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be signed in to deactivate your account.");
            return "redirect:/login";
        }

        userService.deactivate(loggedInUserId);
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Your account has been deactivated.");
        return "redirect:/";
    }
}
