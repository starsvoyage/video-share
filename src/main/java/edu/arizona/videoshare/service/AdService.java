package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import edu.arizona.videoshare.repository.AdRepository;
import edu.arizona.videoshare.model.entity.Ad;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;

    // Create Ad
    public Ad create(Ad ad) {
        return adRepository.save(ad);
    }

    // Get Ad by ID
    public Ad get(Long id) {
        return adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ad not found"));
    }

    // Get All Ads
    public List<Ad> getAll() {
        return adRepository.findAll();
    }

    // Delete Ad
    public void delete(Long id) {
        adRepository.deleteById(id);
    }
    
    // get all active ads for a video (if user is non-premium)
    public List<Ad> getAdsForVideo(Long videoId, boolean isPremium) {

        // Premium users → no ads
        if (isPremium) {
            return List.of();
        }

        // Non-premium → return only active ads for that video
        return adRepository.findByVideoId(videoId)
                .stream()
                .filter(Ad::isActive)
                .toList();
    }
}
