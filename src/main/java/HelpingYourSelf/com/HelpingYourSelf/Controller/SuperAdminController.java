package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.RegisterRequest;
import HelpingYourSelf.com.HelpingYourSelf.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final AuthService authService;

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createSuperAdmin(@RequestBody RegisterRequest request) {
        try {
            authService.createSuperAdmin(request);
            return ResponseEntity.ok("SuperAdmin créé avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
