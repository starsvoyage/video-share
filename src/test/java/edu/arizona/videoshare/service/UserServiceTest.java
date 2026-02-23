package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.user.UserRequest;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.repository.UserCredentialsRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserServiceTest
 *
 * Integration-style tests that run with Spring Boot and real repositories.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired UserCredentialsRepository credsRepository;
    @Autowired BCryptPasswordEncoder encoder;

    /**
     * Helper for building a registration request.
     */
    private UserRequest req(String username, String email, String password) {
        UserRequest r = new UserRequest();
        r.username = username;
        r.email = email;
        r.displayName = "Test User";
        r.password = password;
        return r;
    }

    /**
     * Helper for building an update-style request.
     */
    private UserRequest upd(String displayName, String bio, String avatarUrl) {
        UserRequest u = new UserRequest();
        u.displayName = displayName;
        u.bio = bio;
        u.avatarUrl = avatarUrl;
        return u;
    }

    /**
     * Validates registration: User is persisted, redentials are created and
     * password is stored as bcrypt hash
     */
    @Test
    void registerCreatesUserAndHashedCredentials() {
        String raw = "Password@123";

        // When registering a new user
        User u = userService.register(req("ian0", "ian0@arizona.edu", raw));

        // Then user is persisted and has an ID
        assertNotNull(u.getId());

        // And credentials exist using the same ID (shared PK mapping)
        var creds = credsRepository.findById(u.getId()).orElseThrow();

        // And password is NOT stored as plaintext
        assertNotEquals(raw, creds.getPasswordHash());

        // And bcrypt hash verifies successfully
        assertTrue(encoder.matches(raw, creds.getPasswordHash()));
    }

    /**
     * Validates business rule: username must be unique.
     */
    @Test
    void registerRejectsDuplicateUsername() {
        userService.register(req("xuser", "x@gmail.com", "Password123"));

        // same username but different email - should conflict
        assertThrows(ConflictException.class, () ->
                userService.register(req("xuser", "y@gmail.com", "Password123"))
        );
    }

    /**
     * Validates business rule: Email must be unique.
     */
    @Test
    void registerRejectsDuplicateEmail() {
        userService.register(req("xuser", "x@gmail.com", "Password123"));

        // different username but same email - should conflict
        assertThrows(ConflictException.class, () ->
                userService.register(req("yuser", "x@gmail.com", "Password123"))
        );
    }

    /**
     * Validates 1:1 mapping: each user has exactly one credentials record.
     */
    @Test
    void eachUserHasExactlyOneCredentialsRecord() {
        User u = userService.register(req("ian1", "ian1@arizona.edu", "Password123"));

        // expects exactly 1 credential row after creating 1 user
        assertEquals(1, credsRepository.count());
        assertTrue(credsRepository.findById(u.getId()).isPresent());
    }

    /**
     * Validates profile update: profile fields change but redentials remain unchanged
     */
    @Test
    void updateUpdatesProfileFieldsAndKeepsCredentialsSame() {
        // Given an existing user with credentials
        User u = userService.register(req("ian2", "ian2@arizona.edu", "Password123"));

        // Capture existing password hash (must not change on profile update)
        String oldHash = credsRepository.findById(u.getId()).orElseThrow().getPasswordHash();

        // When updating profile fields
        var updated = userService.update(u.getId(),
                upd("New Display", "New bio", "https://example.com/avatar.png"));

        // Then profile fields update
        assertEquals("New Display", updated.getDisplayName());
        assertEquals("New bio", updated.getBio());
        assertEquals("https://example.com/avatar.png", updated.getAvatarUrl());

        // And credentials unchanged
        String newHash = credsRepository.findById(u.getId()).orElseThrow().getPasswordHash();
        assertEquals(oldHash, newHash);
    }

    /**
     * Validates cascade delete: deleting User removes associated credentials.
     */
    @Test
    void deleteRemovesUserAndCredentials() {
        // Given an existing user
        User u = userService.register(req("ian5", "ian5@arizona.edu", "Password123"));

        Long id = u.getId();

        // Ensure both user and credentials exist before delete
        assertTrue(userRepository.findById(id).isPresent());
        assertTrue(credsRepository.findById(id).isPresent());

        // When deleting user
        userService.delete(id);

        // The user and associated credentials are removed (cascade + orphanRemoval)
        assertFalse(userRepository.findById(id).isPresent());
        assertFalse(credsRepository.findById(id).isPresent());
    }

    /**
     * Validates error handling: deleting non-existent user throws NotFoundException.
     */
    @Test
    void deleteNonExistingUserThrowsNotFound() {
        assertThrows(NotFoundException.class, () ->
                userService.delete(9999L)
        );
    }
}