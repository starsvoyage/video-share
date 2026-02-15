package edu.arizona.videoshare.model.entity;

import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;


/**
 * User entity
 *
 * Represents a platform account profile (public).
 *
 * Authentication secrets are NOT stored here; these are in {@link UserCredentials}.
 * This is to help keep user profile concerns separate from security concerns, reducing
 * the chance of accidentally exposing credential data in REST responses.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                // Database-level enforcement of business rule: unique username and email
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        }
)

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
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * Unique identifier for account.
     */
    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, length = 254)
    private String email;

    /**
     * Display label shown in UI (not unique).
     */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String displayName;

    /**
     * Account timestamps. createdAt is always set; updatedAt is nullable until first update.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Account status for moderation/availability.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Optional profile fields.
     */
    @Size(max = 500)
    @Column(length = 500)
    private String bio;

    @Size(max = 500)
    @Column(length = 500)
    private String avatarUrl;

    /**
     * Platform role for authorization. USerRole.VIEWER is default.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.VIEWER;

    /**
     * 1:1 relationship to credentials (authentication data).
     * Business Rule: Each User should have exactly one UserCredentials record.
     */
    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            optional = false,
            fetch = FetchType.LAZY
    )

    private UserCredentials credentials;

    public User() {}

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
     * Helper to keep both sides of the bi-directional 1:1 association consistent.
     */
    public void attachCredentials(UserCredentials creds) {
        this.credentials = creds;
        creds.setUser(this);
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public UserCredentials getCredentials() { return credentials; }
}