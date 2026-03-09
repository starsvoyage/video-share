package edu.arizona.videoshare.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * LoginForm DTO
 *
 * Represents the login form submitted by a user.
 * Contains the credentials required for authentication.
 *
 * Used by AuthController to bind login form data
 * from the login page.
 */
@Getter
@Setter
public class LoginForm {

    /**
     * Username or email entered by the user.
     * Used to identify the account during login.
     */
    @NotBlank(message = "Username or email is required")
    @Size(max = 254, message = "Username or email is too long")
    private String identifier;

    /**
     * Password entered by the user.
     * Used to verify the user's identity.
     */
    @NotBlank(message = "Password is required")
    @Size(max = 72, message = "Password is too long")
    private String password;
}