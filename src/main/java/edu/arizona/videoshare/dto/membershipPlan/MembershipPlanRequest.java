package edu.arizona.videoshare.dto.membershipPlan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MembershipPlanRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    public String code;

    @NotBlank
    @Size(max = 100)
    public String name;

    @NotNull
    @Min(0)
    public Integer cost;

    @NotNull
    public Boolean adFree;

    @NotNull
    public Boolean active;

    @NotNull
    public Boolean hd4KPlayback;
}
