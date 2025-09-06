package com.microservices.accountingservice.domain.mapper;

import com.microservices.accountingservice.domain.dto.MovimientoDto;
import com.microservices.accountingservice.domain.entity.Movimiento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovimientoMapper {

    public Movimiento toEntity(MovimientoDto movimientoDto) {
        if (movimientoDto == null) {
            return null;
        }
        
        Movimiento movimiento = new Movimiento();
        movimiento.setId(movimientoDto.getId());
        movimiento.setFecha(movimientoDto.getFecha());
        movimiento.setTipoMovimiento(movimientoDto.getTipoMovimiento());
        movimiento.setValor(movimientoDto.getValor());
        movimiento.setSaldo(movimientoDto.getSaldo());
        movimiento.setClienteId(movimientoDto.getClienteId());
        return movimiento;
    }

    public MovimientoDto toDto(Movimiento movimiento) {
        if (movimiento == null) {
            return null;
        }
        
        MovimientoDto dto = new MovimientoDto();
        dto.setId(movimiento.getId());
        dto.setFecha(movimiento.getFecha());
        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setValor(movimiento.getValor());
        dto.setSaldo(movimiento.getSaldo());
        dto.setCuentaId(movimiento.getCuenta() != null ? movimiento.getCuenta().getId() : null);
        dto.setClienteId(movimiento.getClienteId());
        dto.setNumeroCuenta(movimiento.getCuenta() != null ? movimiento.getCuenta().getNumeroCuenta() : null);
        dto.setTipoCuenta(movimiento.getCuenta() != null ? movimiento.getCuenta().getTipoCuenta() : null);
        dto.setFechaCreacion(movimiento.getFechaCreacion());
        dto.setFechaActualizacion(movimiento.getFechaActualizacion());
        return dto;
    }

    public List<MovimientoDto> toDtoList(List<Movimiento> movimientos) {
        if (movimientos == null) {
            return null;
        }
        return movimientos.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void updateEntity(MovimientoDto movimientoDto, Movimiento movimiento) {
        if (movimientoDto == null || movimiento == null) {
            return;
        }
        
        movimiento.setTipoMovimiento(movimientoDto.getTipoMovimiento());
    }
}

