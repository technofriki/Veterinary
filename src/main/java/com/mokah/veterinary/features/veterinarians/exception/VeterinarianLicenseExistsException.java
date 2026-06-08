package com.mokah.veterinary.features.veterinarians.exception;

public class VeterinarianLicenseExistsException extends RuntimeException {
    public VeterinarianLicenseExistsException(String message) {
        super(message);
    }
}
