package edu.arizona.videoshare.exception;

/**
 * NotFoundException
 *
 * Thrown when a requested resource does not exist.
 * Represent domain-level "resource not found" errors
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
