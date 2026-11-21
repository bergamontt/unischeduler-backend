package ua.edu.ukma.unischeduler.feature.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.enums.UserRole;
import ua.edu.ukma.unischeduler.feature.personalevent.PersonalEvent;
import ua.edu.ukma.unischeduler.feature.personalevent.PersonalEventRepository;
import ua.edu.ukma.unischeduler.feature.user.User;
import ua.edu.ukma.unischeduler.feature.user.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonalEventRepository personalEventRepository;

    private User student;
    private User otherStudent;
    private User admin;
    private PersonalEvent otherStudentEvent;

    @BeforeEach
    void setUp() {
        student = userRepository.save(User.builder()
                .email("student@ukma.edu.ua")
                .role(UserRole.STUDENT)
                .build());

        otherStudent = userRepository.save(User.builder()
                .email("other@ukma.edu.ua")
                .role(UserRole.STUDENT)
                .build());

        admin = userRepository.save(User.builder()
                .email("admin@ukma.edu.ua")
                .role(UserRole.ADMIN)
                .build());

        otherStudentEvent = personalEventRepository.save(PersonalEvent.builder()
                .userId(otherStudent.getId())
                .name("Other student event")
                .startsAt(LocalDateTime.of(2026, 11, 21, 10, 0))
                .endsAt(LocalDateTime.of(2026, 11, 21, 11, 0))
                .build());
    }

    @Test
    void unauthenticatedRequestsReturn401() throws Exception {
        mockMvc.perform(get("/course")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/calendar")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/personal-event")).andExpect(status().isUnauthorized());
    }

    @Test
    void studentCanReadScheduleAndMeButCannotAccessAdminEndpoints() throws Exception {
        UserPrincipal principal = principalFor(student);

        mockMvc.perform(get("/course").with(oidcLogin().oidcUser(principal)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auth/me").with(oidcLogin().oidcUser(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("student@ukma.edu.ua"))
                .andExpect(jsonPath("$.role").value("STUDENT"));

        mockMvc.perform(get("/user").with(oidcLogin().oidcUser(principal)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/speciality")
                        .with(oidcLogin().oidcUser(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"CS\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentCannotAccessOtherUsersPersonalData() throws Exception {
        UserPrincipal principal = principalFor(student);

        mockMvc.perform(get("/calendar")
                        .param("userId", otherStudent.getId().toString())
                        .with(oidcLogin().oidcUser(principal)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/calendar")
                        .param("userId", student.getId().toString())
                        .with(oidcLogin().oidcUser(principal)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/personal-event/{id}", otherStudentEvent.getId())
                        .with(oidcLogin().oidcUser(principal)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/personal-event/{id}", otherStudentEvent.getId())
                        .with(oidcLogin().oidcUser(principal)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAdminEndpoints() throws Exception {
        UserPrincipal principal = principalFor(admin);

        mockMvc.perform(get("/user").with(oidcLogin().oidcUser(principal)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/speciality")
                        .with(oidcLogin().oidcUser(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Software Engineering\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void logoutReturns200() throws Exception {
        mockMvc.perform(post("/logout").with(oidcLogin().oidcUser(principalFor(student))))
                .andExpect(status().isOk());
    }

    private UserPrincipal principalFor(User user) {
        OidcIdToken idToken = new OidcIdToken(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("sub", user.getId().toString(), "email", user.getEmail())
        );
        OidcUser delegate = new DefaultOidcUser(List.of(), idToken, "email");
        return new UserPrincipal(user.getId(), user.getEmail(), user.getRole(), delegate);
    }
}
