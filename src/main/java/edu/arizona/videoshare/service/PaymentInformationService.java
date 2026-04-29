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
        validateUserExists(request.userId);

        String lastFourDigits = extractLastFourDigits(request.cardNumber);

        List<PaymentInformation> existingMethods =
                paymentInformationRepository.findByUserIdAndActiveTrueOrderByDefaultPaymentMethodDescCreatedAtDesc(request.userId);

        boolean shouldBeDefault = request.defaultPaymentMethod || existingMethods.isEmpty();

        if (shouldBeDefault) {
            clearDefaultPaymentMethod(request.userId);
        }

        PaymentInformation paymentInformation = PaymentInformation.builder()
                .userId(request.userId)
                .paymentType(request.paymentType)
                .cardholderName(request.cardholderName.trim())
                .lastFourDigits(lastFourDigits)
                .expirationDate(request.expirationDate.trim())
                .defaultPaymentMethod(shouldBeDefault)
                .active(true)
                .build();

        return paymentInformationRepository.save(paymentInformation);
    }

    @Transactional(readOnly = true)
    public List<PaymentInformation> getByUserId(Long userId) {
        validateUserExists(userId);

        return paymentInformationRepository
                .findByUserIdAndActiveTrueOrderByDefaultPaymentMethodDescCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public PaymentInformation getById(Long userId, Long paymentInformationId) {
        validateUserExists(userId);

        return paymentInformationRepository.findByIdAndUserIdAndActiveTrue(paymentInformationId, userId)
                .orElseThrow(() -> new NotFoundException("Payment information not found: " + paymentInformationId));
    }

    @Transactional
    public PaymentInformation update(Long userId, Long paymentInformationId, PaymentInformationRequest request) {
        PaymentInformation paymentInformation = getById(userId, paymentInformationId);

        if (request.paymentType != null) {
            paymentInformation.setPaymentType(request.paymentType);
        }

        if (request.cardholderName != null && !request.cardholderName.isBlank()) {
            paymentInformation.setCardholderName(request.cardholderName.trim());
        }

        if (request.cardNumber != null && !request.cardNumber.isBlank()) {
            paymentInformation.setLastFourDigits(extractLastFourDigits(request.cardNumber));
        }

        if (request.expirationDate != null && !request.expirationDate.isBlank()) {
            paymentInformation.setExpirationDate(request.expirationDate.trim());
        }

        if (request.defaultPaymentMethod) {
            clearDefaultPaymentMethod(userId);
            paymentInformation.setDefaultPaymentMethod(true);
        }

        return paymentInformationRepository.save(paymentInformation);
    }

    @Transactional
    public PaymentInformation setDefault(Long userId, Long paymentInformationId) {
        PaymentInformation paymentInformation = getById(userId, paymentInformationId);

        if (paymentInformation.isDefaultPaymentMethod()) {
            return paymentInformation;
        }

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
            assignNextDefaultPaymentMethod(userId);
        }
    }

    @Transactional
    public MockPaymentResponse processMockPayment(MockPaymentRequest request) {
        PaymentInformation paymentInformation = getById(
                request.userId,
                request.paymentInformationId
        );

        return MockPaymentResponse.builder()
                .successful(true)
                .transactionId("mock_" + UUID.randomUUID())
                .message("Mock payment processed successfully using payment method ending in "
                        + paymentInformation.getLastFourDigits())
                .amountInCents(request.amountInCents)
                .processedAt(LocalDateTime.now())
                .build();
    }

    private void validateUserExists(Long userId) {
        if (userId == null || !userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
    }

    private void clearDefaultPaymentMethod(Long userId) {
        paymentInformationRepository.findByUserIdAndDefaultPaymentMethodTrueAndActiveTrue(userId)
                .ifPresent(existingDefault -> {
                    existingDefault.setDefaultPaymentMethod(false);
                    paymentInformationRepository.save(existingDefault);
                });
    }

    private void assignNextDefaultPaymentMethod(Long userId) {
        List<PaymentInformation> remainingMethods =
                paymentInformationRepository.findByUserIdAndActiveTrueOrderByDefaultPaymentMethodDescCreatedAtDesc(userId);

        if (!remainingMethods.isEmpty()) {
            PaymentInformation nextDefault = remainingMethods.get(0);
            nextDefault.setDefaultPaymentMethod(true);
            paymentInformationRepository.save(nextDefault);
        }
    }

    private String extractLastFourDigits(String cardNumber) {
        if (cardNumber == null) {
            throw new IllegalArgumentException("Card number is required.");
        }

        String cleaned = cardNumber.replaceAll("[^0-9]", "");

        if (cleaned.length() < 4) {
            throw new IllegalArgumentException("Card number must contain at least 4 digits.");
        }

        return cleaned.substring(cleaned.length() - 4);
    }
}