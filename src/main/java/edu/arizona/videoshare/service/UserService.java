package edu.arizona.videoshare.service;

import org.springframework.web.multipart.MultipartFile;
import edu.arizona.videoshare.dto.user.RegisterForm;
import edu.arizona.videoshare.dto.user.UserRequest;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.enums.UserStatus;
import edu.arizona.videoshare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
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
     * UPDATE: Marks a user account as deactivated without removing persisted data.
     */
    @Transactional
    public User deactivate(Long id) {
        User user = users.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        user.setStatus(UserStatus.DELETED);
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

    @Transactional
    public User updateAccountInformation(Long userId, String displayName, String bio, MultipartFile avatar) throws IOException {
        User user = users.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        user.setDisplayName(displayName.trim());

        if (bio == null || bio.trim().isEmpty()) {
            user.setBio(null);
        } else {
            user.setBio(bio.trim());
        }

        if (avatar != null && !avatar.isEmpty()) {

            long maxSize = 5 * 1024 * 1024;
            if (avatar.getSize() > maxSize) {
                throw new IllegalArgumentException("Avatar image must be smaller than 5MB.");
            }

            String contentType = avatar.getContentType();
            if (contentType == null ||
                    !(contentType.equals("image/png")
                            || contentType.equals("image/jpeg")
                            || contentType.equals("image/jpg")
                            || contentType.equals("image/webp"))) {
                throw new IllegalArgumentException("Only PNG, JPG, JPEG, or WEBP images are allowed.");
            }

            String originalFilename = avatar.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;

            Path uploadDir = Paths.get(
                    System.getProperty("user.dir"),
                    "uploads",
                    "user-avatars"
            ).toAbsolutePath().normalize();

            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(fileName);
            avatar.transferTo(filePath.toFile());

            user.setAvatarUrl("/uploads/user-avatars/" + fileName);
        }

        return users.save(user);
    }
}
