package edu.arizona.videoshare.exception;

import edu.arizona.videoshare.exception.response.ApiError;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import edu.arizona.videoshare.exception.ForbiddenException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * GlobalExceptionHandler
 *
 * Centralized REST exception handling for the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation failures triggered by @Valid on request bodies.
     * Returns HTTP 400 with structured field-level violations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiError.FieldViolation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ApiError.FieldViolation(fe.getField(), prettyMessage(fe)))
                .toList();

        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed",
                violations);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles business-level conflict errors.
     * Returns HTTP 409 Conflict.
     */
    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        ApiError body = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles resource-not-found scenarios.
     * Returns HTTP 404 Not Found.
     */
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        ApiError body = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles database integrity violations.
     * Ensures consistent 409 Conflict responses.
     */
    // If a race condition slips past existsBy* checks, DB uniqueness still enforces
    // it.
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        ApiError body = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                "Unique constraint violation",
                List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles authorization failures.
     * Returns HTTP 403 Forbidden.
     */
    @ExceptionHandler(ForbiddenException.class)
    ResponseEntity<ApiError> handleForbidden(ForbiddenException ex) {
        ApiError body = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                List.of());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Formats validation error messages.
     */
    private String prettyMessage(FieldError fe) {
        return fe.getDefaultMessage() != null
                ? fe.getDefaultMessage()
                : "Invalid value";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Avatar image must be smaller than 5MB.");
        redirectAttributes.addFlashAttribute("openCreateChannelModal", true);
        return "redirect:/you";
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ApiError body = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Something went wrong. Please try again.",
                List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
