package com.mokah.veterinary.features.receptionists.exception;

public class ReceptionistEmailExistsException extends RuntimeException {
    public ReceptionistEmailExistsException(String message) {
        super(message);
    }
}
