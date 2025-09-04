package com.microservices.accountingservice.infrastructure.controller;

import com.microservices.accountingservice.application.service.CuentaService;
import com.microservices.accountingservice.domain.dto.CuentaDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaDto> crearCuenta(@Valid @RequestBody CuentaDto cuentaDto) {
        log.info("POST /api/cuentas - Creando cuenta: {}", cuentaDto.getNumeroCuenta());
        CuentaDto cuentaCreada = cuentaService.crearCuenta(cuentaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaCreada);
    }

    @GetMapping
    public ResponseEntity<List<CuentaDto>> obtenerCuentas(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Boolean activas,
            @RequestParam(required = false) String numeroCuenta) {
        log.info("GET /api/cuentas - Obteniendo cuentas con filtros");
        List<CuentaDto> cuentas;
        
        if (clienteId != null) {
            cuentas = cuentaService.obtenerCuentasPorCliente(clienteId);
        } else if (Boolean.TRUE.equals(activas)) {
            cuentas = cuentaService.obtenerCuentasActivas();
        } else {
            cuentas = cuentaService.obtenerTodasLasCuentas();
        }
        
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaDto> obtenerCuentaPorId(@PathVariable Long id) {
        log.info("GET /api/cuentas/{} - Obteniendo cuenta por ID", id);
        CuentaDto cuenta = cuentaService.obtenerCuentaPorId(id);
        return ResponseEntity.ok(cuenta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaDto> actualizarCuenta(@PathVariable Long id, @Valid @RequestBody CuentaDto cuentaDto) {
        log.info("PUT /api/cuentas/{} - Actualizando cuenta", id);
        CuentaDto cuentaActualizada = cuentaService.actualizarCuenta(id, cuentaDto);
        return ResponseEntity.ok(cuentaActualizada);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<CuentaDto> desactivarCuenta(@PathVariable Long id) {
        log.info("PATCH /api/cuentas/{}/desactivar - Desactivando cuenta", id);
        CuentaDto cuentaDesactivada = cuentaService.desactivarCuenta(id);
        return ResponseEntity.ok(cuentaDesactivada);
    }

}

