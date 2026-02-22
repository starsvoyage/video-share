package edu.arizona.videoshare.dto.user;

import edu.arizona.videoshare.model.enums.UserRole;
import edu.arizona.videoshare.model.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserResponse DTO
 *
 * Represents outward-facing user data returned by the API.
 *
 * Important:
 * Does NOT include passwordHash or credential data.
 * Only exposes profile + system metadata fields.
 */
@Data
public class UserResponse {
    /**
     * Database-generated primary key.
     */
    public Long id;

    /**
     * Public username (unique).
     */
    public String username;

    /**
     * Account email.
     */
    public String email;

    /**
     * Display name shown in UI.
     */
    public String displayName;

    /**
     * Account status (ACTIVE, SUSPENDED, DELETED)
     */
    public UserStatus status;

    /**
     * System role (VIEWER, CREATOR, MOD, ADMIN)
     */
    public UserRole role;

    /**
     * Audit timestamps.
     */
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    /**
     * Maps a User entity to a response DTO.
     */
    public static UserResponse of(edu.arizona.videoshare.model.entity.User u) {
        var r = new UserResponse();
        r.id = u.getId();
        r.username = u.getUsername();
        r.email = u.getEmail();
        r.displayName = u.getDisplayName();
        r.status = u.getStatus();
        r.role = u.getRole();
        r.createdAt = u.getCreatedAt();
        r.updatedAt = u.getUpdatedAt();
        return r;
    }
}
