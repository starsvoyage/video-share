package edu.arizona.videoshare.exception.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ApiError
 *
 * Standardized error response object returned by the REST API.
 * Provides consistent error structure across the application
 */
public class ApiError {
    /**
     * Time at which the error occurred.
     */
    public LocalDateTime timestamp = LocalDateTime.now();

    /**
     * HTTP status code.
     */
    public int status;

    /**
     * Short error description.
     */
    public String error;

    /**
     * Detailed message explaining the issue.
     */
    public String message;

    /**
     * Optional validation field errors.
     * Used when request body validation fails.
     */
    public List<FieldViolation> violations;

    public ApiError(int status, String error, String message, List<FieldViolation> violations) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.violations = violations;
    }

    /**
     * Represents a single field-level validation error.
     *
     * Example:
     * { "field": "email", "message": "must not be blank" }
     */
    public record FieldViolation(String field, String message) {}
}