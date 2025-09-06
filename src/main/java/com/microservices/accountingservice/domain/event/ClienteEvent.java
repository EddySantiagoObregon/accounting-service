package com.microservices.accountingservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEvent {
    private String eventType; // CREATED, UPDATED, DELETED, DEACTIVATED
    private Long clienteId;
    private String identificacion;
    private String nombre;
    private String direccion;
    private String telefono;
    private Boolean estado;
    private LocalDateTime timestamp;
}
