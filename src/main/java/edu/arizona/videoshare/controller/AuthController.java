package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.user.LoginForm;
import edu.arizona.videoshare.dto.user.RegisterForm;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerForm") RegisterForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session
    ) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            User user = authService.register(form);

            session.setAttribute("loggedInUserId", user.getId());
            session.setAttribute("loggedInUsername", user.getUsername());
            session.setAttribute("loggedInDisplayName", user.getDisplayName());

            redirectAttributes.addFlashAttribute("successMessage", "Welcome to Video Share!");
            return "redirect:/";
        } catch (ConflictException ex) {
            String msg = ex.getMessage();

            if ("Username already exists".equals(msg)) {
                bindingResult.rejectValue("username", "duplicate", msg);
            } else if ("Email already exists".equals(msg)) {
                bindingResult.rejectValue("email", "duplicate", msg);
            } else {
                model.addAttribute("errorMessage", msg);
            }

            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginForm") LoginForm form,
            BindingResult bindingResult,
            HttpSession session
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            User user = authService.authenticate(form);

            session.setAttribute("loggedInUserId", user.getId());
            session.setAttribute("loggedInUsername", user.getUsername());
            session.setAttribute("loggedInDisplayName", user.getDisplayName());

            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("login.failed", "Invalid username/email or password");
            return "auth/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out.");
        return "redirect:/";
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }
}