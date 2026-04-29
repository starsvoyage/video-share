package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.exception.response.ApiError;
import edu.arizona.videoshare.model.entity.Ad;
import edu.arizona.videoshare.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    // POST /api/ads
    @PostMapping
    public Ad create(@RequestBody Ad ad) {
        return adService.create(ad);
    }

    // GET /api/ads/{id}
    @GetMapping("/{id}")
    public Ad get(@PathVariable Long id) {
        return adService.get(id);
    }

    // GET /api/ads
    @GetMapping
    public List<Ad> getAll(@RequestParam(required = false) Long userId) {
        return userId == null
                ? adService.getAll()
                : adService.getByUserId(userId);
    }

    // GET /api/ads/user/{userId}
    @GetMapping("/user/{userId}")
    public List<Ad> getByUser(@PathVariable Long userId) {
        return adService.getByUserId(userId);
    }

    // DELETE /api/ads/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adService.delete(id);
    }

    // GET /api/ads/videos/{videoId}/ads?isPremium=true/false
    @GetMapping("/videos/{videoId}/ads")
    public List<Ad> getAdsForVideo(
            @PathVariable Long videoId,
            @RequestParam boolean isPremium) {
        return adService.getAdsForVideo(videoId, isPremium);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Request body is invalid. Check field names, formats, and enum values.",
                List.of());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                List.of());
        return ResponseEntity.badRequest().body(body);
    }
}
