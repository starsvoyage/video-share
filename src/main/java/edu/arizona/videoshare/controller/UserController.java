package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.user.UserRequest;
import edu.arizona.videoshare.dto.user.UserResponse;
import edu.arizona.videoshare.exception.ForbiddenException;
import edu.arizona.videoshare.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    private void requireAdmin(HttpServletRequest request) {
        Object roleObj = request.getSession().getAttribute("loggedInRole");

        if (roleObj == null) {
            throw new ForbiddenException("Authentication required");
        }

        if (!roleObj.toString().equals("ADMIN")) {
            throw new ForbiddenException("Admin access required");
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody UserRequest req) {
        return UserResponse.of(service.register(req));
    }

    @GetMapping
    public List<UserResponse> getAll(HttpServletRequest request) {
        requireAdmin(request);

        return service.getAll()
                .stream()
                .map(UserResponse::of)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return UserResponse.of(service.getById(id));
    }

    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest req
    ) {
        return UserResponse.of(service.update(id, req));
    }

    @PutMapping("/{id}/suspend")
    public UserResponse suspendUser(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        return UserResponse.of(service.suspend(id));
    }

    @PutMapping("/{id}/unlock")
    public UserResponse unlockUser(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        return UserResponse.of(service.unlock(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        service.delete(id);
    }
}