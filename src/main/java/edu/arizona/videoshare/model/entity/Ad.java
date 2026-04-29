package edu.arizona.videoshare.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import edu.arizona.videoshare.model.enums.AdPlacement;
import lombok.Setter;

@Entity
@Table(name = "ads")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String title;
    @Setter
    private String mediaUrl;
    @Setter
    private int duration;
    @Setter
    private boolean active;

    @Setter
    @Enumerated(EnumType.STRING)
    private AdPlacement placement;

    private LocalDateTime createdAt;

    //Start time of the ad campaign
    @Setter
    private LocalDateTime startAt;

    //End time of the ad campaign
    @Setter
    private LocalDateTime endAt;
    @Setter
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

    public String getMediaUrl() { return mediaUrl; }

    public int getDuration() { return duration; }

    public boolean isActive() { return active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getStartAt() { return startAt; }

    public LocalDateTime getEndAt() { return endAt; }

    public AdPlacement getPlacement() { return placement; }

    public Video getVideo() { return video; }
}
