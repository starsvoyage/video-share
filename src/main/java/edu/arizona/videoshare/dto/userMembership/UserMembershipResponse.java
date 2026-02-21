package edu.arizona.videoshare.dto.userMembership;

import edu.arizona.videoshare.model.UserMembership;
import edu.arizona.videoshare.model.enums.MembershipStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMembershipResponse {
    public Long id;
    public Long userId;
    public Long planId;
    public String planCode;
    public String planName;
    public MembershipStatus status;
    public LocalDateTime startAt;
    public LocalDateTime endAt;
    public boolean autoRenew;
    public LocalDateTime createdAt;

    public static UserMembershipResponse of(UserMembership m) {
        var r = new UserMembershipResponse();
        r.id = m.getId();
        r.userId = m.getUserId();
        r.planId = m.getMembershipPlan().getId();
        r.planCode = m.getMembershipPlan().getCode();
        r.planName = m.getMembershipPlan().getName();
        r.status = m.getStatus();
        r.startAt = m.getStartAt();
        r.endAt = m.getEndAt();
        r.autoRenew = m.isAutoRenew();
        r.createdAt = m.getCreatedAt();
        return r;
    }
}