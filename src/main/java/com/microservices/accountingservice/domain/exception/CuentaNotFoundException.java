package com.microservices.accountingservice.domain.exception;

public class CuentaNotFoundException extends RuntimeException {
    
    public CuentaNotFoundException(String message) {
        super(message);
    }
    
    public CuentaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

