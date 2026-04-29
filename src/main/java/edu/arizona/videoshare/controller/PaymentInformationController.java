package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.payment.MockPaymentRequest;
import edu.arizona.videoshare.dto.payment.MockPaymentResponse;
import edu.arizona.videoshare.dto.payment.PaymentInformationRequest;
import edu.arizona.videoshare.dto.payment.PaymentInformationResponse;
import edu.arizona.videoshare.service.PaymentInformationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment-information")
public class PaymentInformationController {

    private final PaymentInformationService paymentInformationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentInformationResponse create(@Valid @RequestBody PaymentInformationRequest request) {
        return PaymentInformationResponse.of(paymentInformationService.create(request));
    }

    @GetMapping("/users/{userId}")
    public List<PaymentInformationResponse> getByUserId(@PathVariable Long userId) {
        return paymentInformationService.getByUserId(userId)
                .stream()
                .map(PaymentInformationResponse::of)
                .toList();
    }

    @GetMapping("/{paymentInformationId}/users/{userId}")
    public PaymentInformationResponse getById(
            @PathVariable Long paymentInformationId,
            @PathVariable Long userId
    ) {
        return PaymentInformationResponse.of(paymentInformationService.getById(userId, paymentInformationId));
    }

    @PatchMapping("/{paymentInformationId}/users/{userId}/default")
    public PaymentInformationResponse setDefault(
            @PathVariable Long paymentInformationId,
            @PathVariable Long userId
    ) {
        return PaymentInformationResponse.of(paymentInformationService.setDefault(userId, paymentInformationId));
    }

    @DeleteMapping("/{paymentInformationId}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long paymentInformationId,
            @PathVariable Long userId
    ) {
        paymentInformationService.delete(userId, paymentInformationId);
    }

    @PostMapping("/mock-payment")
    public MockPaymentResponse processMockPayment(@Valid @RequestBody MockPaymentRequest request) {
        return paymentInformationService.processMockPayment(request);
    }
}