package edu.arizona.videoshare.dto.userMembership;

import edu.arizona.videoshare.model.entity.UserMembership;
import edu.arizona.videoshare.model.enums.MembershipStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMembershipResponse {
    private Long id;
    private Long userId;
    private Long planId;
    private String planCode;
    private String planName;
    private MembershipStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private boolean autoRenew;
    private LocalDateTime createdAt;
    private int amountPaid;

    public static UserMembershipResponse of(UserMembership m) {
        UserMembershipResponse r = new UserMembershipResponse();
        r.id = m.getId();
        r.userId = m.getUser().getId();
        r.planId = m.getMembershipPlan().getId();
        r.planCode = m.getMembershipPlan().getCode();
        r.planName = m.getMembershipPlan().getName();
        r.amountPaid = m.getMembershipPlan().getCost();
        r.status = m.getStatus();
        r.startAt = m.getStartAt();
        r.endAt = m.getEndAt();
        r.autoRenew = m.isAutoRenew();
        r.createdAt = m.getCreatedAt();
        return r;
    }
}