package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.userMembership.UserMembershipRequest;
import edu.arizona.videoshare.dto.userMembership.UserMembershipResponse;
import edu.arizona.videoshare.service.UserMembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/memberships")
public class UserMembershipController {
    private final UserMembershipService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserMembershipResponse subscribe(@Valid @RequestBody UserMembershipRequest req) {
        return UserMembershipResponse.of(service.subscribe(req));
    }

    @GetMapping("/api/users/{userId}/membership")
    public UserMembershipResponse getCurrent(@PathVariable Long userId) {
        return UserMembershipResponse.of(service.getCurrentMembership(userId));
    }

    @GetMapping("/{id}")
    public UserMembershipResponse getById(@PathVariable Long id) {
        return UserMembershipResponse.of(service.getById(id));
    }

    @PutMapping("/{id}")
    public UserMembershipResponse update(@PathVariable Long id, @Valid @RequestBody UserMembershipRequest req) {
        return UserMembershipResponse.of(service.updateMembership(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        service.cancelMembership(id);
    }
}
