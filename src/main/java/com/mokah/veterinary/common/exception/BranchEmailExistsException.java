package com.mokah.veterinary.common.exception;

public class BranchEmailExistsException extends RuntimeException {
    public BranchEmailExistsException(String message) {
        super(message);
    }
}
