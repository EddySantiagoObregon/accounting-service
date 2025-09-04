package com.microservices.accountingservice.application.service;

import com.microservices.accountingservice.domain.dto.MovimientoDto;
import com.microservices.accountingservice.domain.entity.Cuenta;
import com.microservices.accountingservice.domain.entity.Movimiento;
import com.microservices.accountingservice.domain.exception.CuentaNotFoundException;
import com.microservices.accountingservice.domain.exception.MovimientoNotFoundException;
import com.microservices.accountingservice.domain.exception.SaldoNoDisponibleException;
import com.microservices.accountingservice.domain.mapper.MovimientoMapper;
import com.microservices.accountingservice.domain.repository.CuentaRepository;
import com.microservices.accountingservice.domain.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoMapper movimientoMapper;

    public MovimientoDto crearMovimiento(MovimientoDto movimientoDto) {
        log.info("Creando movimiento de tipo: {} por valor: {}", 
                movimientoDto.getTipoMovimiento(), movimientoDto.getValor());
        
        Cuenta cuenta = cuentaRepository.findById(movimientoDto.getCuentaId())
            .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + movimientoDto.getCuentaId()));

        if (!cuenta.getEstado()) {
            throw new CuentaNotFoundException("La cuenta está inactiva");
        }

        BigDecimal saldoActual = cuenta.getSaldoActual();
        BigDecimal valorMovimiento = movimientoDto.getValor();
        BigDecimal nuevoSaldo;

        // Validar saldo disponible para retiros
        if ("Retiro".equals(movimientoDto.getTipoMovimiento())) {
            if (saldoActual.compareTo(valorMovimiento) < 0) {
                throw new SaldoNoDisponibleException("Saldo no disponible");
            }
            nuevoSaldo = saldoActual.subtract(valorMovimiento);
        } else { // Depósito
            nuevoSaldo = saldoActual.add(valorMovimiento);
        }

        Movimiento movimiento = movimientoMapper.toEntity(movimientoDto);
        movimiento.setCuenta(cuenta);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setClienteId(movimientoDto.getClienteId());

        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
        
        // Actualizar saldo de la cuenta
        cuenta.setSaldoActual(nuevoSaldo);
        cuentaRepository.save(cuenta);
        
        log.info("Movimiento creado exitosamente con ID: {}, nuevo saldo: {}", 
                movimientoGuardado.getId(), nuevoSaldo);
        
        return movimientoMapper.toDto(movimientoGuardado);
    }

    @Transactional(readOnly = true)
    public List<MovimientoDto> obtenerTodosLosMovimientos() {
        log.info("Obteniendo todos los movimientos");
        List<Movimiento> movimientos = movimientoRepository.findAll();
        return movimientoMapper.toDtoList(movimientos);
    }

    @Transactional(readOnly = true)
    public List<MovimientoDto> obtenerMovimientosPorCuenta(Long cuentaId) {
        log.info("Obteniendo movimientos de la cuenta: {}", cuentaId);
        List<Movimiento> movimientos = movimientoRepository.findByCuentaIdOrderByFechaDesc(cuentaId);
        return movimientoMapper.toDtoList(movimientos);
    }

    @Transactional(readOnly = true)
    public List<MovimientoDto> obtenerMovimientosPorCliente(Long clienteId) {
        log.info("Obteniendo movimientos del cliente: {}", clienteId);
        List<Movimiento> movimientos = movimientoRepository.findByClienteIdOrderByFechaDesc(clienteId);
        return movimientoMapper.toDtoList(movimientos);
    }

    @Transactional(readOnly = true)
    public List<MovimientoDto> obtenerMovimientosPorClienteYFecha(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo movimientos del cliente: {} entre {} y {}", clienteId, fechaInicio, fechaFin);
        List<Movimiento> movimientos = movimientoRepository.findByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);
        return movimientoMapper.toDtoList(movimientos);
    }

    @Transactional(readOnly = true)
    public MovimientoDto obtenerMovimientoPorId(Long id) {
        log.info("Obteniendo movimiento por ID: {}", id);
        Movimiento movimiento = movimientoRepository.findById(id)
            .orElseThrow(() -> new MovimientoNotFoundException("Movimiento no encontrado con ID: " + id));
        return movimientoMapper.toDto(movimiento);
    }

    public MovimientoDto actualizarMovimiento(Long id, MovimientoDto movimientoDto) {
        log.info("Actualizando movimiento con ID: {}", id);
        
        Movimiento movimientoExistente = movimientoRepository.findById(id)
            .orElseThrow(() -> new MovimientoNotFoundException("Movimiento no encontrado con ID: " + id));

        // Para actualizaciones, solo permitimos cambiar el tipo de movimiento
        // El valor y saldo no se pueden modificar por integridad
        movimientoExistente.setTipoMovimiento(movimientoDto.getTipoMovimiento());
        
        Movimiento movimientoActualizado = movimientoRepository.save(movimientoExistente);
        
        log.info("Movimiento actualizado exitosamente con ID: {}", id);
        return movimientoMapper.toDto(movimientoActualizado);
    }

}

