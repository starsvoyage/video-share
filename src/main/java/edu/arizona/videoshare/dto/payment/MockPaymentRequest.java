package edu.arizona.videoshare.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MockPaymentRequest {

    @NotNull
    public Long userId;

    @NotNull
    public Long paymentInformationId;

    @NotNull
    @Positive
    public Integer amountInCents;
}