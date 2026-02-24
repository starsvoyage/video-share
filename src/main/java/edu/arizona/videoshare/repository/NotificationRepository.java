package edu.arizona.videoshare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.arizona.videoshare.model.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(long recipientId);

    List<Notification> findByRecipientIdAndIsReadFalse(long recipientId);
}
