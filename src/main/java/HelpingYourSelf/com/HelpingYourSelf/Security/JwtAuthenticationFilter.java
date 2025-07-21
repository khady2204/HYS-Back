package HelpingYourSelf.com.HelpingYourSelf.Security;

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

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            System.out.println("[JwtAuthenticationFilter] Authorization header: " + authHeader);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                System.out.println("[JwtAuthenticationFilter] Token extracted: " + token);

                if (jwtTokenProvider.validateToken(token)) {
                    System.out.println("[JwtAuthenticationFilter] Token valid");
                    String subject = jwtTokenProvider.getSubjectFromToken(token);
                    System.out.println("[JwtAuthenticationFilter] Subject: " + subject);

                    // ✅ Charger UserDetails via téléphone
                    var userDetails = customUserDetailsService.loadUserByUsername(subject);
                    System.out.println("[JwtAuthenticationFilter] UserDetails loaded: " + userDetails.getUsername());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("[JwtAuthenticationFilter] Authentication set in context");
                } else {
                    System.out.println("[JwtAuthenticationFilter] Token invalid");
                }
            } else {
                System.out.println("[JwtAuthenticationFilter] No Bearer token found");
            }
        } catch (Exception e) {
            System.out.println("[JwtAuthenticationFilter] Exception: " + e.getMessage());
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
