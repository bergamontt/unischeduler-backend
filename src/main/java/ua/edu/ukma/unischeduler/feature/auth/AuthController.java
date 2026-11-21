package ua.edu.ukma.unischeduler.feature.auth;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.unischeduler.feature.user.UserResponseDto;
import ua.edu.ukma.unischeduler.feature.user.UserService;

@RestController
@RequestMapping("/auth")
@NullMarked
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponseDto getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.findById(principal.getId());
    }
}
