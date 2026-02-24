package edu.arizona.videoshare.dto.membershipPlan;

import edu.arizona.videoshare.model.entity.MembershipPlan;
import lombok.Data;

@Data
public class MembershipPlanResponse {
    public Long id;
    public String code;
    public String name;

    public int cost;

    public boolean adFree;
    public boolean active;
    public boolean hd4KPlayback;

    public static MembershipPlanResponse of(MembershipPlan p) {
        var r = new MembershipPlanResponse();
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
