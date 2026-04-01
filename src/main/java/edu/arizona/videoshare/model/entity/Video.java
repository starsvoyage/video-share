package edu.arizona.videoshare.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import edu.arizona.videoshare.model.enums.VideoVisibility;

import java.time.LocalDateTime;

/**
 * Video entity
 *
 * Minimal implementation so other domains (like PlaylistVideo) can reference
 * it.
 */
@Getter
@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "media_url", length = 1000)
    private String mediaUrl;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", foreignKey = @ForeignKey(name = "fk_videos_owner_user"))
    private User owner;

    // Added this to make the channel and subscription entities work
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    //For the file path, when we need to access the video file
    @Setter
    private String filePath;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VideoVisibility visibility = VideoVisibility.PUBLIC;

    @Setter
    @Min(0)
    @Column(nullable = false)
    private int duration = 0;

    public Video() {}

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
