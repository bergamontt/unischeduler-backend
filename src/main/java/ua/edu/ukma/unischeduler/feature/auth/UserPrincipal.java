package ua.edu.ukma.unischeduler.feature.auth;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import ua.edu.ukma.unischeduler.common.enums.UserRole;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@NullMarked
public class UserPrincipal implements OidcUser, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String email;
    private final UserRole role;
    private final OidcUser delegate;
    private final List<GrantedAuthority> authorities;

    public UserPrincipal(Long id, String email, UserRole role, OidcUser delegate) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.delegate = delegate;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public Map<String, Object> getClaims() {
        return delegate.getClaims();
    }

    @Override
    public @Nullable OidcUserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return delegate.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return email;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public void verifyOwnership(Long ownerId) {
        if (!isAdmin() && !id.equals(ownerId)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
