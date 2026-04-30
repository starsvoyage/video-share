package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.membershipPlan.MembershipPlanResponse;
import edu.arizona.videoshare.dto.payment.PaymentInformationRequest;
import edu.arizona.videoshare.dto.payment.PaymentInformationResponse;

import edu.arizona.videoshare.service.PaymentInformationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.arizona.videoshare.model.enums.PaymentType;

@Controller
@RequiredArgsConstructor
@RequestMapping("/settings/payment-method")
public class PaymentMethodPageController {

    private final PaymentInformationService paymentInformationService;

    @GetMapping
    public String paymentMethodPage(HttpSession session, Model model) {
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        loadPaymentMethodPageData(userId, model);

        return "settings/payment-method";
    }

    @PostMapping("/new")
    public String addPaymentMethod(
            @RequestParam String cardholderName,
            @RequestParam PaymentType paymentType,
            @RequestParam String cardNumber,
            @RequestParam String expirationDate,
            @RequestParam(defaultValue = "false") boolean defaultPaymentMethod,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            PaymentInformationRequest request = new PaymentInformationRequest();
            request.userId = userId;
            request.cardholderName = cardholderName;
            request.paymentType = paymentType;
            request.cardNumber = cardNumber;
            request.expirationDate = expirationDate;
            request.defaultPaymentMethod = defaultPaymentMethod;

            paymentInformationService.create(request);

            redirectAttributes.addFlashAttribute("successMessage", "Payment method added.");
            return "redirect:/settings/payment-method";

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment method failed: " + ex.getMessage());
            return "redirect:/settings/payment-method";
        }
    }

    @PostMapping("/{id}/edit")
    public String updatePaymentMethod(
            @PathVariable Long id,
            @RequestParam String cardholderName,
            @RequestParam PaymentType paymentType,
            @RequestParam(required = false) String cardNumber,
            @RequestParam String expirationDate,
            @RequestParam(defaultValue = "false") boolean defaultPaymentMethod,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            PaymentInformationRequest request = new PaymentInformationRequest();
            request.userId = userId;
            request.cardholderName = cardholderName;
            request.paymentType = paymentType;
            request.cardNumber = cardNumber;
            request.expirationDate = expirationDate;
            request.defaultPaymentMethod = defaultPaymentMethod;

            paymentInformationService.update(userId, id, request);

            redirectAttributes.addFlashAttribute("successMessage", "Payment method updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment method failed: " + ex.getMessage());
        }

        return "redirect:/settings/payment-method";
    }

    @PostMapping("/{id}/default")
    public String setDefaultPaymentMethod(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        paymentInformationService.setDefault(userId, id);

        redirectAttributes.addFlashAttribute("successMessage", "Default payment method updated.");
        return "redirect:/settings/payment-method";
    }

    @PostMapping("/{id}/delete")
    public String deletePaymentMethod(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        paymentInformationService.delete(userId, id);

        redirectAttributes.addFlashAttribute("successMessage", "Payment method deleted.");
        return "redirect:/settings/payment-method";
    }

    private void loadPaymentMethodPageData(Long userId, Model model) {
        model.addAttribute("paymentMethods",
                paymentInformationService.getByUserId(userId)
                        .stream()
                        .map(PaymentInformationResponse::of)
                        .toList());

        if (!model.containsAttribute("paymentInformationRequest")) {
            model.addAttribute("paymentInformationRequest", new PaymentInformationRequest());
        }
    }

    private Long getLoggedInUserId(HttpSession session) {
        return (Long) session.getAttribute("loggedInUserId");
    }
}