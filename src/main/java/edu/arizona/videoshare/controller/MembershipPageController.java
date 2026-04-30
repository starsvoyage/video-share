package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.membershipPlan.MembershipPlanResponse;
import edu.arizona.videoshare.dto.payment.PaymentInformationRequest;
import edu.arizona.videoshare.dto.userMembership.UserMembershipResponse;
import edu.arizona.videoshare.service.MembershipPlanService;
import edu.arizona.videoshare.service.UserMembershipService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import edu.arizona.videoshare.dto.userMembership.UserMembershipRequest;
import edu.arizona.videoshare.model.enums.PaymentType;
import edu.arizona.videoshare.service.PaymentInformationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MembershipPageController {

    private final MembershipPlanService membershipPlanService;
    private final UserMembershipService userMembershipService;
    private final PaymentInformationService paymentInformationService;

    @GetMapping("/settings/membership")
    public String membershipPlansPage(
            @RequestParam(name = "section", defaultValue = "overview") String subSection,
            HttpSession session,
            Model model
    ) {
        Long userId = (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("subSection", subSection);
        model.addAttribute("plans",
                membershipPlanService.getAllActivePlans()
                        .stream()
                        .map(MembershipPlanResponse::of)
                        .toList());

        if (userId != null) {
            try {
                UserMembershipResponse currentMembership =
                        UserMembershipResponse.of(userMembershipService.getCurrentMembership(userId));

                model.addAttribute("currentMembership", currentMembership);

                if (currentMembership.isAutoRenew() && currentMembership.getStartAt() != null) {
                    model.addAttribute("nextPaymentDate",
                            currentMembership.getStartAt().plusMonths(1));
                } else {
                    model.addAttribute("nextPaymentDate", null);
                }

            } catch (Exception ex) {
                model.addAttribute("currentMembership", null);
                model.addAttribute("nextPaymentDate", null);
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

    @GetMapping("/choose-plan")
    public String choosePlanPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("loggedInUserId");

        model.addAttribute("plans",
                membershipPlanService.getAllActivePlans()
                        .stream()
                        .map(MembershipPlanResponse::of)
                        .toList()
        );

        if (userId != null) {
            try {
                model.addAttribute(
                        "currentMembership",
                        UserMembershipResponse.of(userMembershipService.getCurrentMembership(userId))
                );
            } catch (Exception ex) {
                model.addAttribute("currentMembership", null);
            }
        } else {
            model.addAttribute("currentMembership", null);
        }

        return "choose-plan";
    }

    @GetMapping("/checkout/{planId}")
    public String checkoutPage(
            @PathVariable Long planId,
            HttpSession session,
            Model model
    ) {
        Long userId = (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("plan",
                MembershipPlanResponse.of(
                        membershipPlanService.getById(planId)
                )
        );

        model.addAttribute("paymentInformationRequest", new PaymentInformationRequest());

        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(
            @RequestParam Long membershipPlanId,
            @RequestParam String cardholderName,
            @RequestParam PaymentType paymentType,
            @RequestParam String cardNumber,
            @RequestParam String expirationDate,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            PaymentInformationRequest paymentRequest = new PaymentInformationRequest();
            paymentRequest.userId = userId;
            paymentRequest.cardholderName = cardholderName;
            paymentRequest.paymentType = paymentType;
            paymentRequest.cardNumber = cardNumber;
            paymentRequest.expirationDate = expirationDate;
            paymentRequest.defaultPaymentMethod = true;

            paymentInformationService.create(paymentRequest);

            UserMembershipRequest membershipRequest = new UserMembershipRequest();
            membershipRequest.setMembershipPlanId(membershipPlanId);
            membershipRequest.setAutoRenew(true);

            userMembershipService.subscribe(userId, membershipRequest);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Membership started successfully."
            );

            return "redirect:/";

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Checkout failed: " + ex.getMessage()
            );

            return "redirect:/checkout/" + membershipPlanId;
        }
    }
    @PostMapping("/membership/auto-renew")
    public String updateAutoRenew(
            @RequestParam Boolean autoRenew,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = (Long) session.getAttribute("loggedInUserId");

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            userMembershipService.updateCurrentAutoRenew(userId, autoRenew);
            redirectAttributes.addFlashAttribute("successMessage", "Auto-renew updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/settings/membership";
    }

}