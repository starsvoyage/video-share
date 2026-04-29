package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.membershipPlan.MembershipPlanResponse;
import edu.arizona.videoshare.dto.userMembership.UserMembershipResponse;
import edu.arizona.videoshare.service.MembershipPlanService;
import edu.arizona.videoshare.service.UserMembershipService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MembershipPageController {

    private final MembershipPlanService membershipPlanService;
    private final UserMembershipService userMembershipService;

    @GetMapping("/settings/membership")
    public String membershipPlansPage(
            @RequestParam(name = "section", defaultValue = "overview") String subSection,
            HttpSession session,
            Model model
    ) {
        Long userId = (Long) session.getAttribute("loggedInUserId");

        model.addAttribute("subSection", subSection);
        model.addAttribute("plans",
                membershipPlanService.getAllActivePlans()
                        .stream()
                        .map(MembershipPlanResponse::of)
                        .toList());

        if (userId != null) {
            try {
                model.addAttribute(
                        "currentMembership",
                        UserMembershipResponse.of(userMembershipService.getCurrentMembership(userId))
                );
            } catch (Exception ex) {
                model.addAttribute("currentMembership", null);
            }

            model.addAttribute(
                    "history",
                    userMembershipService.getMembershipHistory(userId)
                            .stream()
                            .map(UserMembershipResponse::of)
                            .toList()
            );
        } else {
            model.addAttribute("currentMembership", null);
            model.addAttribute("history", List.of());
        }

        return "settings/membership";
    }
}