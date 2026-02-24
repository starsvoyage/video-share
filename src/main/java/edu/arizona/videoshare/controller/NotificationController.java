package edu.arizona.videoshare.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Notification> getFeed(@PathVariable Long userId) {
        return notificationService.getFeed(userId);
    }

    @PatchMapping("/{id}/read")
    public Notification markRead(@PathVariable Long id) {
        return notificationService.markRead(id);
    }
}
