package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.UserCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserJpaTest
 *
 * Repository/ORM-focused tests using @DataJpaTest.
 */
@ActiveProfiles("test")
@DataJpaTest
class UserJpaTest {

    @Autowired UserRepository userRepository;
    @Autowired UserCredentialsRepository credsRepository;

    /**
     * Helper builder for creating a valid User + UserCredentials pair.
     */
    private User buildUser(String username, String email) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setDisplayName("TestUser");

        UserCredentials c = new UserCredentials();

        // Dummy bcrypt-looking string
        c.setPasswordHash("$2a$10$dummyDummyDummyDummyDummyDummyDummyDummyDummyDummy");

        u.attachCredentials(c);
        return u;
    }

    /**
     * Validates shared primary key mapping.
     */
    @Test
    void savesUserAndCredentialsTogether_sharedPrimaryKey() {
        User saved = userRepository.save(buildUser("ian2", "ian2@ua.edu"));

        // user exists
        assertNotNull(saved.getId());

        // credentials exists and uses same id (shared PK)
        UserCredentials creds = credsRepository.findById(saved.getId()).orElseThrow();
        assertEquals(saved.getId(), creds.getId());
        assertEquals(saved.getId(), creds.getUser().getId());
    }

    /**
     * Validates lifecycle callback behavior.
     */
    @Test
    void updatePersistsChanges_andSetsUpdatedAt() {
        User u = userRepository.saveAndFlush(buildUser("ian4", "ian4@ua.edu"));

        assertNull(u.getUpdatedAt()); // should be null right after create

        u.setDisplayName("Updated Name");
        User updated = userRepository.saveAndFlush(u);

        assertEquals("Updated Name", updated.getDisplayName());
        assertNotNull(updated.getUpdatedAt()); // @PreUpdate should have fired

        // prove it’s persisted (fresh read)
        User reloaded = userRepository.findById(u.getId()).orElseThrow();
        assertEquals("Updated Name", reloaded.getDisplayName());
        assertNotNull(reloaded.getUpdatedAt());
    }

    /**
     * Validates cascade/orphan removal:
     */
    @Test
    void deletingUserDeletesCredentials() {
        User u = userRepository.saveAndFlush(buildUser("ian3", "ian3@ua.edu"));

        userRepository.delete(u);
        userRepository.flush();

        assertFalse(credsRepository.findById(u.getId()).isPresent());
    }

    /**
     * Validates DB-level unique constraint enforcement for username.
     */
    @Test
    void usernameMustBeUnique_dbConstraint() {
        userRepository.save(buildUser("user1", "user1@gmail.com"));

        // second user with same username should violate unique constraint
        User u2 = buildUser("user1", "user2@gmail.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(u2); // flush forces DB constraint check
        });
    }

    /**
     * Validates DB-level unique constraint enforcement for email.
     */
    @Test
    void emailMustBeUnique_dbConstraint() {
        userRepository.save(buildUser("user1", "user1@gmail.com"));

        User u2 = buildUser("user2", "user1@gmail.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(u2);
        });
    }

}