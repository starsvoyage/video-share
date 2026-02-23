package edu.arizona.videoshare.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Entity
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"channels", "credentials"})
    private User subscriber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id")
    @JsonIgnoreProperties({"subscribers", "user"})
    private Channel channel;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum SubscriptionStatus {
        ACTIVE,
        CANCELLED
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    private SubscriptionStatus status;


    public Subscription() {
        
    }

    

}
