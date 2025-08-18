package HelpingYourSelf.com.HelpingYourSelf.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8100")
public class AuthRedirectController {

    @GetMapping("/auth-success")
    public Map<String, Object> authSuccess(@AuthenticationPrincipal OAuth2User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("name", user.getAttribute("name"));
        response.put("email", user.getAttribute("email"));
        return response;
    }
}
