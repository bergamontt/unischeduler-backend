package ua.edu.ukma.unischeduler.feature.auth;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@NullMarked
public class CurrentUserService {

    public UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AccessDeniedException("User is not authenticated");
        }
        return principal;
    }

    public boolean isAdmin() {
        return getCurrentUser().isAdmin();
    }

    public void verifyOwnership(Long ownerId) {
        getCurrentUser().verifyOwnership(ownerId);
    }

    public Long resolveUserId(Long requestedUserId) {
        UserPrincipal current = getCurrentUser();
        return current.isAdmin() ? requestedUserId : current.getId();
    }
}
