package HelpingYourSelf.com.HelpingYourSelf.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(origins = "http://localhost:8100")
@Controller
public class AuthRedirectController {

    @GetMapping("/auth-success")
    public String authSuccess(@AuthenticationPrincipal OAuth2User user, Model model) {
        model.addAttribute("name", user.getAttribute("name"));
        model.addAttribute("email", user.getAttribute("email"));
        return "index"; // Celaa  va afficher templates/index.html
    }
}
