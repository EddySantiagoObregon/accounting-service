package com.microservices.accountingservice.domain.exception;

public class CuentaAlreadyExistsException extends RuntimeException {
    
    public CuentaAlreadyExistsException(String message) {
        super(message);
    }
    
    public CuentaAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

