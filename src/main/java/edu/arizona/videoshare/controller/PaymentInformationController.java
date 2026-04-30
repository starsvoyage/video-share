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

    @GetMapping("/users/{userId}/{paymentInformationId}")
    public PaymentInformationResponse getById(
            @PathVariable Long userId,
            @PathVariable Long paymentInformationId
    ) {
        return PaymentInformationResponse.of(
                paymentInformationService.getById(userId, paymentInformationId)
        );
    }

    @PatchMapping("/users/{userId}/{paymentInformationId}")
    public PaymentInformationResponse update(
            @PathVariable Long userId,
            @PathVariable Long paymentInformationId,
            @Valid @RequestBody PaymentInformationRequest request
    ) {
        return PaymentInformationResponse.of(
                paymentInformationService.update(userId, paymentInformationId, request)
        );
    }

    @PatchMapping("/users/{userId}/{paymentInformationId}/default")
    public PaymentInformationResponse setDefault(
            @PathVariable Long userId,
            @PathVariable Long paymentInformationId
    ) {
        return PaymentInformationResponse.of(
                paymentInformationService.setDefault(userId, paymentInformationId)
        );
    }

    @DeleteMapping("/users/{userId}/{paymentInformationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long userId,
            @PathVariable Long paymentInformationId
    ) {
        paymentInformationService.delete(userId, paymentInformationId);
    }

    @PostMapping("/mock-payment")
    public MockPaymentResponse processMockPayment(@Valid @RequestBody MockPaymentRequest request) {
        return paymentInformationService.processMockPayment(request);
    }
}