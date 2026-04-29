package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.UserMembership;
import edu.arizona.videoshare.model.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {

    List<UserMembership> findByUserId(Long userId);

    Optional<UserMembership> findByUserIdAndStatus(Long userId, MembershipStatus status);

    List<UserMembership> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<UserMembership> findByUserIdOrderByStartAtDesc(Long userId);

    Optional<UserMembership> findFirstByUserIdAndStatusOrderByStartAtDesc(Long userId, MembershipStatus status);

    Optional<UserMembership> findFirstByUserIdOrderByStartAtDesc(Long userId);

    boolean existsByUserIdAndStatus(Long userId, MembershipStatus status);

    List<UserMembership> findByStatusAndEndAtBefore(MembershipStatus status, LocalDateTime time);
}