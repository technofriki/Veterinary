package com.mokah.veterinary.features.veterinarians.exception;

public class VeterinarianEmailExistsException extends RuntimeException {
    public VeterinarianEmailExistsException(String message) {
        super(message);
    }
}
