package com.mokah.veterinary.common.exception;

public class OwnerByPetExistsException extends RuntimeException {
    public OwnerByPetExistsException(String message) {
        super(message);
    }
}
