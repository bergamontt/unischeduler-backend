package ua.edu.ukma.unischeduler.common.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void resourceNotFoundReturns404WithMessage() {
        ProblemDetail result = handler.handleResourceNotFoundException(new ResourceNotFoundException("Course not found"));

        assertEquals(404, result.getStatus());
        assertEquals("Resource Not Found", result.getTitle());
        assertEquals("Course not found", result.getDetail());
        assertInstanceOf(Instant.class, result.getProperties().get("timestamp"));
    }

    @Test
    void badRequestReturns400WithMessage() {
        ProblemDetail result = handler.handleBadRequestException(new IllegalArgumentException("Invalid event date"));

        assertEquals(400, result.getStatus());
        assertEquals("Bad Request", result.getTitle());
        assertEquals("Invalid event date", result.getDetail());
        assertInstanceOf(Instant.class, result.getProperties().get("timestamp"));
    }

    @Test
    void unexpectedExceptionReturns500WithoutInternalDetails() {
        ProblemDetail result = handler.handleUnexpectedException(new RuntimeException("Database password is invalid"));

        assertEquals(500, result.getStatus());
        assertEquals("Internal Server Error", result.getTitle());
        assertEquals("An unexpected error occurred", result.getDetail());
        assertInstanceOf(Instant.class, result.getProperties().get("timestamp"));
    }

    @Test
    void dataIntegrityViolationReturns409() {
        ProblemDetail result = handler.handleDataIntegrityViolation(new DataIntegrityViolationException("fk violation"));

        assertEquals(409, result.getStatus());
        assertEquals("Conflict", result.getTitle());
        assertInstanceOf(Instant.class, result.getProperties().get("timestamp"));
    }

    @Test
    void accessDeniedReturns403() {
        ProblemDetail result = handler.handleAccessDeniedException(new AccessDeniedException("Forbidden"));

        assertEquals(403, result.getStatus());
        assertEquals("Forbidden", result.getTitle());
        assertEquals("Access denied", result.getDetail());
    }

    @Test
    void methodNotSupportedReturns405() {
        ProblemDetail result = handler.handleMethodNotSupported(new HttpRequestMethodNotSupportedException("PATCH"));

        assertEquals(405, result.getStatus());
        assertEquals("Method Not Allowed", result.getTitle());
    }
}
