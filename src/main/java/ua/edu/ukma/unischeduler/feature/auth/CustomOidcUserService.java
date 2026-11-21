package ua.edu.ukma.unischeduler.feature.auth;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.enums.UserRole;
import ua.edu.ukma.unischeduler.feature.user.User;
import ua.edu.ukma.unischeduler.feature.user.UserRepository;

import java.util.Locale;

@Service
@NullMarked
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {
    private final UserRepository userRepository;

    @Value("${app.auth.allowed-email-domain:ukma.edu.ua}")
    private @Nullable String allowedEmailDomain;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = extractEmail(oidcUser);
        validateDomain(email);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .role(UserRole.STUDENT)
                        .build()));

        return new UserPrincipal(user.getId(), user.getEmail(), user.getRole(), oidcUser);
    }

    private String extractEmail(OidcUser oidcUser) {
        String rawEmail = oidcUser.getEmail();
        if (rawEmail == null || rawEmail.isBlank()) {
            rawEmail = oidcUser.getPreferredUsername();
        }
        if (rawEmail == null || rawEmail.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("missing_email", "Email claim not found in Microsoft OIDC token", null));
        }
        return rawEmail.trim().toLowerCase(Locale.ROOT);
    }

    private void validateDomain(String email) {
        if (allowedEmailDomain == null || allowedEmailDomain.isBlank()) {
            return;
        }
        String suffix = "@" + allowedEmailDomain.trim().toLowerCase(Locale.ROOT);
        if (!email.endsWith(suffix)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_domain", "Only corporate emails (" + suffix + ") are allowed", null));
        }
    }
}
