package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserRepository
 *
 * Persistence layer abstraction for User entities.
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // Retrieves a User by email (login identifier)
    Optional<User> findByEmail(String email);
    // Retrieves a User by username (public identifier)
    Optional<User> findByUsername(String username);

    // Checks existence by email
    boolean existsByEmail(String email);
    // Checks existence by username.
    boolean existsByUsername(String username);
}