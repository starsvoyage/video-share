package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.membershipPlan.MembershipPlanRequest;
import edu.arizona.videoshare.dto.membershipPlan.MembershipPlanResponse;
import edu.arizona.videoshare.service.MembershipPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/membership-plans")
public class MembershipPlanController {
    private final MembershipPlanService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipPlanResponse create(@Valid @RequestBody MembershipPlanRequest req) {
        return MembershipPlanResponse.of(service.createPlan(req));
    }

    @GetMapping
    public List<MembershipPlanResponse> getAll() {
        return service.getAllActivePlans()
                .stream()
                .map(MembershipPlanResponse::of)
                .toList();
    }

    @GetMapping("/{id}")
    public MembershipPlanResponse getById(@PathVariable Long id) {
        return MembershipPlanResponse.of(service.getById(id));
    }

    @PutMapping("/{id}")
    public MembershipPlanResponse update(@PathVariable Long id, @Valid @RequestBody MembershipPlanRequest req) {
        return MembershipPlanResponse.of(service.updatePlan(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deletePlan(id);
    }
}
