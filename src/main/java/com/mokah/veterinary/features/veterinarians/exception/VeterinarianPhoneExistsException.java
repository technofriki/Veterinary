package com.mokah.veterinary.features.veterinarians.exception;

public class VeterinarianPhoneExistsException extends RuntimeException {
    public VeterinarianPhoneExistsException(String message) {
        super(message);
    }
}
