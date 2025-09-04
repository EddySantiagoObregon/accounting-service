package com.microservices.accountingservice.domain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDto {

    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime fecha;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "^(Deposito|Retiro)$", message = "El tipo de movimiento debe ser Deposito o Retiro")
    private String tipoMovimiento;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0")
    private BigDecimal valor;

    @NotNull(message = "El saldo es obligatorio")
    private BigDecimal saldo;

    @NotNull(message = "El ID de la cuenta es obligatorio")
    private Long cuentaId;

    private String numeroCuenta;
    private String tipoCuenta;
    private Long clienteId;
    private String nombreCliente;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}

