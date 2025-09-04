package com.microservices.accountingservice.application.service;

import com.microservices.accountingservice.domain.dto.MovimientoDto;
import com.microservices.accountingservice.domain.entity.Cuenta;
import com.microservices.accountingservice.domain.entity.Movimiento;
import com.microservices.accountingservice.domain.exception.CuentaNotFoundException;
import com.microservices.accountingservice.domain.exception.SaldoNoDisponibleException;
import com.microservices.accountingservice.domain.mapper.MovimientoMapper;
import com.microservices.accountingservice.domain.repository.CuentaRepository;
import com.microservices.accountingservice.domain.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para MovimientoService")
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private MovimientoMapper movimientoMapper;

    @InjectMocks
    private MovimientoService movimientoService;

    private Cuenta cuenta;
    private MovimientoDto movimientoDto;
    private Movimiento movimiento;

    @BeforeEach
    void setUp() {
        // Configurar cuenta de prueba
        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("478758");
        cuenta.setTipoCuenta("Ahorros");
        cuenta.setSaldoInicial(new BigDecimal("2000.00"));
        cuenta.setEstado(true);
        cuenta.setClienteId(1L);

        // Configurar movimiento DTO de prueba
        movimientoDto = new MovimientoDto();
        movimientoDto.setTipoMovimiento("Retiro");
        movimientoDto.setValor(new BigDecimal("575.00"));
        movimientoDto.setCuentaId(1L);

        // Configurar movimiento de prueba
        movimiento = new Movimiento();
        movimiento.setId(1L);
        movimiento.setTipoMovimiento("Retiro");
        movimiento.setValor(new BigDecimal("575.00"));
        movimiento.setSaldo(new BigDecimal("1425.00"));
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setCuenta(cuenta);
    }

    @Test
    @DisplayName("Debería crear un retiro exitosamente cuando hay saldo suficiente")
    void deberiaCrearRetiroExitosamenteCuandoHaySaldoSuficiente() {
        // Given
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(movimientoMapper.toEntity(movimientoDto)).thenReturn(movimiento);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        when(movimientoMapper.toDto(movimiento)).thenReturn(movimientoDto);

        // When
        MovimientoDto resultado = movimientoService.crearMovimiento(movimientoDto);

        // Then
        assertNotNull(resultado);
        verify(cuentaRepository).findById(1L);
        verify(movimientoRepository).save(any(Movimiento.class));
        verify(movimientoMapper).toDto(movimiento);
    }

    @Test
    @DisplayName("Debería crear un depósito exitosamente")
    void deberiaCrearDepositoExitosamente() {
        // Given
        movimientoDto.setTipoMovimiento("Deposito");
        movimientoDto.setValor(new BigDecimal("500.00"));
        movimiento.setTipoMovimiento("Deposito");
        movimiento.setValor(new BigDecimal("500.00"));
        movimiento.setSaldo(new BigDecimal("2500.00"));

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(movimientoMapper.toEntity(movimientoDto)).thenReturn(movimiento);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        when(movimientoMapper.toDto(movimiento)).thenReturn(movimientoDto);

        // When
        MovimientoDto resultado = movimientoService.crearMovimiento(movimientoDto);

        // Then
        assertNotNull(resultado);
        verify(cuentaRepository).findById(1L);
        verify(movimientoRepository).save(any(Movimiento.class));
        verify(movimientoMapper).toDto(movimiento);
    }

    @Test
    @DisplayName("Debería lanzar SaldoNoDisponibleException cuando no hay saldo suficiente para retiro")
    void deberiaLanzarSaldoNoDisponibleExceptionCuandoNoHaySaldoSuficiente() {
        // Given
        movimientoDto.setValor(new BigDecimal("3000.00")); // Mayor al saldo inicial
        cuenta.setSaldoInicial(new BigDecimal("2000.00"));

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        // When & Then
        SaldoNoDisponibleException exception = assertThrows(
            SaldoNoDisponibleException.class,
            () -> movimientoService.crearMovimiento(movimientoDto)
        );

        assertEquals("Saldo no disponible", exception.getMessage());
        verify(cuentaRepository).findById(1L);
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    @DisplayName("Debería lanzar CuentaNotFoundException cuando la cuenta no existe")
    void deberiaLanzarCuentaNotFoundExceptionCuandoLaCuentaNoExiste() {
        // Given
        when(cuentaRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        CuentaNotFoundException exception = assertThrows(
            CuentaNotFoundException.class,
            () -> movimientoService.crearMovimiento(movimientoDto)
        );

        assertEquals("Cuenta no encontrada con ID: 1", exception.getMessage());
        verify(cuentaRepository).findById(1L);
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    @DisplayName("Debería lanzar CuentaNotFoundException cuando la cuenta está inactiva")
    void deberiaLanzarCuentaNotFoundExceptionCuandoLaCuentaEstaInactiva() {
        // Given
        cuenta.setEstado(false);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        // When & Then
        CuentaNotFoundException exception = assertThrows(
            CuentaNotFoundException.class,
            () -> movimientoService.crearMovimiento(movimientoDto)
        );

        assertEquals("La cuenta está inactiva", exception.getMessage());
        verify(cuentaRepository).findById(1L);
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    @DisplayName("Debería permitir retiro cuando el saldo es exactamente igual al valor del retiro")
    void deberiaPermitirRetiroCuandoElSaldoEsExactamenteIgualAlValorDelRetiro() {
        // Given
        movimientoDto.setValor(new BigDecimal("2000.00")); // Igual al saldo inicial
        movimiento.setValor(new BigDecimal("2000.00"));
        movimiento.setSaldo(BigDecimal.ZERO);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(movimientoMapper.toEntity(movimientoDto)).thenReturn(movimiento);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        when(movimientoMapper.toDto(movimiento)).thenReturn(movimientoDto);

        // When
        MovimientoDto resultado = movimientoService.crearMovimiento(movimientoDto);

        // Then
        assertNotNull(resultado);
        verify(cuentaRepository).findById(1L);
        verify(movimientoRepository).save(any(Movimiento.class));
        verify(movimientoMapper).toDto(movimiento);
    }

    @Test
    @DisplayName("Debería calcular el saldo correctamente después de un retiro")
    void deberiaCalcularElSaldoCorrectamenteDespuesDeUnRetiro() {
        // Given
        BigDecimal valorRetiro = new BigDecimal("575.00");
        BigDecimal saldoEsperado = new BigDecimal("1425.00"); // 2000 - 575

        movimientoDto.setValor(valorRetiro);
        movimiento.setValor(valorRetiro);
        movimiento.setSaldo(saldoEsperado);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(movimientoMapper.toEntity(movimientoDto)).thenReturn(movimiento);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        when(movimientoMapper.toDto(movimiento)).thenReturn(movimientoDto);

        // When
        MovimientoDto resultado = movimientoService.crearMovimiento(movimientoDto);

        // Then
        assertNotNull(resultado);
        verify(movimientoRepository).save(argThat(mov -> 
            mov.getSaldo().compareTo(saldoEsperado) == 0
        ));
    }

    @Test
    @DisplayName("Debería calcular el saldo correctamente después de un depósito")
    void deberiaCalcularElSaldoCorrectamenteDespuesDeUnDeposito() {
        // Given
        movimientoDto.setTipoMovimiento("Deposito");
        BigDecimal valorDeposito = new BigDecimal("600.00");
        BigDecimal saldoEsperado = new BigDecimal("2600.00"); // 2000 + 600

        movimientoDto.setValor(valorDeposito);
        movimiento.setTipoMovimiento("Deposito");
        movimiento.setValor(valorDeposito);
        movimiento.setSaldo(saldoEsperado);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(movimientoMapper.toEntity(movimientoDto)).thenReturn(movimiento);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        when(movimientoMapper.toDto(movimiento)).thenReturn(movimientoDto);

        // When
        MovimientoDto resultado = movimientoService.crearMovimiento(movimientoDto);

        // Then
        assertNotNull(resultado);
        verify(movimientoRepository).save(argThat(mov -> 
            mov.getSaldo().compareTo(saldoEsperado) == 0
        ));
    }
}

