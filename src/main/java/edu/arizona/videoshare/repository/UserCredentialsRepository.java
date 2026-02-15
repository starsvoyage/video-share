package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserCredentialsRepository
 *
 * Persistence abstraction for UserCredentials entities.
 */
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {
}