package com.microservices.accountingservice.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.accountingservice.domain.event.ClienteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "cliente-events", groupId = "accounting-service-group")
    public void handleClienteEvent(String mensaje) {
        try {
            ClienteEvent evento = objectMapper.readValue(mensaje, ClienteEvent.class);
            log.info("Evento de cliente recibido: {} para cliente ID: {}", 
                    evento.getEventType(), evento.getClienteId());
            
            // Aquí se podría sincronizar información del cliente en el servicio de contabilidad
            // Por ejemplo, actualizar una tabla de clientes local o cache
            
        } catch (Exception e) {
            log.error("Error al procesar evento de cliente: {}", e.getMessage());
        }
    }
}
