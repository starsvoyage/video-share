package edu.arizona.videoshare.model.entity;

import java.time.LocalDateTime;

import edu.arizona.videoshare.model.enums.NotificationType;
import edu.arizona.videoshare.model.enums.SourceType;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="notifications", indexes={
    @Index(name="idx_recipient_id", columnList="recipient_id"),
    @Index(name="idx_actor_user_id", columnList="actor_user_id"),
    @Index(name="idx_notification_created", columnList="created_at"),
    @Index(name="idx_notification_read", columnList="is_read")
})
public class Notification {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private NotificationType type;

    private String message;

    private LocalDateTime createdAt;

    private boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private SourceType sourceType;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recipient_id", nullable=false)
    private User recipient;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="actor_user_id", nullable=true)
    private User actorUser;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        isRead = false;
    }
    
}
