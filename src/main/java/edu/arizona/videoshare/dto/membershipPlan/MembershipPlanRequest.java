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
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Min(0)
    private Integer cost;

    @NotNull
    private Boolean adFree;

    @NotNull
    private Boolean active;

    @NotNull
    private Boolean hd4KPlayback;
}
