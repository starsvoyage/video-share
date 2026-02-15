package edu.arizona.videoshare.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;


/**
 * UserCredentials entity
 *
 * Represents security/authentication data/
 *
 * Stores authentication-related fields separately from {@link User} profile data.
 * Shared Primary Key association (UserCredentials.id == User.id)
 * Implemented with @MapsId to ensure the credentials row shares the same PK as its user.
 */
@Entity
@Table(name = "user_credentials")
public class UserCredentials {

    /**
     * Shared primary key: this value is inherited from the associated User.id.
     */
    @Id
    private Long id;

    /**
     * Owning side of the 1:1 relationship.
     */
    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(
                    name = "fk_credentials_user"
            )
    )
    private User user;

    /**
     * Secure one-way hash of the user's password.
     */
    @NotBlank
    @Size(min = 20, max = 100) // BCrypt hashes are typically 60 chars
    @Column(nullable = false, length = 100)
    private String passwordHash;

    /**
     * Timestamp of when the password was last set/updated.
     */
    private LocalDateTime passwordUpdatedAt;

    /**
     * Number of consecutive failed authentication attempts.
     */
    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    /**
     * Lock flag (or lock indicator). TO be combined with lockedUntil.
     */
    @Column(nullable = false)
    private boolean locked = false;

    /**
     * Temporary lockout expiration time.
     */
    private LocalDateTime lockedUntil;

    public UserCredentials() {}

    /**
     * Sets passwordUpdatedAt if the application didn't explicitly provide it.
     */
    @PrePersist
    void onCreate() {
        if (passwordUpdatedAt == null) {
            passwordUpdatedAt = LocalDateTime.now();
        }
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getPasswordUpdatedAt() { return passwordUpdatedAt; }
    public void setPasswordUpdatedAt(LocalDateTime passwordUpdatedAt) { this.passwordUpdatedAt = passwordUpdatedAt; }

    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
}