package edu.arizona.videoshare.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * VerifyCodeForm (DTO)
 *
 * Represents the 6-digit verification code
 * entered by the user.
 */
@Getter
@Setter
public class VerifyCodeForm {

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "\\d{6}", message = "Verification code must be 6 digits")
    private String code;
}