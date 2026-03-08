package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.user.LoginForm;
import edu.arizona.videoshare.dto.user.RegisterForm;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.UserCredentials;
import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;

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
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.VIEWER);

        UserCredentials credentials = new UserCredentials();
        credentials.setPasswordHash(encoder.encode(rawPassword));

        user.attachCredentials(credentials);

        return users.save(user);
    }

    @Transactional(readOnly = true)
    public User authenticate(LoginForm form) {
        String identifier = form.getIdentifier().trim();
        String password = form.getPassword();

        User user = users.findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username/email or password"));

        if (user.getCredentials() == null ||
                !encoder.matches(password, user.getCredentials().getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username/email or password");
        }

        return user;
    }
}