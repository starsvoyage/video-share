package edu.arizona.videoshare.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * UserCredentials entity
 *
 * Represents security/authentication data/
 *
 * Stores authentication-related fields separately from {@link User} profile data.
 * Shared Primary Key association (UserCredentials.id == User.id)
 * Implemented with @MapsId to ensure the credentials row shares the same PK as its user.
 * Not using @Data on JPA entities to prevent unintended equals/hashCode behavior.
 */
@Getter
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
    @Setter
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
    @Setter
    @NotBlank
    @Size(min = 20, max = 100) // BCrypt hashes are typically 60 chars
    @Column(nullable = false, length = 100)
    private String passwordHash;

    /**
     * Timestamp of when the password was last set/updated.
     */
    @Setter
    private LocalDateTime passwordUpdatedAt;

    /**
     * Number of consecutive failed authentication attempts.
     */
    @Setter
    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    /**
     * Lock flag (or lock indicator). TO be combined with lockedUntil.
     */
    @Setter
    @Column(nullable = false)
    private boolean locked = false;

    /**
     * Temporary lockout expiration time.
     */
    @Setter
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

}