package ua.edu.ukma.unischeduler.feature.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.edu.ukma.unischeduler.feature.user.User;
import ua.edu.ukma.unischeduler.feature.user.UserRepository;

import java.io.IOException;
import java.util.Optional;

/**
 * Ensures that if a user's role is updated in the database (e.g. promoted to ADMIN),
 * their active session authorities are automatically synchronized with the database
 * on every incoming request without requiring re-login.
 */
@Slf4j
@Component
@NullMarked
@RequiredArgsConstructor
public class UserRoleSyncFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof OAuth2AuthenticationToken token && token.getPrincipal() instanceof UserPrincipal principal) {
            Optional<User> userOpt = userRepository.findById(principal.getId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.getRole() != principal.getRole()) {
                    log.info("Synchronizing user role from DB for {}: {} -> {}",
                            user.getEmail(), principal.getRole(), user.getRole());

                    UserPrincipal updatedPrincipal = new UserPrincipal(
                            user.getId(),
                            user.getEmail(),
                            user.getRole(),
                            principal.getDelegate()
                    );
                    OAuth2AuthenticationToken updatedToken = new OAuth2AuthenticationToken(
                            updatedPrincipal,
                            updatedPrincipal.getAuthorities(),
                            token.getAuthorizedClientRegistrationId()
                    );
                    updatedToken.setDetails(token.getDetails());

                    SecurityContext context = SecurityContextHolder.getContext();
                    context.setAuthentication(updatedToken);
                    securityContextRepository.saveContext(context, request, response);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
