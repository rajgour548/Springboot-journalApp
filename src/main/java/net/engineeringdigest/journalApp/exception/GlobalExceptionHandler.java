package net.engineeringdigest.journalApp.exception;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.dto.ErrorDto;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorDto response = new ErrorDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                Collections.emptyList()
        );
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorDto> handleInvalidRequest(
            InvalidRequestException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorDto response = new ErrorDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                Collections.emptyList()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorDto> handleDuplicateKey(
            DuplicateKeyException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;

        ErrorDto response = new ErrorDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "An account with these details already exists.",
                request.getRequestURI(),
                Collections.emptyList()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorDto response = new ErrorDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Incorrect username or password",
                request.getRequestURI(),
                Collections.emptyList()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorDto> handleExternalServiceError(
            ResourceAccessException ex,
            HttpServletRequest request) {

        log.error("External service connection failed at path: {}",
                request.getRequestURI(), ex);

        HttpStatus status = HttpStatus.BAD_GATEWAY;

        ErrorDto response = new ErrorDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Authentication provider is temporarily unavailable. Please try again later.",
                request.getRequestURI(),
                Collections.emptyList()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(GoogleAuthenticationException.class)
    public ResponseEntity<ErrorDto> handleGoogleAuthenticationException(
            GoogleAuthenticationException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorDto response = new ErrorDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                Collections.emptyList()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error occurred at path: {}",
                request.getRequestURI(), ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorDto response = new ErrorDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Something went wrong on our side. Please try again later.",
                request.getRequestURI(),
                Collections.emptyList()
        );

        return ResponseEntity.status(status).body(response);
    }
}

