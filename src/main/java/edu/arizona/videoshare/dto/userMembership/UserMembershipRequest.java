package edu.arizona.videoshare.dto.userMembership;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMembershipRequest {

    @NotNull
    private Long membershipPlanId;

    @NotNull
    private Boolean autoRenew;
}
