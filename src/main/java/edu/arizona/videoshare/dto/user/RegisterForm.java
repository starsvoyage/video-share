package edu.arizona.videoshare.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * RegisterForm DTO
 *
 * Represents the registration form submitted by a new user.
 * Contains the information required to create a new account.
 *
 * Used by AuthController to bind form data from the
 * registration page and perform validation.
 */
@Getter
@Setter
public class RegisterForm {

    /**
     * Username chosen by the user.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * Email address for the user account.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 254, message = "Email must be 254 characters or less")
    private String email;

    /**
     * Password chosen by the user.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    private String password;

    /**
     * Confirmation of the password.
     * Used to verify that the user entered the same password.
     */
    @NotBlank(message = "Confirm password is required")
    @Size(min = 8, max = 72, message = "")
    private String confirmPassword;
}