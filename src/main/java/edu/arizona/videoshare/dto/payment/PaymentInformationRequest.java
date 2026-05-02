package edu.arizona.videoshare.dto.payment;

import edu.arizona.videoshare.model.enums.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PaymentInformationRequest {

    public Long userId;

    @NotNull
    public PaymentType paymentType;

    @NotBlank
    public String cardholderName;

    @NotBlank
    @Pattern(
            regexp = "^[0-9 ]{13,23}$",
            message = "Card number must contain 13 to 19 digits."
    )
    public String cardNumber;

    @NotBlank
    @Pattern(
            regexp = "^(0[1-9]|1[0-2])/\\d{2}$",
            message = "Expiration date must use MM/YY format."
    )
    public String expirationDate;

    public boolean defaultPaymentMethod;
}