package com.secureusermanagement.exception;

public class MobileNumberAlreadyExistsException extends RuntimeException {

    public MobileNumberAlreadyExistsException(String message) {
        super(message);
    }
}