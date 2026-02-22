package edu.arizona.videoshare.dto.userMembership;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMembershipRequest {

    @NotNull
    public Long userId;

    @NotNull
    public Long membershipPlanId;

    public LocalDateTime startAt;
    public LocalDateTime endAt;

    @NotNull
    public Boolean autoRenew;
}
