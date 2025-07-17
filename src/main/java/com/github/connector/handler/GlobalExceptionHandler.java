package com.github.connector.handler;

import com.github.connector.exception.GitHubApiException;
import com.github.connector.exception.MandatoryFieldMissingException;
import com.github.connector.exception.RateLimitExceededException;
import com.github.connector.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static final String ERROR = "error";

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> onNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR, ex.getMessage()));
    }

    @ExceptionHandler(MandatoryFieldMissingException.class)
    public ResponseEntity<Map<String, String>> onMandatoryField(MandatoryFieldMissingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERROR, ex.getMessage()));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> onRateLimit(RateLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of(ERROR, ex.getMessage(), "retry_after_seconds", ex.getRetryAfterMinutes()));
    }

    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<Map<String, String>> onApiError(GitHubApiException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(ERROR, ex.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> onMissingFields(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERROR, "Missing mandatory field - " + ex.getParameterName()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> onOther(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(ERROR, "An unexpected error occurred: "));
    }
}
