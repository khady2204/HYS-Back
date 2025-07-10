package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.SuggestionResponse;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Service.AuthService;
import HelpingYourSelf.com.HelpingYourSelf.Service.SuggestionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;
    private final AuthService authService;

    @GetMapping
    public List<SuggestionResponse> getSuggestions(HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);
        return suggestionService.getSuggestionsFor(currentUser);
    }
}
