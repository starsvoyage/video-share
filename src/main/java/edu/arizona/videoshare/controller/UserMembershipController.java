package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.userMembership.PlaybackAccessResponse;
import edu.arizona.videoshare.dto.userMembership.UserMembershipRequest;
import edu.arizona.videoshare.dto.userMembership.UserMembershipResponse;
import edu.arizona.videoshare.service.UserMembershipService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/memberships")
public class UserMembershipController {

    private final UserMembershipService service;

    @PostMapping("/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public UserMembershipResponse subscribe(
            @Valid @RequestBody UserMembershipRequest req,
            HttpSession session
    ) {
        Long userId = getLoggedInUserId(session);
        return UserMembershipResponse.of(service.subscribe(userId, req));
    }

    @GetMapping("/current")
    public UserMembershipResponse getCurrent(HttpSession session) {
        Long userId = getLoggedInUserId(session);
        return UserMembershipResponse.of(service.getCurrentMembership(userId));
    }

    @GetMapping("/history")
    public List<UserMembershipResponse> getHistory(HttpSession session) {
        Long userId = getLoggedInUserId(session);
        return service.getMembershipHistory(userId)
                .stream()
                .map(UserMembershipResponse::of)
                .toList();
    }

    @GetMapping("/playback-access")
    public PlaybackAccessResponse getPlaybackAccess(HttpSession session) {
        Long userId = getLoggedInUserId(session);
        return service.checkPlaybackAccess(userId);
    }

    @PostMapping("/current/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelCurrentMembership(HttpSession session) {
        Long userId = getLoggedInUserId(session);
        service.cancelMembershipByUserId(userId);
    }

    @GetMapping("/{id}")
    public UserMembershipResponse getById(@PathVariable Long id) {
        return UserMembershipResponse.of(service.getById(id));
    }

    @PutMapping("/{id}")
    public UserMembershipResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UserMembershipRequest req
    ) {
        return UserMembershipResponse.of(service.updateMembership(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        service.cancelMembership(id);
    }

    private Long getLoggedInUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) {
            throw new IllegalStateException("No logged-in user found in session");
        }
        return userId;
    }
}
