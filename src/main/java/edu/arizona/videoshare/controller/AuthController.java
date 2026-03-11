package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.user.LoginForm;
import edu.arizona.videoshare.dto.user.RegisterForm;
import edu.arizona.videoshare.dto.user.VerifyCodeForm;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.service.AuthService;
import edu.arizona.videoshare.service.VerificationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AuthController (Presentation Layer)
 *
 * Handles authentication-related web pages and actions.
 * Provides endpoints for registration, login, logout,
 * and rendering the home page.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationService verificationService;

    /**
     * GET /register
     * Displays the user registration page.
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "auth/register";
    }

    /**
     * POST /register
     * Processes user registration and creates a new account.
     * On success, logs the user in and redirects to the home page.
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerForm") RegisterForm form,
            BindingResult bindingResult,
            Model model
    ) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            User user = authService.register(form);
            model.addAttribute("email", user.getEmail());
            model.addAttribute("verifyCodeForm", new VerifyCodeForm());
            return "auth/verify-account";

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

    /**
     * GET /login
     * Displays the login page.
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "auth/login";
    }

    /**
     * POST /login
     * Authenticates a user using username/email and password.
     * On success, creates a session and redirects to home.
     */
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

    /**
     * POST /logout
     * Logs the user out by invalidating the session.
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out.");
        return "redirect:/";
    }

    /**
     * GET /
     * Returns the application home page.
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token,
                                RedirectAttributes redirectAttributes) {
        try {
            verificationService.verifyByToken(token);
            redirectAttributes.addFlashAttribute("successMessage", "Account verified successfully. Please sign in.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/login";
        }
    }

    @PostMapping("/verify/resend")
    public String resendVerification(@RequestParam("email") String email,
                                     RedirectAttributes redirectAttributes) {
        try {
            verificationService.resendVerification(email);
            redirectAttributes.addFlashAttribute("successMessage", "Verification email sent.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/login";
    }

    @GetMapping("/verify/account")
    public String showVerifyAccountPage(Model model) {
        if (!model.containsAttribute("verifyCodeForm")) {
            model.addAttribute("verifyCodeForm", new VerifyCodeForm());
        }
        return "auth/verify-account";
    }

    @PostMapping("/verify/account")
    public String verifyAccountByCode(
            @Valid @ModelAttribute("verifyCodeForm") VerifyCodeForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/verify-account";
        }

        try {
            verificationService.verifyByCode(form.getCode());
            redirectAttributes.addFlashAttribute("successMessage", "Account verified successfully. Please sign in.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("code", "invalid", ex.getMessage());
            return "auth/verify-account";
        }
    }
}