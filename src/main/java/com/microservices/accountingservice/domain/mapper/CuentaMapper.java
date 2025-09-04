package com.microservices.accountingservice.domain.mapper;

import com.microservices.accountingservice.domain.dto.CuentaDto;
import com.microservices.accountingservice.domain.entity.Cuenta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    CuentaMapper INSTANCE = Mappers.getMapper(CuentaMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "movimientos", ignore = true)
    @Mapping(target = "saldoActual", ignore = true)
    Cuenta toEntity(CuentaDto cuentaDto);

    @Mapping(target = "saldoActual", expression = "java(cuenta.getSaldoActual())")
    CuentaDto toDto(Cuenta cuenta);

    List<CuentaDto> toDtoList(List<Cuenta> cuentas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "movimientos", ignore = true)
    @Mapping(target = "saldoActual", ignore = true)
    Cuenta updateEntity(CuentaDto cuentaDto, @org.mapstruct.MappingTarget Cuenta cuenta);
}

