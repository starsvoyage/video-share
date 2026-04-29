package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.userMembership.PlaybackAccessResponse;
import edu.arizona.videoshare.dto.userMembership.UserMembershipRequest;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.MembershipPlan;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.UserMembership;
import edu.arizona.videoshare.model.enums.MembershipStatus;
import edu.arizona.videoshare.repository.MembershipPlanRepository;
import edu.arizona.videoshare.repository.UserMembershipRepository;
import edu.arizona.videoshare.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMembershipService {

    private final UserMembershipRepository memberships;
    private final MembershipPlanRepository plans;
    private final UserRepository users;

    @Transactional
    public UserMembership subscribe(Long userId, UserMembershipRequest req) {
        expireMembershipIfNeeded(userId);

        User user = users.findById(userId)
                .orElseThrow(() -> new NotFoundException("Exception: user not found: " + userId));

        MembershipPlan plan = plans.findById(req.getMembershipPlanId())
                .orElseThrow(() -> new NotFoundException("Exception: membership plan not found: " + req.getMembershipPlanId()));

        if (!plan.isActive()) {
            throw new ConflictException("Exception: membership plan is not available: " + plan.getCode());
        }

        UserMembership currentActive = memberships
                .findFirstByUserIdAndStatusOrderByStartAtDesc(userId, MembershipStatus.ACTIVE)
                .orElse(null);

        LocalDateTime now = LocalDateTime.now();

        if (currentActive != null) {
            if (currentActive.getMembershipPlan().getId().equals(plan.getId())) {
                return currentActive;
            }

            currentActive.setStatus(MembershipStatus.CANCELED);
            currentActive.setEndAt(now);
            currentActive.setAutoRenew(false);
            memberships.save(currentActive);
        }

        UserMembership membership = UserMembership.builder()
                .user(user)
                .membershipPlan(plan)
                .status(MembershipStatus.ACTIVE)
                .startAt(now)
                .endAt(null)
                .autoRenew(req.getAutoRenew())
                .build();

        return memberships.save(membership);
    }

    @Transactional(readOnly = true)
public java.util.List<UserMembership> getMembershipHistory(Long userId) {
    return memberships.findByUserIdOrderByCreatedAtDesc(userId);
}

    @Transactional(readOnly = true)
    public UserMembership getCurrentMembership(Long userId) {
        expireMembershipIfNeeded(userId);

        return memberships.findFirstByUserIdAndStatusOrderByStartAtDesc(userId, MembershipStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Exception: no active membership for user: " + userId));
    }

    /**
     * Return the latest membership record regardless of status.
     */
    @Transactional
    public UserMembership getLatestMembership(Long userId) {
        expireMembershipIfNeeded(userId);

        return memberships.findFirstByUserIdOrderByStartAtDesc(userId)
                .orElseThrow(() -> new NotFoundException("Exception: no membership history for user: " + userId));
    }

    /**
     * Return full membership history for a user.
     */
    @Transactional
    public List<UserMembership> getMembershipHistory(Long userId) {
        expireMembershipIfNeeded(userId);
        return memberships.findByUserIdOrderByStartAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public UserMembership getById(Long id) {
        return memberships.findById(id)
                .orElseThrow(() -> new NotFoundException("Exception: membership not found: " + id));
    }

    /**
     * Update a membership record.
     * For now, this supports changing auto-renew only.
     */
    @Transactional
    public UserMembership updateMembership(Long id, UserMembershipRequest req) {
        UserMembership membership = memberships.findById(id)
                .orElseThrow(() -> new NotFoundException("Exception: membership not found: " + id));

        if (req.getAutoRenew() != null) {
            membership.setAutoRenew(req.getAutoRenew());
        }

        return memberships.save(membership);
    }

    /**
     * Cancel the user's currently active membership.
     */
    @Transactional
    public void cancelMembershipByUserId(Long userId) {
        UserMembership membership = memberships
                .findFirstByUserIdAndStatusOrderByStartAtDesc(userId, MembershipStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Exception: no active membership for user: " + userId));

        membership.setStatus(MembershipStatus.CANCELED);
        membership.setEndAt(LocalDateTime.now());
        membership.setAutoRenew(false);
        memberships.save(membership);
    }

    /**
     * Cancel a membership by membership record ID.
     */
    @Transactional
    public void cancelMembership(Long id) {
        UserMembership membership = memberships.findById(id)
                .orElseThrow(() -> new NotFoundException("Exception: membership not found: " + id));

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new ConflictException("Exception: membership is not active: " + id);
        }

        membership.setStatus(MembershipStatus.CANCELED);
        membership.setEndAt(LocalDateTime.now());
        membership.setAutoRenew(false);
        memberships.save(membership);
    }

    /**
     * Check playback benefits for the user's current membership.
     */
    @Transactional
    public PlaybackAccessResponse checkPlaybackAccess(Long userId) {
        expireMembershipIfNeeded(userId);

        UserMembership membership = memberships
                .findFirstByUserIdAndStatusOrderByStartAtDesc(userId, MembershipStatus.ACTIVE)
                .orElse(null);

        if (membership == null) {
            return new PlaybackAccessResponse(
                    true,
                    "No active membership. Playback allowed with default/free rules.",
                    "FREE",
                    false,
                    false
            );
        }

        MembershipPlan plan = membership.getMembershipPlan();

        return new PlaybackAccessResponse(
                true,
                "Playback allowed.",
                plan.getCode(),
                plan.isAdFree(),
                plan.isHd4KPlayback()
        );
    }

    /**
     * Expire one user's active membership if its end date has passed.
     */
    @Transactional
    public void expireMembershipIfNeeded(Long userId) {
        UserMembership membership = memberships
                .findFirstByUserIdAndStatusOrderByStartAtDesc(userId, MembershipStatus.ACTIVE)
                .orElse(null);

        if (membership == null) {
            return;
        }

        if (membership.getEndAt() != null && !membership.getEndAt().isAfter(LocalDateTime.now())) {
            membership.setStatus(MembershipStatus.EXPIRED);
            membership.setAutoRenew(false);
            memberships.save(membership);
        }
    }

    /**
     * Scheduled cleanup for active memberships that have passed endAt.
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void expireEndedMemberships() {
        List<UserMembership> expiredMemberships =
                memberships.findByStatusAndEndAtBefore(MembershipStatus.ACTIVE, LocalDateTime.now());

        for (UserMembership membership : expiredMemberships) {
            membership.setStatus(MembershipStatus.EXPIRED);
            membership.setAutoRenew(false);
        }

        memberships.saveAll(expiredMemberships);
    }
}

