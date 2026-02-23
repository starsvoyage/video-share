package edu.arizona.videoshare.dto.notification;

import edu.arizona.videoshare.model.enums.NotificationType;
import edu.arizona.videoshare.model.enums.SourceType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequest {
    
    @NotNull
    private Long recipientId;

    private Long actorUserId;

    @NotNull
    @Enumerated
    private NotificationType type;

    @NotNull
    private String message;

    @NotNull
    @Enumerated
    private SourceType sourceType;
    
    private Long sourceId;

}
