package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.user.UserRequest;
import edu.arizona.videoshare.dto.user.UserResponse;
import edu.arizona.videoshare.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * UserController (Presentation Layer)
 *
 * Exposes REST endpoints for managing User resources.
 * Base path: /api/users
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

//    public UserController(UserService service) {
//        this.service = service;
//    }

    /**
     * POST /api/users
     * Registers a new user.
     * HTTP 201 Created on success.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody UserRequest req) {
        return UserResponse.of(service.register(req));
    }

    /**
     * GET /api/users
     * Returns all users. List of DTOs.
     */
    @GetMapping
    public List<UserResponse> getAll() {
        return service.getAll()
                .stream()
                .map(UserResponse::of)
                .toList();
    }

    /**
     * GET /api/users/{id}
     * Returns a single user by id.
     * Mapped globally to HTTP 404
     */
    @GetMapping("/{id}")
    public UserResponse getById(
            @PathVariable Long id) {
        return UserResponse.of(service.getById(id));
    }

    /**
     * PUT /api/users/{id}
     * Updates profile fields for a user.
     * HTTP 200 OK on success.
     */
    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest req
    ) {
        return UserResponse.of(service.update(id, req));
    }

    /**
     * DELETE /api/users/{id}
     * Deletes a user and cascades removal of associated credentials.
     * HTTP 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {
        service.delete(id);
    }
}