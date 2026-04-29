package edu.arizona.videoshare.model.entity;

import edu.arizona.videoshare.model.enums.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentType paymentType;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String cardholderName;

    /*
     * Only store the last 4 digits.
     * Never store the full card number.
     */
    @NotBlank
    @Pattern(regexp = "\\d{4}", message = "Last four digits must be exactly 4 numbers")
    @Column(nullable = false, length = 4)
    private String lastFourDigits;

    @NotBlank
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Expiration date must use MM/YY format")
    @Column(nullable = false, length = 5)
    private String expirationDate;

    @Column(nullable = false)
    private boolean defaultPaymentMethod;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        this.active = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}