package HelpingYourSelf.com.HelpingYourSelf.Security;

import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository; // ✅ on injecte le repo

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // ✅ Valide -> authentifie + met à jour l’activité
                if (jwtTokenProvider.validateToken(token)) {
                    String subject = jwtTokenProvider.getSubjectFromToken(token); // email ou phone selon ton generateToken
                    var userDetails = customUserDetailsService.loadUserByUsername(subject);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // ✅ Mettre à jour lastActivityAt + isOnline
                    findUserBySubject(subject).ifPresent(u -> {
                        u.setIsOnline(true);
                        u.setLastActivityAt(Instant.now());
                        userRepository.save(u);
                    });
                }
            }
        } catch (ExpiredJwtException eje) {
            // 🔴 Token expiré → on passe l’utilisateur en offline
            String subject = null;
            try {
                subject = eje.getClaims() != null ? eje.getClaims().getSubject() : null;
            } catch (Exception ignored) {}
            if (subject != null) {
                findUserBySubject(subject).ifPresent(u -> {
                    if (Boolean.TRUE.equals(u.getIsOnline())) {
                        u.setIsOnline(false);
                        u.setLastOnlineAt(Instant.now());
                        userRepository.save(u);
                    }
                });
            }
            // on ne remplit pas le SecurityContext
        } catch (Exception e) {
            // autre erreur → 401
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Retrouve l’utilisateur par "subject" (email ou phone).
     */
    private Optional<User> findUserBySubject(String subject) {
        // Si tu es sûr que le subject = phone, garde seulement findByPhone.
        return userRepository.findByEmail(subject)
                .or(() -> userRepository.findByPhone(subject));
    }
}
