package com.microservices.accountingservice.infrastructure.controller;

import com.microservices.accountingservice.application.service.ReporteService;
import com.microservices.accountingservice.domain.dto.ReporteEstadoCuentaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<ReporteEstadoCuentaDto> generarReporteEstadoCuenta(
            @RequestParam Long cliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        log.info("GET /api/reportes/estado-cuenta - Generando reporte para cliente: {} entre {} y {}", 
                cliente, fechaInicio, fechaFin);
        
        ReporteEstadoCuentaDto reporte = reporteService.generarReporteEstadoCuenta(cliente, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}

