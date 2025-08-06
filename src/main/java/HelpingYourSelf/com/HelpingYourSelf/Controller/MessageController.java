package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.DiscussionResponse;
import HelpingYourSelf.com.HelpingYourSelf.DTO.MessageRequest;
import HelpingYourSelf.com.HelpingYourSelf.DTO.MessageResponse;
import HelpingYourSelf.com.HelpingYourSelf.DTO.UserSummary;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Security.CustomUserDetails;
import HelpingYourSelf.com.HelpingYourSelf.Service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Message;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // ✅ Envoie d’un message
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal CustomUserDetails currentUserDetails,
                                         @Valid @ModelAttribute MessageRequest request) {
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

    @GetMapping("/discussions")
    public ResponseEntity<?> getMyGroupedMessages(@AuthenticationPrincipal(expression = "user") User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Non authentifié");
        }

        Map<User, List<Message>> grouped = messageService.getGroupedDiscussions(currentUser);

        List<DiscussionResponse> discussions = grouped.entrySet().stream()
                .map(entry -> new DiscussionResponse(
                        new UserSummary(
                                entry.getKey().getId(),
                                entry.getKey().getPrenom(),
                                entry.getKey().getNom(),
                                entry.getKey().getProfileImage(),
                                entry.getKey().getPhone()
                        ),
                        entry.getValue()
                ))
                .toList();

        return ResponseEntity.ok(discussions);
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<?> markAsRead(@AuthenticationPrincipal CustomUserDetails currentUserDetails,
                                        @PathVariable Long messageId) {
        if (currentUserDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            messageService.markMessageAsRead(messageId, currentUserDetails.getUser());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }


}