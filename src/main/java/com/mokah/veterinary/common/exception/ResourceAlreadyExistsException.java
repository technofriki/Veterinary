package com.mokah.veterinary.common.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String resourceName, String field, Object value) {
        super(resourceName + " already exists with " + field + ": " + value);
    }
}
