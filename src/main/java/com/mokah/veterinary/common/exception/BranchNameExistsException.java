package com.mokah.veterinary.common.exception;

public class BranchNameExistsException extends RuntimeException {
    public BranchNameExistsException(String message) {
        super(message);
    }
}
