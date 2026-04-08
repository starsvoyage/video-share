package edu.arizona.videoshare.model.entity;

import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * User entity
 *
 * Represents a platform account profile (public).
 *
 * Authentication secrets are NOT stored here; these are in
 * {@link UserCredentials}.
 * This is to help keep user profile concerns separate from security concerns,
 * reducing
 * the chance of accidentally exposing credential data in REST responses.
 * Not using @Data on JPA entities to prevent unintended equals/hashCode
 * behavior.
 */
@Getter
@Entity
@Table(name = "users", uniqueConstraints = {
        // Database-level enforcement of business rule: unique username and email
        @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})

public class User {

    /**
     * Surrogate primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier for display/URLs.
     */
    @Setter
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * Unique identifier for account.
     */
    @Setter
    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, length = 254)
    private String email;

    /**
     * Display label shown in UI (not unique).
     */
    @Setter
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String displayName;

    /**
     * Account timestamps. createdAt is always set; updatedAt is nullable until
     * first update.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Account status for moderation/availability.
     */
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Optional profile fields.
     */
    @Setter
    @Size(max = 500)
    @Column(length = 500)
    private String bio;

    @Setter
    @Size(max = 500)
    @Column(length = 500)
    private String avatarUrl;

    /**
     * Platform role for authorization. USerRole.VIEWER is default.
     */
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.VIEWER;

    /**
     * 1:1 relationship to credentials (authentication data).
     * Business Rule: Each User should have exactly one UserCredentials record.
     */

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private UserCredentials credentials;

    public User() {
    }

    /**
     * Set creation timestamp automatically when the row is first inserted.
     */
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Set update timestamp automatically any time the row is updated.
     */
    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Helper to keep both sides of the bidirectional 1:1 association consistent.
     */
    public void attachCredentials(UserCredentials creds) {
        this.credentials = creds;
        creds.setUser(this);
    }

}
