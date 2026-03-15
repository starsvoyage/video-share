package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByCode(String code);

    Optional<VerificationToken> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}