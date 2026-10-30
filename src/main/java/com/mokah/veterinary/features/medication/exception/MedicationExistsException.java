package com.mokah.veterinary.features.medication.exception;

public class MedicationExistsException extends RuntimeException {
    public MedicationExistsException(String message) {
        super(message);
    }
}
