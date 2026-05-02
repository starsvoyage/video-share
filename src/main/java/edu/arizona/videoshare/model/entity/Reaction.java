package edu.arizona.videoshare.model.entity;

import java.time.LocalDateTime;

import edu.arizona.videoshare.model.enums.ReactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reactions",
        uniqueConstraints = {@UniqueConstraint(name = "uq_reaction_user_video",
                                               columnNames = {"user_id", "video_id"}),
                             @UniqueConstraint(name = "uq_reaction_user_comment",
                                               columnNames = {"user_id", "comment_id"})},
        indexes = {@Index(name = "idx_reactions_user", columnList = "user_id"),
                   @Index(name = "idx_reactions_video", columnList = "video_id"),
                   @Index(name = "idx_reactions_comment", columnList = "comment_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // reacting user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Temp: video entity not finalized yet, store FK only
    @Column(name = "video_id")
    private Long videoId;
    /*
     * target video - commented until video is done
     * 
     * @ManyToOne(fetch = FetchType.LAZY)
     * 
     * @JoinColumn(name = "video_id")
     * private Video video;
     */

    // target comment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isValidTarget() {
        boolean hasVideo = (videoId != null);
        boolean hasComment = (comment != null);
        return hasVideo ^ hasComment;
    }
}
