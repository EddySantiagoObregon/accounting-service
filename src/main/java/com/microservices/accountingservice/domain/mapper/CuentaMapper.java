package com.microservices.accountingservice.domain.mapper;

import com.microservices.accountingservice.domain.dto.CuentaDto;
import com.microservices.accountingservice.domain.entity.Cuenta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CuentaMapper {

    public Cuenta toEntity(CuentaDto cuentaDto) {
        if (cuentaDto == null) {
            return null;
        }
        
        Cuenta cuenta = new Cuenta();
        cuenta.setId(cuentaDto.getId());
        cuenta.setNumeroCuenta(cuentaDto.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaDto.getTipoCuenta());
        cuenta.setSaldoInicial(cuentaDto.getSaldoInicial());
        cuenta.setSaldoActual(cuentaDto.getSaldoActual());
        cuenta.setClienteId(cuentaDto.getClienteId());
        cuenta.setEstado(cuentaDto.getEstado());
        return cuenta;
    }

    public CuentaDto toDto(Cuenta cuenta) {
        if (cuenta == null) {
            return null;
        }
        
        CuentaDto dto = new CuentaDto();
        dto.setId(cuenta.getId());
        dto.setNumeroCuenta(cuenta.getNumeroCuenta());
        dto.setTipoCuenta(cuenta.getTipoCuenta());
        dto.setSaldoInicial(cuenta.getSaldoInicial());
        dto.setSaldoActual(cuenta.getSaldoActual());
        dto.setClienteId(cuenta.getClienteId());
        dto.setEstado(cuenta.getEstado());
        dto.setFechaCreacion(cuenta.getFechaCreacion());
        dto.setFechaActualizacion(cuenta.getFechaActualizacion());
        return dto;
    }

    public List<CuentaDto> toDtoList(List<Cuenta> cuentas) {
        if (cuentas == null) {
            return null;
        }
        return cuentas.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void updateEntity(CuentaDto cuentaDto, Cuenta cuenta) {
        if (cuentaDto == null || cuenta == null) {
            return;
        }
        
        cuenta.setNumeroCuenta(cuentaDto.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaDto.getTipoCuenta());
        cuenta.setSaldoInicial(cuentaDto.getSaldoInicial());
        cuenta.setSaldoActual(cuentaDto.getSaldoActual());
        cuenta.setClienteId(cuentaDto.getClienteId());
        cuenta.setEstado(cuentaDto.getEstado());
    }
}

