package ua.edu.ukma.unischeduler.feature.scheduleimport;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/admin/schedule-imports")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "unischeduler.import.enabled", havingValue = "true")
public class ScheduleImportController {
    private final ScheduleImportService service;

    @Value("${UNISCHEDULER_IMPORT_TOKEN:}")
    private String expectedToken;

    @PostMapping("/replace")
    public ScheduleImportResult replace(@RequestHeader(value = "X-Import-Token", required = false) String token,
                                        @Valid @RequestBody ScheduleImportRequest request) {
        if (expectedToken == null || expectedToken.isBlank() || token == null ||
                !MessageDigest.isEqual(expectedToken.getBytes(StandardCharsets.UTF_8),
                                       token.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Import authorization failed");
        }
        return service.replace(request);
    }
}
