package edu.arizona.videoshare.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import edu.arizona.videoshare.service.AdService;
import edu.arizona.videoshare.model.entity.Ad;

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
}
