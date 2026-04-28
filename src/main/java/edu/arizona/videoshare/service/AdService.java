package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import edu.arizona.videoshare.repository.AdRepository;
import edu.arizona.videoshare.model.entity.Ad;
import edu.arizona.videoshare.model.enums.AdPlacement;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;

    // Create Ad
    public Ad create(Ad ad) {
        validate(ad);
        validateSchedule(ad);
        return adRepository.save(ad);
    }

    // Get Ad by ID
    public Ad get(Long id) {
        return adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ad not found: " + id));
    }

    // Get All Ads
    public List<Ad> getAll() {
        return adRepository.findAll();
    }

    // Get all ads for videos owned by a specific user
    public List<Ad> getByUserId(Long userId) {
        return adRepository.findByVideo_Owner_Id(userId);
    }

    // Delete Ad
    public void delete(Long id) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ad not found: " + id));
        adRepository.delete(ad);
    }

    //Check if an Ad is active based on current time and its start/end time
    public boolean isAdActiveNow(Ad ad) {
        LocalDateTime now = LocalDateTime.now();
        if (ad.getStartAt() == null || ad.getEndAt() == null) {
            return false;
        }

        if (now.isAfter(ad.getStartAt()) && now.isBefore(ad.getEndAt())) {
            return true;
        }

        return false;
    }

    public Ad selectActiveAd(AdPlacement placement) {
        List<Ad> activeAds = adRepository.findAll().stream()
            .filter(ad -> placement == null || ad.getPlacement() == placement)
            .filter(ad -> ad.isActive() && isAdActiveNow(ad))
            .collect(Collectors.toList());

        if (activeAds.isEmpty()) {
            return null;
        }

        Collections.shuffle(activeAds);
        return activeAds.get(0);
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

    private void validateSchedule(Ad ad) {
        if (ad.getStartAt() != null
                && ad.getEndAt() != null
                && !ad.getEndAt().isAfter(ad.getStartAt())) {
            throw new IllegalArgumentException("Ad end time must be after the start time.");
        }
    }

    private void validate(Ad ad) {
        List<String> errors = new ArrayList<>();

        if (ad.getTitle() == null || ad.getTitle().isBlank()) {
            errors.add("Ad title is required.");
        }
        if (ad.getMediaUrl() == null || ad.getMediaUrl().isBlank()) {
            errors.add("Ad media URL is required.");
        }
        if (ad.getDuration() < 0) {
            errors.add("Ad duration must be 0 or greater.");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", errors));
        }
    }
}
