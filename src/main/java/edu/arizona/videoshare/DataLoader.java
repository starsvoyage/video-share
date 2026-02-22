package edu.arizona.videoshare;

import edu.arizona.videoshare.dto.user.UserRequest;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

/**
 * DataLoader
 *
 * Seeds initial data into the database when the application starts.
 *
 * Profile restriction:
 * Disabled when the "test" profile is active to prevent interference with automated tests.
 */
@Profile("!test")
@Component
public class DataLoader implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;

    public DataLoader(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Runs automatically at application startup.
     * Seeds users only if the database is empty to prevent duplicate insertions.
     */
    @Override
    public void run(String... args) {
        // Avoid reseeding on restart
        if (userRepository.count() > 0) return;

        seed("ian", "idiazvachier@arizona.edu", "Ian Diaz-Vachier", "Password@123");
        seed("user1", "user1@ua.edu", "TheUser1", "User1@123");
    }

    /**
     * Helper method to seed a user via service layer.
     */
    private void seed(String username, String email, String displayName, String password) {
        UserRequest req = new UserRequest();
        req.username = username;
        req.email = email;
        req.displayName = displayName;
        req.password = password;

        // Uses service.register() to enforce rules
        userService.register(req);
    }
}