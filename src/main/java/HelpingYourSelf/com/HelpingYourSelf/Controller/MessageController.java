package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.MessageRequest;
import HelpingYourSelf.com.HelpingYourSelf.DTO.MessageResponse;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Security.CustomUserDetails;
import HelpingYourSelf.com.HelpingYourSelf.Service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // ✅ Envoie d’un message
    @PostMapping
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal CustomUserDetails currentUserDetails,
                                         @Valid @RequestBody MessageRequest request) {
        if (currentUserDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User currentUser = currentUserDetails.getUser();

        try {
            MessageResponse response = messageService.sendMessage(currentUser, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // ✅ Récupération des messages entre deux utilisateurs
    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageResponse>> getMessages(@AuthenticationPrincipal CustomUserDetails currentUserDetails,
                                                             @PathVariable Long userId) {
        if (currentUserDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User currentUser = currentUserDetails.getUser();
        List<MessageResponse> messages = messageService.getMessagesBetweenUsers(currentUser.getId(), userId);
        return ResponseEntity.ok(messages);
    }
}
