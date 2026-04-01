package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.user.AccountInformationForm;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.service.UserService;
import edu.arizona.videoshare.dto.user.ChangePasswordForm;
import edu.arizona.videoshare.service.AuthService;
import edu.arizona.videoshare.service.VerificationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final UserService userService;
    private final AuthService authService;
    private final VerificationService verificationService;

    private boolean notLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInUserId") == null;
    }

    @GetMapping("/settings")
    public String settingsRoot() {
        return "redirect:/settings/account";
    }

    @GetMapping("/settings/account")
    public String accountSettings(
            HttpSession session,
            Model model,
            @RequestParam(name = "section", defaultValue = "overview") String section) {

        if (session.getAttribute("loggedInUserId") == null) {
            return "redirect:/login";
        }

        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");
        User user = userService.getById(loggedInUserId);

        model.addAttribute("activeSection", "account");
        model.addAttribute("subSection", section);
        model.addAttribute("user", user);

        AccountInformationForm form = new AccountInformationForm();
        form.setDisplayName(user.getDisplayName());
        form.setBio(user.getBio());
        model.addAttribute("accountInformationForm", form);
        model.addAttribute("changePasswordForm", new ChangePasswordForm());

        return "settings/account";
    }

    @PostMapping("/settings/account-information")
    public String updateAccountInformation(
            HttpSession session,
            @Valid @ModelAttribute("accountInformationForm") AccountInformationForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (session.getAttribute("loggedInUserId") == null) {
            return "redirect:/login";
        }

        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");

        if (bindingResult.hasErrors()) {
            User user = userService.getById(loggedInUserId);
            model.addAttribute("activeSection", "account");
            model.addAttribute("subSection", "account-information");
            model.addAttribute("user", user);
            return "settings/account";
        }

        try {
            User updatedUser = userService.updateAccountInformation(
                    loggedInUserId,
                    form.getDisplayName(),
                    form.getBio(),
                    form.getAvatar()
            );

            session.setAttribute("loggedInAvatarUrl", updatedUser.getAvatarUrl());
            session.setAttribute("loggedInUsername", updatedUser.getUsername());

            redirectAttributes.addFlashAttribute("successMessage", "Account information updated successfully.");
            return "redirect:/settings/account?section=account-information";

        } catch (IllegalArgumentException ex) {
            User user = userService.getById(loggedInUserId);
            model.addAttribute("activeSection", "account");
            model.addAttribute("subSection", "account-information");
            model.addAttribute("user", user);
            model.addAttribute("uploadError", ex.getMessage());
            return "settings/account";

        } catch (Exception ex) {
            User user = userService.getById(loggedInUserId);
            model.addAttribute("activeSection", "account");
            model.addAttribute("subSection", "account-information");
            model.addAttribute("user", user);
            model.addAttribute("uploadError", "Failed to update account information.");
            return "settings/account";
        }
    }

    @PostMapping("/settings/resend-verification")
    public String resendVerificationForLoggedInUser(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.getById(userId);

            verificationService.resendVerification(user.getEmail());

            redirectAttributes.addFlashAttribute("successMessage", "Verification email sent. Enter the code below.");

            return "redirect:/verify/account";

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/settings/account?section=account-information";
        }
    }

    @PostMapping("/settings/change-password")
    public String changePassword(
            HttpSession session,
            @Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (session.getAttribute("loggedInUserId") == null) {
            return "redirect:/login";
        }

        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");
        User user = userService.getById(loggedInUserId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("activeSection", "account");
            model.addAttribute("subSection", "change-password");
            model.addAttribute("user", user);

            AccountInformationForm accountForm = new AccountInformationForm();
            accountForm.setDisplayName(user.getDisplayName());
            accountForm.setBio(user.getBio());
            model.addAttribute("accountInformationForm", accountForm);

            return "settings/account";
        }

        try {
            authService.changePassword(
                    loggedInUserId,
                    form.getCurrentPassword(),
                    form.getNewPassword(),
                    form.getConfirmNewPassword()
            );

            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
            return "redirect:/settings/account?section=change-password";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("activeSection", "account");
            model.addAttribute("subSection", "change-password");
            model.addAttribute("user", user);
            model.addAttribute("passwordError", ex.getMessage());

            AccountInformationForm accountForm = new AccountInformationForm();
            accountForm.setDisplayName(user.getDisplayName());
            accountForm.setBio(user.getBio());
            model.addAttribute("accountInformationForm", accountForm);

            return "settings/account";
        }
    }

    @PostMapping("/settings/deactivate")
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

    @GetMapping("/settings/premium")
    public String premiumSettings(HttpSession session, Model model) {
        if (notLoggedIn(session)) return "redirect:/login";
        model.addAttribute("activeSection", "premium");
        return "settings/premium";
    }

    @GetMapping("/settings/followed-channels")
    public String followedChannelsSettings(HttpSession session, Model model) {
        if (notLoggedIn(session)) return "redirect:/login";
        model.addAttribute("activeSection", "followed-channels");
        return "settings/followed-channels";
    }

    @GetMapping("/settings/notifications")
    public String notificationsSettings(HttpSession session, Model model) {
        if (notLoggedIn(session)) return "redirect:/login";
        model.addAttribute("activeSection", "notifications");
        return "settings/notifications";
    }
}