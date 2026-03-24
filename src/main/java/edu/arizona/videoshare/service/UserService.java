package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.user.RegisterForm;
import edu.arizona.videoshare.dto.user.UserRequest;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.repository.UserRepository;
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
    private final AuthService authService;

    @Transactional
    public User register(UserRequest req) {

        RegisterForm form = new RegisterForm();
        form.setUsername(req.username);
        form.setEmail(req.email);
        form.setPassword(req.password);
        form.setConfirmPassword(req.password);

        return authService.register(form);
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
