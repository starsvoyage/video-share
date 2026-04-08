package edu.arizona.videoshare.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * VerificationToken entity
 *
 * Stores email verification tokens used to activate accounts.
 * Future implementation: Reset passwords.
 */
@Getter
@Setter
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique verification token sent via email.
     */
    @Column(nullable = false, unique = true, length = 120)
    private String token;

    /**
     * 6-digit code the user can enter manually.
     */
    @Column(nullable = false, length = 6)
    private String code;

    /**
     * Associated user.
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Expiration timestamp.
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Indicates if the token has already been used.
     */
    @Column(nullable = false)
    private boolean used = false;
}