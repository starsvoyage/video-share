package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.userMembership.UserMembershipRequest;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.MembershipPlan;
import edu.arizona.videoshare.model.UserMembership;
import edu.arizona.videoshare.model.enums.MembershipStatus;
import edu.arizona.videoshare.repository.MembershipPlanRepository;
import edu.arizona.videoshare.repository.UserMembershipRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserMembershipService {

    private final UserMembershipRepository memberships;
    private final MembershipPlanRepository plans;

    @Transactional
    public UserMembership subscribe(UserMembershipRequest req) {
        if (memberships.existsByUserIdAndStatus(req.userId, MembershipStatus.ACTIVE)) {
            throw new ConflictException("Exception: user already has an active membership: " + req.userId);
        }

        MembershipPlan plan = plans.findById(req.membershipPlanId).orElseThrow(() -> new NotFoundException("Exception: membership plan not found: " + req.membershipPlanId));

        if (!plan.isActive()) {
            throw new ConflictException("Exception: membership plan is not available: " + plan.getCode());
        }

        UserMembership membership = UserMembership.builder()
                .userId(req.userId)
                .membershipPlan(plan)
                .status(MembershipStatus.ACTIVE)
                .startAt(req.startAt != null ? req.startAt : LocalDateTime.now())
                .endAt(req.endAt)
                .autoRenew(req.autoRenew)
                .build();

        return memberships.save(membership);
    }

    @Transactional(readOnly = true)
    public UserMembership getCurrentMembership(Long userId) {
        return memberships.findByUserIdAndStatus(userId, MembershipStatus.ACTIVE).orElseThrow(() -> new NotFoundException("Exception: no active membership for user: " + userId));
    }

    @Transactional(readOnly = true)
    public UserMembership getById(Long id) {
        return memberships.findById(id).orElseThrow(() -> new NotFoundException("Exception: membership not found: " + id));
    }

    @Transactional
    public UserMembership updateMembership(Long id, UserMembershipRequest req) {
        UserMembership membership = memberships.findById(id).orElseThrow(() -> new NotFoundException("Exception: membership not found: " + id));

        if (req.autoRenew != null) membership.setAutoRenew(req.autoRenew);
        if (req.endAt != null)     membership.setEndAt(req.endAt);

        return memberships.save(membership);
    }

    @Transactional
    public void cancelMembership(Long id) {
        UserMembership membership = memberships.findById(id).orElseThrow(() -> new NotFoundException("Exceptino: membership not found: " + id));

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new ConflictException("Membership inactive: " + id);
        }

        membership.setStatus(MembershipStatus.CANCELED);
        membership.setEndAt(LocalDateTime.now());
        memberships.save(membership);
    }
}
