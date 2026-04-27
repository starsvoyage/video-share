package edu.arizona.videoshare.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import edu.arizona.videoshare.model.enums.AdPlacement;

@Entity
@Table(name = "ads")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String mediaUrl;
    private int duration;
    private boolean active;

    @Enumerated(EnumType.STRING)
    private AdPlacement placement;

    private LocalDateTime createdAt;

    //Start time of the ad campaign
    private LocalDateTime startAt;

    //End time of the ad campaign
    private LocalDateTime endAt;
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // getters/setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    public AdPlacement getPlacement() { return placement; }
    public void setPlacement(AdPlacement placement) { this.placement = placement; }
    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }
}
