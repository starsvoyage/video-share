package edu.arizona.videoshare.dto.payment;

import edu.arizona.videoshare.model.entity.PaymentInformation;
import edu.arizona.videoshare.model.enums.PaymentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentInformationResponse {
    private Long id;
    private Long userId;
    private PaymentType paymentType;
    private String cardholderName;
    private String lastFourDigits;
    private String expirationDate;
    private boolean defaultPaymentMethod;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentInformationResponse of(PaymentInformation paymentInformation) {
        return PaymentInformationResponse.builder()
                .id(paymentInformation.getId())
                .userId(paymentInformation.getUserId())
                .paymentType(paymentInformation.getPaymentType())
                .cardholderName(paymentInformation.getCardholderName())
                .lastFourDigits(paymentInformation.getLastFourDigits())
                .expirationDate(paymentInformation.getExpirationDate())
                .defaultPaymentMethod(paymentInformation.isDefaultPaymentMethod())
                .active(paymentInformation.isActive())
                .createdAt(paymentInformation.getCreatedAt())
                .updatedAt(paymentInformation.getUpdatedAt())
                .build();
    }
}