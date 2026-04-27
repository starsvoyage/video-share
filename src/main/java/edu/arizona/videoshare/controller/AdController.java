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

    // POST /api/v1/ads
    @PostMapping
    public Ad create(@RequestBody Ad ad) {
        return adService.create(ad);
    }

    // GET /api/v1/ads/{id}
    @GetMapping("/{id}")
    public Ad get(@PathVariable Long id) {
        return adService.get(id);
    }

    // GET /api/v1/ads
    @GetMapping
    public List<Ad> getAll() {
        return adService.getAll();
    }

    // DELETE /api/v1/ads/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adService.delete(id);
    }
    
    // GET /api/v1/videos/{videoId}/ads?isPremium=true/false
    @GetMapping("/videos/{videoId}/ads")
    public List<Ad> getAdsForVideo(
        @PathVariable Long videoId,
        @RequestParam boolean isPremium) {

    return adService.getAdsForVideo(videoId, isPremium);
}
}
