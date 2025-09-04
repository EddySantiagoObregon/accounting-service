package com.microservices.accountingservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteEstadoCuentaDto {

    private Long clienteId;
    private String nombreCliente;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<CuentaReporteDto> cuentas;
    private BigDecimal saldoTotal;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuentaReporteDto {
        private Long cuentaId;
        private String numeroCuenta;
        private String tipoCuenta;
        private BigDecimal saldoInicial;
        private BigDecimal saldoActual;
        private Boolean estado;
        private List<MovimientoReporteDto> movimientos;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovimientoReporteDto {
        private Long movimientoId;
        private LocalDateTime fecha;
        private String tipoMovimiento;
        private BigDecimal valor;
        private BigDecimal saldo;
    }
}

