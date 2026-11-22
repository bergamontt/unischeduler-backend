package ua.edu.ukma.unischeduler.feature.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ua.edu.ukma.unischeduler.common.enums.UserRole;
import ua.edu.ukma.unischeduler.feature.user.User;
import ua.edu.ukma.unischeduler.feature.user.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {
    private final UserRepository userRepository;

    private static final String DEFAULT_ADMIN_EMAIL = "ir.hryshchenko@ukma.edu.ua";

    @Override
    public void run(String... args) {
        userRepository.findByEmail(DEFAULT_ADMIN_EMAIL).ifPresentOrElse(
                user -> {
                    if (user.getRole() != UserRole.ADMIN) {
                        user.setRole(UserRole.ADMIN);
                        userRepository.save(user);
                        log.info("Elevated user {} to ADMIN role", DEFAULT_ADMIN_EMAIL);
                    }
                },
                () -> {
                    User admin = User.builder()
                            .email(DEFAULT_ADMIN_EMAIL)
                            .role(UserRole.ADMIN)
                            .build();
                    userRepository.save(admin);
                    log.info("Initialized default admin user: {}", DEFAULT_ADMIN_EMAIL);
                }
        );
    }
}
