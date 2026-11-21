package ua.edu.ukma.unischeduler.feature.auth;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@NullMarked
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOidcUserService customOidcUserService;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${app.auth.failure-redirect-url}")
    private String failureRedirectUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if (allowedOrigins.isEmpty()) {
            throw new IllegalStateException("app.cors.allowed-origins must not be empty");
        }
        String frontendUrl = allowedOrigins.getFirst();

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**", "/login/**", "/error").permitAll()
                        .requestMatchers("/user", "/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/course/**", "/class/**", "/speciality/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/course/**", "/class/**", "/speciality/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/course/**", "/class/**", "/speciality/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/course", "/course/**",
                                "/class", "/class/**",
                                "/speciality", "/speciality/**",
                                "/calendar"
                        ).hasAnyRole("STUDENT", "ADMIN")
                        .anyRequest().hasAnyRole("STUDENT", "ADMIN")
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService))
                        .successHandler((_, response, _) -> response.sendRedirect(frontendUrl))
                        .failureHandler((_, response, _) -> response.sendRedirect(failureRedirectUrl))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("SESSION", "JSESSIONID")
                        .logoutSuccessHandler((_, response, _) -> response.setStatus(HttpStatus.OK.value()))
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((_, response, _) -> response.setStatus(HttpStatus.FORBIDDEN.value()))
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
