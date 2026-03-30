package edu.arizona.videoshare.service;

import java.util.List;

import edu.arizona.videoshare.dto.notification.NotificationResponse;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.enums.NotificationType;
import edu.arizona.videoshare.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import edu.arizona.videoshare.model.entity.Notification;
import edu.arizona.videoshare.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getFeed(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public NotificationResponse markRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found: " + id));

        n.setRead(true);
        Notification saved = notificationRepository.save(n);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getByType(Long userId, NotificationType type) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        return notificationRepository
                .findByRecipientIdAndTypeOrderByCreatedAtDesc(userId, type)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    // ── ADDED: DTO mapping helper (Pure Fabrication — keeps mapping out of controller) ──
    private NotificationResponse toResponse(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setType(n.getType());
        r.setMessage(n.getMessage());
        r.setCreatedAt(n.getCreatedAt());
        r.setRead(n.isRead());
        r.setSourceType(n.getSourceType());
        return r;
    }
}
