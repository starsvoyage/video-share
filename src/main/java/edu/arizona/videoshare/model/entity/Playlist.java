package edu.arizona.videoshare.model.entity;

import edu.arizona.videoshare.model.enums.Visibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Playlist entity
 *
 * Represents a personal collection of videos.

 * - Playlist > User (M:1)
 * - Playlist > PlaylistVideo (1:M)
 */
@Getter
@Entity
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Playlist display name */
    @Setter
    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String name;

    /** About text (nullable) */
    @Setter
    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /** Public / Private */
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Visibility visibility = Visibility.PRIVATE;

    /**
     * Owner of the playlist.
     * Many playlists belong to one user.
     */
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_playlists_user")
    )
    private User user;

    /**
     * Videos contained in this playlist.
     * 1 playlist -> many PlaylistVideo rows.
     */
    @OneToMany(
            mappedBy = "playlist",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("position ASC")
    private List<PlaylistVideo> items = new ArrayList<>();

    public Playlist() {}

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Helper: keep both sides of the Playlist <-> PlaylistVideo association consistent.
     */
    public void addItem(PlaylistVideo pv) {
        items.add(pv);
        pv.setPlaylist(this);
    }

    /**
     * Helper: remove item and keep both sides consistent.
     */
    public void removeItem(PlaylistVideo pv) {
        items.remove(pv);
        pv.setPlaylist(null);
    }
}
