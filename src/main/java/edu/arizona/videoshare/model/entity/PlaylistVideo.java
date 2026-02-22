package edu.arizona.videoshare.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * PlaylistVideo entity
 *
 * Represents one video inside one playlist.
 *
 * - PlaylistVideo > Video (M:1)
 * - PlaylistVideo > Playlist (M:1)
 */
@Getter
@Entity
@Table(
        name = "playlist_videos",
        uniqueConstraints = {
                // A single playlist should not contain the same video multiple times.
                @UniqueConstraint(
                        name = "uk_playlist_videos_playlist_video",
                        columnNames = {"playlist_id", "video_id"}
                )
        }
)
public class PlaylistVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Order in playlist */
    @Setter
    @Column(nullable = false)
    private int position;

    /** Foreign key: video_id */
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "video_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_playlist_videos_video")
    )
    private Video video;

    /** Foreign key: playlist_id */
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "playlist_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_playlist_videos_playlist")
    )
    private Playlist playlist;

    public PlaylistVideo() {}
}
