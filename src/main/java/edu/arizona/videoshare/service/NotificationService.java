package edu.arizona.videoshare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.arizona.videoshare.model.entity.Notification;
import edu.arizona.videoshare.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getFeed(Long userId) {
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public Notification markRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow();

        n.setRead(true);
        return notificationRepository.save(n);
    }
}
