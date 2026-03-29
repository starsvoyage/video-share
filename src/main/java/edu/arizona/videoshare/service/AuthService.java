package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.user.LoginForm;
import edu.arizona.videoshare.dto.user.RegisterForm;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.UserCredentials;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.repository.UserCredentialsRepository;
import edu.arizona.videoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
/**
 * AuthService (Business Layer)
 *
 * Handles authentication and registration business logic.
 * Responsible for creating new user accounts and verifying
 * login credentials.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;
    private final VerificationService verificationService;
    private final UserCredentialsRepository credentialsRepository;

    /**
     * Registers a new user account.
     * Validates uniqueness of username and email,
     * hashes the password, and saves the user.
     */
    @Transactional
    public User register(RegisterForm form) {
        String username = form.getUsername().trim();
        String email = form.getEmail().trim().toLowerCase();
        String rawPassword = form.getPassword();

        if (users.existsByUsername(username)) {
            throw new ConflictException("Username already exists");
        }

        if (users.existsByEmail(email)) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setDisplayName(username);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setRole(UserRole.VIEWER);

        UserCredentials credentials = new UserCredentials();
        credentials.setPasswordHash(encoder.encode(rawPassword));
        user.attachCredentials(credentials);

        User savedUser = users.save(user);
        verificationService.createAndSendVerification(savedUser);

        return savedUser;
    }

    /**
     * Authenticates a user during login.
     * Verifies the identifier (username/email) and password.
     */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public User authenticate(LoginForm form) {
        String identifier = form.getIdentifier().trim();
        String password = form.getPassword();

        User user = users.findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username/email or password"));

        UserCredentials credentials = user.getCredentials();

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException("Your account is suspended.");
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new IllegalArgumentException("This account is no longer available.");
        }

        if (credentials  == null) {
            throw new IllegalArgumentException("Invalid username/email or password");
        }

        log.info("Before auth: user={}, attempts={}, locked={}, lockedUntil={}",
                user.getUsername(),
                credentials.getFailedLoginAttempts(),
                credentials.isLocked(),
                credentials.getLockedUntil());

        autoUnlockIfExpired(credentials);

        if (credentials.isLocked()) {
            log.info("Login blocked because account is locked for user={}", user.getUsername());
            throw new IllegalArgumentException("Your account is temporarily locked. Please reset your password or try again later.");
        }

        if (!encoder.matches(password, credentials.getPasswordHash())) {
            registerFailedAttempt(credentials);
            throw new IllegalArgumentException("Invalid username/email or password");
        }

        clearFailedAttempts(credentials);
        return user;
    }
    private void autoUnlockIfExpired(UserCredentials credentials) {
        if (credentials.isLocked()
                && credentials.getLockedUntil() != null
                && credentials.getLockedUntil().isBefore(LocalDateTime.now())) {
            credentials.setLocked(false);
            credentials.setLockedUntil(null);
            credentials.setFailedLoginAttempts(0);
            credentialsRepository.save(credentials);
        }
    }

    private void registerFailedAttempt(UserCredentials credentials) {
        int attempts = credentials.getFailedLoginAttempts() + 1;
        credentials.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            credentials.setLocked(true);
            credentials.setLockedUntil(LocalDateTime.now().plusMinutes(15));
        }

        credentialsRepository.save(credentials);

        log.info("After failed attempt: userId={}, attempts={}, locked={}, lockedUntil={}",
                credentials.getUser().getId(),
                credentials.getFailedLoginAttempts(),
                credentials.isLocked(),
                credentials.getLockedUntil());
    }

    private void clearFailedAttempts(UserCredentials credentials) {
        if (credentials.getFailedLoginAttempts() > 0 || credentials.isLocked() || credentials.getLockedUntil() != null) {
            credentials.setFailedLoginAttempts(0);
            credentials.setLocked(false);
            credentials.setLockedUntil(null);
            credentialsRepository.save(credentials);
        }
    }
}