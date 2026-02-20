package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.user.UserRequest;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.UserCredentials;
import edu.arizona.videoshare.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * UserService (Business Layer)
 *
 * This class implements business use-cases around User accounts.
 *
 */
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;

//    public UserService(UserRepository users, BCryptPasswordEncoder encoder) {
//        this.users = users;
//        this.encoder = encoder;
//    }

    /**
     * CREATE: Registers a new user account.
     */
    @Transactional
    public User register(UserRequest req) {

        // Business rule: username/email unique
        if (users.existsByUsername(req.username)) {
            throw new ConflictException("Username already exists");
        }
        if (users.existsByEmail(req.email)) {
            throw new ConflictException("Email already exists");
        }

        // Create user profile
        User u = new User();
        u.setUsername(req.username.trim());
        u.setEmail(req.email.trim().toLowerCase());
        u.setDisplayName(req.displayName.trim());

        // Create credentials
        // Because UserCredentials uses @MapsId, credentials will share the same PK as the user.
        UserCredentials creds = new UserCredentials();
        creds.setPasswordHash(encoder.encode(req.password));

        // attachCredentials keeps both sides of the 1:1 mapping consistent in memory
        u.attachCredentials(creds);

        // Saving user cascades creds due to cascade=ALL on User.credentials
        return users.save(u);
    }

    /**
     * READ: Returns all users.
     */
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return users.findAll();
    }

    /**
     * READ: Returns a user by id.
     */
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    /**
     * UPDATE: Updates mutable profile fields for a user.
     */
    @Transactional
    public User update(Long id, UserRequest req) {
        User user = users.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        // Only update fields present in the request
        if (req.displayName != null) user.setDisplayName(req.displayName.trim());
        if (req.bio != null) user.setBio(req.bio.trim());
        if (req.avatarUrl != null) user.setAvatarUrl(req.avatarUrl.trim());

        return users.save(user);
    }

    /**
     * DELETE: Deletes a user by id.
     */
    @Transactional
    public void delete(Long id) {
        if (!users.existsById(id)) {
            throw new NotFoundException("User not found: " + id);
        }
        users.deleteById(id);
    }

}