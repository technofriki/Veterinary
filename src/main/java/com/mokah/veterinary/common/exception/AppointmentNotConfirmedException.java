package com.mokah.veterinary.common.exception;

public class AppointmentNotConfirmedException extends RuntimeException {
    public AppointmentNotConfirmedException(String message) {
        super(message);
    }
}
