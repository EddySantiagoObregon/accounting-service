package com.microservices.accountingservice.application.service;

import com.microservices.accountingservice.domain.dto.ReporteEstadoCuentaDto;
import com.microservices.accountingservice.domain.entity.Cuenta;
import com.microservices.accountingservice.domain.entity.Movimiento;
import com.microservices.accountingservice.domain.repository.CuentaRepository;
import com.microservices.accountingservice.domain.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReporteService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    public ReporteEstadoCuentaDto generarReporteEstadoCuenta(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Generando reporte de estado de cuenta para cliente: {} entre {} y {}", 
                clienteId, fechaInicio, fechaFin);

        // Obtener cuentas del cliente
        List<Cuenta> cuentas = cuentaRepository.findByClienteIdAndEstadoTrue(clienteId);
        
        if (cuentas.isEmpty()) {
            log.warn("No se encontraron cuentas activas para el cliente: {}", clienteId);
            return new ReporteEstadoCuentaDto();
        }

        // Obtener movimientos del cliente en el rango de fechas
        List<Movimiento> movimientos = movimientoRepository.findByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);

        // Construir el reporte
        ReporteEstadoCuentaDto reporte = new ReporteEstadoCuentaDto();
        reporte.setClienteId(clienteId);
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);

        // Mapear cuentas con sus movimientos
        List<ReporteEstadoCuentaDto.CuentaReporteDto> cuentasReporte = cuentas.stream()
            .map(cuenta -> {
                ReporteEstadoCuentaDto.CuentaReporteDto cuentaReporte = new ReporteEstadoCuentaDto.CuentaReporteDto();
                cuentaReporte.setCuentaId(cuenta.getId());
                cuentaReporte.setNumeroCuenta(cuenta.getNumeroCuenta());
                cuentaReporte.setTipoCuenta(cuenta.getTipoCuenta());
                cuentaReporte.setSaldoInicial(cuenta.getSaldoInicial());
                cuentaReporte.setSaldoActual(cuenta.getSaldoActual());
                cuentaReporte.setEstado(cuenta.getEstado());

                // Filtrar movimientos de esta cuenta en el rango de fechas
                List<ReporteEstadoCuentaDto.MovimientoReporteDto> movimientosCuenta = movimientos.stream()
                    .filter(mov -> mov.getCuenta().getId().equals(cuenta.getId()))
                    .map(mov -> {
                        ReporteEstadoCuentaDto.MovimientoReporteDto movReporte = new ReporteEstadoCuentaDto.MovimientoReporteDto();
                        movReporte.setMovimientoId(mov.getId());
                        movReporte.setFecha(mov.getFecha());
                        movReporte.setTipoMovimiento(mov.getTipoMovimiento());
                        movReporte.setValor(mov.getValor());
                        movReporte.setSaldo(mov.getSaldo());
                        return movReporte;
                    })
                    .collect(Collectors.toList());

                cuentaReporte.setMovimientos(movimientosCuenta);
                return cuentaReporte;
            })
            .collect(Collectors.toList());

        reporte.setCuentas(cuentasReporte);

        // Calcular saldo total
        BigDecimal saldoTotal = cuentas.stream()
            .map(Cuenta::getSaldoActual)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        reporte.setSaldoTotal(saldoTotal);

        log.info("Reporte generado exitosamente para cliente: {} con {} cuentas y {} movimientos", 
                clienteId, cuentas.size(), movimientos.size());

        return reporte;
    }
}

