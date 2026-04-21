package edu.arizona.videoshare.service;

import edu.arizona.videoshare.exception.NotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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


}
