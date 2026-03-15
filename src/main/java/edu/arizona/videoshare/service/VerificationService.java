package edu.arizona.videoshare.service;

import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.VerificationToken;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationTokenRepository tokens;
    private final UserRepository users;
    private final EmailService emailService;

    @Transactional
    public void createAndSendVerification(User user) {
        tokens.findByUserId(user.getId()).ifPresent(tokens::delete);

        VerificationToken verification = new VerificationToken();
        verification.setUser(user);
        verification.setToken(UUID.randomUUID().toString());
        verification.setCode(generateSixDigitCode());
        verification.setExpiresAt(LocalDateTime.now().plusHours(24));
        verification.setUsed(false);

        tokens.save(verification);

        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getUsername(),
                verification.getToken(),
                verification.getCode()
        );
    }

    @Transactional
    public void resendVerification(String email) {
        User user = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account already verified");
        }

        createAndSendVerification(user);
    }

    @Transactional
    public void verifyByToken(String tokenValue) {
        VerificationToken verification = tokens.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification link"));

        validateVerification(verification);
        activateUser(verification);
    }

    @Transactional
    public void verifyByCode(String code) {
        VerificationToken verification = tokens.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification code"));

        validateVerification(verification);
        activateUser(verification);
    }

    private void validateVerification(VerificationToken verification) {
        if (verification.isUsed()) {
            throw new IllegalArgumentException("Verification already completed");
        }

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification has expired");
        }
    }

    private void activateUser(VerificationToken verification) {
        User user = verification.getUser();
        user.setStatus(UserStatus.ACTIVE);
        verification.setUsed(true);

        users.save(user);
        tokens.save(verification);

        emailService.sendVerificationSuccessEmail(
                user.getEmail(),
                user.getUsername()
        );
    }

    private String generateSixDigitCode() {
        int number = new Random().nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}