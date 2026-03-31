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

    //Creating a notification, but only if the recipient and actor are not the same user
    public Notification createNotification(Notification notification) {

        if (notification.getRecipient() == null && notification.getActorUser() == null) {
            return null;
        }

        if(notification.getRecipient().getId().equals(notification.getActorUser().getId())) {
            return null;
        }

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
