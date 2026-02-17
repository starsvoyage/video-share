package edu.arizona.videoshare.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UserRequest DTO
 *
 * Represents incoming data for user registration and profile updates.
 *
 * Security note:
 * Raw passwords are accepted here but never persisted directly.
 * The service layer hashes passwords using BCrypt before storage.
 */
public class UserRequest {

    /**
     * Public unique identifier for the user.
     */
    @NotBlank
    @Size(min = 3, max = 50)
    public String username;

    /**
     * Account login identifier.
     */
    @NotBlank
    @Email
    @Size(max = 254)
    public String email;

    /**
     * Display name shown publicly.
     */
    @NotBlank
    @Size(max = 100)
    public String displayName;

    /**
     * Optional profile description.
     */
    @Size(max = 500)
    public String bio;

    /**
     * Optional path to avatar image.
     */
    @Size(max = 500)
    public String avatarUrl;

    /**
     * Raw password only used during registration or password update.
     * BCrypt only uses the first 72 bytes of input.
     * Service layer is responsible for hashing before persistence.
     */
    @NotBlank
    @Size(min = 8, max = 72) // BCrypt only uses first 72 bytes
    public String password;
}