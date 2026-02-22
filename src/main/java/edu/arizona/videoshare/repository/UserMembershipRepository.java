package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entities.UserMembership;
import edu.arizona.videoshare.model.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {
    List<UserMembership> findByUserId(Long userId);
    Optional<UserMembership> findByUserIdAndStatus(Long userId, MembershipStatus status);

    boolean existsByUserIdAndStatus(Long userId, MembershipStatus status);
}
