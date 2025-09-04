package com.microservices.accountingservice.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.accountingservice.domain.event.CuentaEvent;
import com.microservices.accountingservice.domain.event.MovimientoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountingEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishCuentaEvent(CuentaEvent evento) {
        try {
            String mensaje = objectMapper.writeValueAsString(evento);
            kafkaTemplate.send("cuenta-events", mensaje);
            log.info("Evento de cuenta publicado: {}", evento.getEventType());
        } catch (JsonProcessingException e) {
            log.error("Error al serializar evento de cuenta: {}", e.getMessage());
        }
    }

    public void publishMovimientoEvent(MovimientoEvent evento) {
        try {
            String mensaje = objectMapper.writeValueAsString(evento);
            kafkaTemplate.send("movimiento-events", mensaje);
            log.info("Evento de movimiento publicado: {}", evento.getEventType());
        } catch (JsonProcessingException e) {
            log.error("Error al serializar evento de movimiento: {}", e.getMessage());
        }
    }
}
