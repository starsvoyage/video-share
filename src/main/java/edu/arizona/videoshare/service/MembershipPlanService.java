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
        String code = req.getCode().trim().toUpperCase();

        if (plans.existsByCode(code)) {
            throw new ConflictException("Exception: plan code already exists: " + code);
        }

        MembershipPlan plan = MembershipPlan.builder()
                .code(code)
                .name(req.getName().trim())
                .cost(req.getCost())
                .adFree(req.getAdFree())
                .active(req.getActive())
                .hd4KPlayback(req.getHd4KPlayback())
                .build();

        return plans.save(plan);
    }

    @Transactional(readOnly = true)
    public List<MembershipPlan> getAllActivePlans() {
        return plans.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<MembershipPlan> getAllPlans() {
        return plans.findAll();
    }

    @Transactional(readOnly = true)
    public MembershipPlan getById(Long id) {
        return plans.findById(id)
                .orElseThrow(() -> new NotFoundException("Exception: membership plan not found: " + id));
    }

    @Transactional
    public MembershipPlan updatePlan(Long id, MembershipPlanRequest req) {
        MembershipPlan plan = plans.findById(id)
                .orElseThrow(() -> new NotFoundException("Exception: membership plan not found: " + id));

        if (req.getCode() != null) {
            String newCode = req.getCode().trim().toUpperCase();

            if (!newCode.equals(plan.getCode()) && plans.existsByCode(newCode)) {
                throw new ConflictException("Exception: plan code already exists: " + newCode);
            }

            plan.setCode(newCode);
        }

        if (req.getName() != null) {
            plan.setName(req.getName().trim());
        }

        if (req.getCost() != null) {
            plan.setCost(req.getCost());
        }

        if (req.getAdFree() != null) {
            plan.setAdFree(req.getAdFree());
        }

        if (req.getActive() != null) {
            plan.setActive(req.getActive());
        }

        if (req.getHd4KPlayback() != null) {
            plan.setHd4KPlayback(req.getHd4KPlayback());
        }

        return plans.save(plan);
    }

    @Transactional
    public void deletePlan(Long id) {
        MembershipPlan plan = plans.findById(id)
                .orElseThrow(() -> new NotFoundException("Exception: membership plan not found: " + id));

        plan.setActive(false);
        plans.save(plan);
    }
}
