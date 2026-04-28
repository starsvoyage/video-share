package edu.arizona.videoshare.dto.payment;

import edu.arizona.videoshare.model.enums.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PaymentInformationRequest {

    @NotNull
    public Long userId;

    @NotNull
    public PaymentType paymentType;

    @NotBlank
    public String cardholderName;

    @NotBlank
    @Pattern(regexp = "\\d{13,19}", message = "Card number must be between 13 and 19 digits.")
    public String cardNumber;

    @NotBlank
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Expiration date must use MM/YY format.")
    public String expirationDate;

    public boolean defaultPaymentMethod;
}