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
public class MovimientoEvent {
    private String eventType; // CREATED, UPDATED
    private Long movimientoId;
    private Long cuentaId;
    private String numeroCuenta;
    private Long clienteId;
    private String tipoMovimiento;
    private BigDecimal valor;
    private BigDecimal saldo;
    private LocalDateTime fecha;
    private LocalDateTime timestamp;
}
