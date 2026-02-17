package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.MembershipPlan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    Optional<MembershipPlan> findByCode(String code);
    List<MembershipPlan> findByActiveTrue();
    boolean existsByCode(String code);
}
