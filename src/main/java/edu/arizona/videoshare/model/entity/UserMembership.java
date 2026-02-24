package edu.arizona.videoshare.model.entity;
import edu.arizona.videoshare.model.enums.MembershipStatus;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name= "user_memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMembership {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    //TODO: Need to create many to one relationship to entity User
    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "membership_plan_id", nullable = false)
    private MembershipPlan membershipPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column
    private LocalDateTime endAt;

    @Column(nullable = false)
    private boolean autoRenew;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreated() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = MembershipStatus.ACTIVE;
        }
    }
}
