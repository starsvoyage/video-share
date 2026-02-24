package edu.arizona.videoshare.dto.notification;

import java.time.LocalDateTime;

import edu.arizona.videoshare.model.entity.Notification;
import edu.arizona.videoshare.model.enums.NotificationType;
import edu.arizona.videoshare.model.enums.SourceType;
import lombok.Data;

@Data
public class NotificationResponse {
    
    private Long id;

    private NotificationType type;

    private String message;

    private LocalDateTime createdAt;

    private boolean isRead;

    private SourceType sourceType;

    public NotificationResponse toResponse(Notification n) {
    var r = new NotificationResponse();

    r.id = n.getId();
    r.setType(n.getType());
    r.setMessage(n.getMessage());
    r.setCreatedAt(n.getCreatedAt());
    r.setRead(n.isRead());

    return r;
}
}
