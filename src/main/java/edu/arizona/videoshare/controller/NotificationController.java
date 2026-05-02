package edu.arizona.videoshare.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.arizona.videoshare.dto.notification.NotificationRequest;
import edu.arizona.videoshare.dto.notification.NotificationResponse;
import edu.arizona.videoshare.model.enums.NotificationType;
import edu.arizona.videoshare.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public NotificationResponse create(@Valid @RequestBody NotificationRequest request) {
        return notificationService.createNotification(request);
    }

   @GetMapping("/user/{userId}/playlist-likes")
    public List<NotificationResponse> getPlaylistLikeNotifications(@PathVariable Long userId) {
        return notificationService.getByType(userId, NotificationType.LIKE_PLAYLIST);
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
