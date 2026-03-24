package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.membershipPlan.MembershipPlanRequest;
import edu.arizona.videoshare.exception.ConflictException;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.MembershipPlan;
import edu.arizona.videoshare.repository.MembershipPlanRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MembershipPlanService {
    private final MembershipPlanRepository plans;

    @Transactional
    public MembershipPlan createPlan(MembershipPlanRequest req) {

        if (plans.existsByCode(req.code)) {
            throw new ConflictException("Exception: plan code already exists: " + req.code);
        }

        MembershipPlan plan = MembershipPlan.builder()
                .code(req.code.trim().toUpperCase())
                .name(req.name.trim())
                .cost(req.cost)
                .adFree(req.adFree)
                .active(req.active)
                .hd4KPlayback(req.hd4KPlayback)
                .build();

        return plans.save(plan);
    }

    @Transactional(readOnly = true)
    public List<MembershipPlan> getAllActivePlans() {
        return plans.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public MembershipPlan getById(Long id) {
        return plans.findById(id).orElseThrow(() -> new NotFoundException("Exception: membership plan not found: " + id));
    }

    @Transactional
    public MembershipPlan updatePlan(Long id, MembershipPlanRequest req) {
        MembershipPlan plan = plans.findById(id).orElseThrow(() -> new NotFoundException("Exception: membership plan not found: " + id));

        if (req.name != null)        plan.setName(req.name.trim());
        if (req.cost != null)        plan.setCost(req.cost);
        if (req.adFree != null)      plan.setAdFree(req.adFree);
        if (req.active != null)      plan.setActive(req.active);
        if (req.hd4KPlayback != null) plan.setHd4KPlayback(req.hd4KPlayback);

        return plans.save(plan);
    }

    @Transactional
    public void deletePlan(Long id) {
        MembershipPlan plan = plans.findById(id).orElseThrow(() -> new NotFoundException("Exception: membership plan not found: " + id));

        plan.setActive(false);
        plans.save(plan);
    }
}
