package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.PaymentInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentInformationRepository extends JpaRepository<PaymentInformation, Long> {
    List<PaymentInformation> findByUserIdAndActiveTrueOrderByDefaultPaymentMethodDescCreatedAtDesc(Long userId);

    Optional<PaymentInformation> findByIdAndUserIdAndActiveTrue(Long id, Long userId);

    Optional<PaymentInformation> findByUserIdAndDefaultPaymentMethodTrueAndActiveTrue(Long userId);
}