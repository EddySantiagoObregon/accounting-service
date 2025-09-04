package com.microservices.accountingservice.domain.mapper;

import com.microservices.accountingservice.domain.dto.MovimientoDto;
import com.microservices.accountingservice.domain.entity.Movimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    MovimientoMapper INSTANCE = Mappers.getMapper(MovimientoMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "cuenta", ignore = true)
    @Mapping(target = "numeroCuenta", ignore = true)
    @Mapping(target = "tipoCuenta", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "nombreCliente", ignore = true)
    Movimiento toEntity(MovimientoDto movimientoDto);

    @Mapping(target = "numeroCuenta", source = "cuenta.numeroCuenta")
    @Mapping(target = "tipoCuenta", source = "cuenta.tipoCuenta")
    @Mapping(target = "clienteId", source = "cuenta.clienteId")
    @Mapping(target = "nombreCliente", ignore = true)
    MovimientoDto toDto(Movimiento movimiento);

    List<MovimientoDto> toDtoList(List<Movimiento> movimientos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "cuenta", ignore = true)
    @Mapping(target = "valor", ignore = true)
    @Mapping(target = "saldo", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    Movimiento updateEntity(MovimientoDto movimientoDto, @org.mapstruct.MappingTarget Movimiento movimiento);
}

