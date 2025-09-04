package com.microservices.accountingservice.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.accountingservice.domain.dto.MovimientoDto;
import com.microservices.accountingservice.domain.entity.Cuenta;
import com.microservices.accountingservice.domain.entity.Movimiento;
import com.microservices.accountingservice.domain.repository.CuentaRepository;
import com.microservices.accountingservice.domain.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMvc
@ActiveProfiles("test")
@Transactional
class MovimientoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Cuenta cuentaTest;

    @BeforeEach
    void setUp() {
        // Limpiar datos de prueba
        movimientoRepository.deleteAll();
        cuentaRepository.deleteAll();

        // Crear cuenta de prueba
        cuentaTest = new Cuenta();
        cuentaTest.setNumeroCuenta("123456789");
        cuentaTest.setTipoCuenta("Ahorros");
        cuentaTest.setSaldoInicial(BigDecimal.valueOf(1000.00));
        cuentaTest.setSaldoActual(BigDecimal.valueOf(1000.00));
        cuentaTest.setClienteId(1L);
        cuentaTest.setEstado(true);
        cuentaTest = cuentaRepository.save(cuentaTest);
    }

    @Test
    void deberiaCrearMovimientoDepositoExitosamente() throws Exception {
        MovimientoDto movimientoDto = MovimientoDto.builder()
                .cuentaId(cuentaTest.getId())
                .clienteId(1L)
                .tipoMovimiento("Deposito")
                .valor(BigDecimal.valueOf(500.00))
                .build();

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMovimiento").value("Deposito"))
                .andExpect(jsonPath("$.valor").value(500.00))
                .andExpect(jsonPath("$.saldo").value(1500.00));

        // Verificar que el saldo de la cuenta se actualizó
        Cuenta cuentaActualizada = cuentaRepository.findById(cuentaTest.getId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(1500.00), cuentaActualizada.getSaldoActual());
    }

    @Test
    void deberiaCrearMovimientoRetiroExitosamente() throws Exception {
        MovimientoDto movimientoDto = MovimientoDto.builder()
                .cuentaId(cuentaTest.getId())
                .clienteId(1L)
                .tipoMovimiento("Retiro")
                .valor(BigDecimal.valueOf(300.00))
                .build();

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMovimiento").value("Retiro"))
                .andExpect(jsonPath("$.valor").value(300.00))
                .andExpect(jsonPath("$.saldo").value(700.00));

        // Verificar que el saldo de la cuenta se actualizó
        Cuenta cuentaActualizada = cuentaRepository.findById(cuentaTest.getId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(700.00), cuentaActualizada.getSaldoActual());
    }

    @Test
    void deberiaFallarRetiroConSaldoInsuficiente() throws Exception {
        MovimientoDto movimientoDto = MovimientoDto.builder()
                .cuentaId(cuentaTest.getId())
                .clienteId(1L)
                .tipoMovimiento("Retiro")
                .valor(BigDecimal.valueOf(1500.00)) // Más que el saldo disponible
                .build();

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));

        // Verificar que el saldo de la cuenta no cambió
        Cuenta cuentaActualizada = cuentaRepository.findById(cuentaTest.getId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(1000.00), cuentaActualizada.getSaldoActual());
    }

    @Test
    void deberiaObtenerMovimientosPorCuenta() throws Exception {
        // Crear algunos movimientos de prueba
        Movimiento movimiento1 = new Movimiento();
        movimiento1.setCuenta(cuentaTest);
        movimiento1.setClienteId(1L);
        movimiento1.setTipoMovimiento("Deposito");
        movimiento1.setValor(BigDecimal.valueOf(200.00));
        movimiento1.setSaldo(BigDecimal.valueOf(1200.00));
        movimiento1.setFecha(LocalDateTime.now());
        movimientoRepository.save(movimiento1);

        Movimiento movimiento2 = new Movimiento();
        movimiento2.setCuenta(cuentaTest);
        movimiento2.setClienteId(1L);
        movimiento2.setTipoMovimiento("Retiro");
        movimiento2.setValor(BigDecimal.valueOf(100.00));
        movimiento2.setSaldo(BigDecimal.valueOf(1100.00));
        movimiento2.setFecha(LocalDateTime.now());
        movimientoRepository.save(movimiento2);

        mockMvc.perform(get("/api/movimientos/cuenta/{cuentaId}", cuentaTest.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tipoMovimiento").exists())
                .andExpect(jsonPath("$[0].valor").exists())
                .andExpect(jsonPath("$[0].saldo").exists());
    }

    @Test
    void deberiaObtenerMovimientoPorId() throws Exception {
        Movimiento movimiento = new Movimiento();
        movimiento.setCuenta(cuentaTest);
        movimiento.setClienteId(1L);
        movimiento.setTipoMovimiento("Deposito");
        movimiento.setValor(BigDecimal.valueOf(500.00));
        movimiento.setSaldo(BigDecimal.valueOf(1500.00));
        movimiento.setFecha(LocalDateTime.now());
        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);

        mockMvc.perform(get("/api/movimientos/{id}", movimientoGuardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movimientoGuardado.getId()))
                .andExpect(jsonPath("$.tipoMovimiento").value("Deposito"))
                .andExpect(jsonPath("$.valor").value(500.00))
                .andExpect(jsonPath("$.saldo").value(1500.00));
    }

    @Test
    void deberiaFallarObtenerMovimientoInexistente() throws Exception {
        mockMvc.perform(get("/api/movimientos/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Movimiento no encontrado con ID: 999"));
    }

    @Test
    void deberiaFallarCrearMovimientoConCuentaInexistente() throws Exception {
        MovimientoDto movimientoDto = MovimientoDto.builder()
                .cuentaId(999L) // Cuenta que no existe
                .clienteId(1L)
                .tipoMovimiento("Deposito")
                .valor(BigDecimal.valueOf(100.00))
                .build();

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cuenta no encontrada con ID: 999"));
    }
}