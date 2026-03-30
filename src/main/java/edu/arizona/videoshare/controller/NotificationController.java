package edu.arizona.videoshare.controller;

import java.util.List;

import edu.arizona.videoshare.dto.notification.NotificationResponse;
import edu.arizona.videoshare.model.enums.NotificationType;
import org.springframework.web.bind.annotation.*;

import edu.arizona.videoshare.model.entity.Notification;
import edu.arizona.videoshare.service.NotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public Notification create(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }

    @GetMapping("/user/{userId}")
    public List<NotificationResponse> getFeed(@PathVariable Long userId) {
        return notificationService.getFeed(userId);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(@PathVariable Long id) {
        return notificationService.markRead(id);
    }

    @GetMapping("/user/{userId}/filter")
    public List<NotificationResponse> getByType(
            @PathVariable Long userId,
            @RequestParam NotificationType type) {
        return notificationService.getByType(userId, type);
    }

    @GetMapping("/user/{userId}/unread-count")
    public long getUnreadCount(@PathVariable Long userId) {
        return notificationService.getUnreadCount(userId);
    }
}
