package edu.arizona.videoshare.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MockPaymentResponse {
    private boolean successful;
    private String transactionId;
    private String message;
    private Integer amountInCents;
    private LocalDateTime processedAt;
}