package ua.edu.ukma.unischeduler.feature.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.enums.UserRole;
import ua.edu.ukma.unischeduler.feature.user.User;
import ua.edu.ukma.unischeduler.feature.user.UserRepository;

import java.util.List;
import java.util.Locale;

/**
 * Automatically registers or promotes configured admin emails to ADMIN role on application startup.
 */
@Slf4j
@Component
@Order(10)
@NullMarked
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
    private final UserRepository userRepository;

    @Value("${app.auth.admin-emails:ir.hryshchenko@ukma.edu.ua,admin@ukma.edu.ua}")
    private List<String> adminEmails = List.of();

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminEmails.isEmpty()) {
            return;
        }

        for (String rawEmail : adminEmails) {
            String email = rawEmail.trim().toLowerCase(Locale.ROOT);
            if (email.isBlank()) continue;

            userRepository.findByEmail(email).ifPresentOrElse(
                    user -> {
                        if (user.getRole() != UserRole.ADMIN) {
                            user.setRole(UserRole.ADMIN);
                            userRepository.save(user);
                            log.info("Promoted user {} to ADMIN role on startup", email);
                        }
                    },
                    () -> {
                        userRepository.save(User.builder()
                                .email(email)
                                .role(UserRole.ADMIN)
                                .build());
                        log.info("Created admin user {} on startup", email);
                    }
            );
        }
    }
}
