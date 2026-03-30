package edu.arizona.videoshare.service;

import edu.arizona.videoshare.model.entity.PasswordResetToken;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.UserCredentials;
import edu.arizona.videoshare.repository.PasswordResetTokenRepository;
import edu.arizona.videoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository users;
    private final PasswordResetTokenRepository resetTokens;
    private final BCryptPasswordEncoder encoder;
    private final EmailService emailService;

    @Transactional
    public void requestPasswordReset(String email) {
        users.findByEmailIgnoreCase(email.trim()).ifPresent(user -> {
            resetTokens.deleteByUserId(user.getId());

            PasswordResetToken token = new PasswordResetToken();
            token.setUser(user);
            token.setToken(UUID.randomUUID().toString());
            token.setExpiresAt(LocalDateTime.now().plusHours(1));
            token.setUsed(false);

            resetTokens.save(token);
            emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), token.getToken());
        });
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken token = resetTokens.findByToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        if (token.isUsed()) {
            throw new IllegalArgumentException("This password reset link has already been used");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This password reset link has expired");
        }

        User user = token.getUser();
        UserCredentials credentials = user.getCredentials();

        credentials.setPasswordHash(encoder.encode(newPassword));
        credentials.setPasswordUpdatedAt(LocalDateTime.now());
        credentials.setFailedLoginAttempts(0);
        credentials.setLocked(false);
        credentials.setLockedUntil(null);

        token.setUsed(true);

        emailService.sendPasswordResetSuccessEmail(
                user.getEmail(),
                user.getUsername()
        );
    }
}