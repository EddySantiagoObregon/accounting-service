package com.microservices.accountingservice.infrastructure.controller;

import com.microservices.accountingservice.application.service.MovimientoService;
import com.microservices.accountingservice.domain.dto.MovimientoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<MovimientoDto> crearMovimiento(@Valid @RequestBody MovimientoDto movimientoDto) {
        log.info("POST /api/movimientos - Creando movimiento de tipo: {} por valor: {}", 
                movimientoDto.getTipoMovimiento(), movimientoDto.getValor());
        MovimientoDto movimientoCreado = movimientoService.crearMovimiento(movimientoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoCreado);
    }

    @GetMapping
    public ResponseEntity<List<MovimientoDto>> obtenerTodosLosMovimientos() {
        log.info("GET /api/movimientos - Obteniendo todos los movimientos");
        List<MovimientoDto> movimientos = movimientoService.obtenerTodosLosMovimientos();
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<MovimientoDto>> obtenerMovimientosPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(required = false) Long cuentaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        log.info("GET /api/movimientos/cliente/{} - Obteniendo movimientos del cliente", clienteId);
        List<MovimientoDto> movimientos;
        
        if (cuentaId != null) {
            movimientos = movimientoService.obtenerMovimientosPorCuenta(cuentaId);
        } else if (fechaInicio != null && fechaFin != null) {
            movimientos = movimientoService.obtenerMovimientosPorClienteYFecha(clienteId, fechaInicio, fechaFin);
        } else {
            movimientos = movimientoService.obtenerMovimientosPorCliente(clienteId);
        }
        
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDto> obtenerMovimientoPorId(@PathVariable Long id) {
        log.info("GET /api/movimientos/{} - Obteniendo movimiento por ID", id);
        MovimientoDto movimiento = movimientoService.obtenerMovimientoPorId(id);
        return ResponseEntity.ok(movimiento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDto> actualizarMovimiento(@PathVariable Long id, @Valid @RequestBody MovimientoDto movimientoDto) {
        log.info("PUT /api/movimientos/{} - Actualizando movimiento", id);
        MovimientoDto movimientoActualizado = movimientoService.actualizarMovimiento(id, movimientoDto);
        return ResponseEntity.ok(movimientoActualizado);
    }

}

