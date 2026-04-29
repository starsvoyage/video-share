package edu.arizona.videoshare.dto.membershipPlan;

import edu.arizona.videoshare.model.entity.MembershipPlan;
import lombok.Data;

@Data
public class MembershipPlanResponse {
    private Long id;
    private String code;
    private String name;
    private int cost;
    private boolean adFree;
    private boolean active;
    private boolean hd4KPlayback;

    public static MembershipPlanResponse of(MembershipPlan p) {
        MembershipPlanResponse r = new MembershipPlanResponse();
        r.id = p.getId();
        r.code = p.getCode();
        r.name = p.getName();
        r.cost = p.getCost();
        r.adFree = p.isAdFree();
        r.active = p.isActive();
        r.hd4KPlayback = p.isHd4KPlayback();
        return r;
    }
}
