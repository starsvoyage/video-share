package edu.arizona.videoshare.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name= "membership_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; //TODO: identifies code of membership type, codes and types tbd

    @Column(nullable = false)
    private String name; //name of plan type

    @Column(nullable = false)
    private int cost; //monthly cost in cents

    @Column(nullable = false)
    private boolean adFree; //true if membership is an ad free tier

    @Column(nullable = false)
    private boolean active; //true if membership tier is available

    @Column(nullable = false)
    private boolean hd4KPlayback; //true if membership tier includes 4K high definition playback
}