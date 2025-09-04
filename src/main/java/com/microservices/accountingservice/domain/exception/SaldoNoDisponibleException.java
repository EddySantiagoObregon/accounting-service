package com.microservices.accountingservice.domain.exception;

public class SaldoNoDisponibleException extends RuntimeException {
    
    public SaldoNoDisponibleException(String message) {
        super(message);
    }
    
    public SaldoNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}

