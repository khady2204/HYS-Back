package HelpingYourSelf.com.HelpingYourSelf.Controller;

import java.security.Principal;
import java.time.Instant;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.ChatMessage;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void chat(@Payload ChatMessage message, Principal principal) {
        if ((message.getSender() == null || message.getSender().isEmpty()) && principal != null) {
            message.setSender(principal.getName());
        }
        if (message.getTimestamp() == null) {
            message.setTimestamp(Instant.now());
        }
        messagingTemplate.convertAndSend("/topic/chat", message);
    }
}

