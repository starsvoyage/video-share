package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.payment.MockPaymentRequest;
import edu.arizona.videoshare.dto.payment.MockPaymentResponse;
import edu.arizona.videoshare.dto.payment.PaymentInformationRequest;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.PaymentInformation;
import edu.arizona.videoshare.repository.PaymentInformationRepository;
import edu.arizona.videoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentInformationService {

    private final PaymentInformationRepository paymentInformationRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentInformation create(PaymentInformationRequest request) {
        userRepository.findById(request.userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + request.userId));

        if (request.defaultPaymentMethod) {
            clearDefaultPaymentMethod(request.userId);
        }

        List<PaymentInformation> existingMethods =
                paymentInformationRepository.findByUserIdAndActiveTrueOrderByDefaultPaymentMethodDescCreatedAtDesc(request.userId);

        boolean shouldBeDefault = request.defaultPaymentMethod || existingMethods.isEmpty();

        PaymentInformation paymentInformation = PaymentInformation.builder()
                .userId(request.userId)
                .paymentType(request.paymentType)
                .cardholderName(request.cardholderName)
                .lastFourDigits(extractLastFourDigits(request.cardNumber))
                .expirationDate(request.expirationDate)
                .defaultPaymentMethod(shouldBeDefault)
                .active(true)
                .build();

        return paymentInformationRepository.save(paymentInformation);
    }

    @Transactional(readOnly = true)
    public List<PaymentInformation> getByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        return paymentInformationRepository.findByUserIdAndActiveTrueOrderByDefaultPaymentMethodDescCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public PaymentInformation getById(Long userId, Long paymentInformationId) {
        return paymentInformationRepository.findByIdAndUserIdAndActiveTrue(paymentInformationId, userId)
                .orElseThrow(() -> new NotFoundException("Payment information not found: " + paymentInformationId));
    }

    @Transactional
    public PaymentInformation setDefault(Long userId, Long paymentInformationId) {
        PaymentInformation paymentInformation = getById(userId, paymentInformationId);

        clearDefaultPaymentMethod(userId);
        paymentInformation.setDefaultPaymentMethod(true);

        return paymentInformationRepository.save(paymentInformation);
    }

    @Transactional
    public void delete(Long userId, Long paymentInformationId) {
        PaymentInformation paymentInformation = getById(userId, paymentInformationId);

        boolean wasDefault = paymentInformation.isDefaultPaymentMethod();

        paymentInformation.setActive(false);
        paymentInformation.setDefaultPaymentMethod(false);
        paymentInformationRepository.save(paymentInformation);

        if (wasDefault) {
            List<PaymentInformation> remainingMethods =
                    paymentInformationRepository.findByUserIdAndActiveTrueOrderByDefaultPaymentMethodDescCreatedAtDesc(userId);

            if (!remainingMethods.isEmpty()) {
                PaymentInformation nextDefault = remainingMethods.get(0);
                nextDefault.setDefaultPaymentMethod(true);
                paymentInformationRepository.save(nextDefault);
            }
        }
    }

    @Transactional
    public MockPaymentResponse processMockPayment(MockPaymentRequest request) {
        PaymentInformation paymentInformation = getById(request.userId, request.paymentInformationId);

        return MockPaymentResponse.builder()
                .successful(true)
                .transactionId("mock_" + UUID.randomUUID())
                .message("Mock payment processed successfully using payment method ending in " + paymentInformation.getLastFourDigits())
                .amountInCents(request.amountInCents)
                .processedAt(LocalDateTime.now())
                .build();
    }

    private void clearDefaultPaymentMethod(Long userId) {
        paymentInformationRepository.findByUserIdAndDefaultPaymentMethodTrueAndActiveTrue(userId)
                .ifPresent(existingDefault -> {
                    existingDefault.setDefaultPaymentMethod(false);
                    paymentInformationRepository.save(existingDefault);
                });
    }

    private String extractLastFourDigits(String cardNumber) {
        String cleaned = cardNumber.replaceAll("\\s+", "");
        return cleaned.substring(cleaned.length() - 4);
    }
}