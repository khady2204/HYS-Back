package HelpingYourSelf.com.HelpingYourSelf.Controller;

import java.security.Principal;
import java.time.Instant;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.NotificationMessage;

@Controller
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/notify")
    public void notify(@Payload NotificationMessage message, Principal principal) {
        if ((message.getSender() == null || message.getSender().isEmpty()) && principal != null) {
            message.setSender(principal.getName());
        }
        if (message.getTimestamp() == null) {
            message.setTimestamp(Instant.now());
        }
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }
}

