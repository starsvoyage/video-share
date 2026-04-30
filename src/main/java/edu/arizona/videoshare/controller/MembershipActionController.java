package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.userMembership.UserMembershipRequest;
import edu.arizona.videoshare.service.UserMembershipService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MembershipActionController {

    private final UserMembershipService userMembershipService;

    @PostMapping("/membership/subscribe")
    public String subscribe(@RequestParam Long membershipPlanId,
                            @RequestParam(defaultValue = "true") Boolean autoRenew,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("loggedInUserId");

        UserMembershipRequest req = new UserMembershipRequest();
        req.setMembershipPlanId(membershipPlanId);
        req.setAutoRenew(autoRenew);

        try {
            userMembershipService.subscribe(userId, req);
            redirectAttributes.addFlashAttribute("successMessage", "Membership updated successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/settings/membership?section=available-plans";
    }

    @PostMapping("/membership/cancel")
    public String cancel(HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("loggedInUserId");

        try {
            userMembershipService.cancelMembershipByUserId(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Membership canceled successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/settings/membership";
    }
}