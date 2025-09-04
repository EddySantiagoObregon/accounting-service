package com.microservices.accountingservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaEvent {
    private String eventType; // CREATED, UPDATED, DELETED, DEACTIVATED
    private Long cuentaId;
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldoInicial;
    private BigDecimal saldoActual;
    private Long clienteId;
    private Boolean estado;
    private LocalDateTime timestamp;
}
