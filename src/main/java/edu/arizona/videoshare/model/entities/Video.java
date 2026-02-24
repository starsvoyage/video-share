package edu.arizona.videoshare.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import edu.arizona.videoshare.model.VideoVisibility;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String videoUrl;

    private int duration;

    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private VideoVisibility visibility;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }
    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }
    public void setDuration(int duration) { this.duration = duration; }
    public int getDuration() { return duration; }
    public void setVisibility(VideoVisibility visibility) { this.visibility = visibility; }
    public VideoVisibility getVisibility() { return visibility; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public String getVideoUrl() { return videoUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
