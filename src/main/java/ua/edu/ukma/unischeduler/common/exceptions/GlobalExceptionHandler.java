package ua.edu.ukma.unischeduler.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ProblemDetail handleBadRequestException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Bad Request");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");
        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
