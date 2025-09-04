package com.microservices.accountingservice.application.service;

import com.microservices.accountingservice.domain.dto.CuentaDto;
import com.microservices.accountingservice.domain.entity.Cuenta;
import com.microservices.accountingservice.domain.exception.CuentaAlreadyExistsException;
import com.microservices.accountingservice.domain.exception.CuentaNotFoundException;
import com.microservices.accountingservice.domain.mapper.CuentaMapper;
import com.microservices.accountingservice.domain.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;

    public CuentaDto crearCuenta(CuentaDto cuentaDto) {
        log.info("Creando cuenta con número: {}", cuentaDto.getNumeroCuenta());
        
        if (cuentaRepository.existsByNumeroCuenta(cuentaDto.getNumeroCuenta())) {
            throw new CuentaAlreadyExistsException(
                "Ya existe una cuenta con el número: " + cuentaDto.getNumeroCuenta()
            );
        }

        Cuenta cuenta = cuentaMapper.toEntity(cuentaDto);
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        
        log.info("Cuenta creada exitosamente con ID: {}", cuentaGuardada.getId());
        return cuentaMapper.toDto(cuentaGuardada);
    }

    @Transactional(readOnly = true)
    public List<CuentaDto> obtenerTodasLasCuentas() {
        log.info("Obteniendo todas las cuentas");
        List<Cuenta> cuentas = cuentaRepository.findAll();
        return cuentaMapper.toDtoList(cuentas);
    }

    @Transactional(readOnly = true)
    public List<CuentaDto> obtenerCuentasActivas() {
        log.info("Obteniendo cuentas activas");
        List<Cuenta> cuentas = cuentaRepository.findAllActiveAccounts();
        return cuentaMapper.toDtoList(cuentas);
    }

    @Transactional(readOnly = true)
    public List<CuentaDto> obtenerCuentasPorCliente(Long clienteId) {
        log.info("Obteniendo cuentas del cliente: {}", clienteId);
        List<Cuenta> cuentas = cuentaRepository.findByClienteIdAndEstadoTrue(clienteId);
        return cuentaMapper.toDtoList(cuentas);
    }

    @Transactional(readOnly = true)
    public CuentaDto obtenerCuentaPorId(Long id) {
        log.info("Obteniendo cuenta por ID: {}", id);
        Cuenta cuenta = cuentaRepository.findById(id)
            .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));
        return cuentaMapper.toDto(cuenta);
    }

    @Transactional(readOnly = true)
    public CuentaDto obtenerCuentaPorNumero(String numeroCuenta) {
        log.info("Obteniendo cuenta por número: {}", numeroCuenta);
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
            .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));
        return cuentaMapper.toDto(cuenta);
    }

    public CuentaDto actualizarCuenta(Long id, CuentaDto cuentaDto) {
        log.info("Actualizando cuenta con ID: {}", id);
        
        Cuenta cuentaExistente = cuentaRepository.findById(id)
            .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));

        // Verificar si el número de cuenta ya existe en otra cuenta
        if (!cuentaExistente.getNumeroCuenta().equals(cuentaDto.getNumeroCuenta()) &&
            cuentaRepository.existsByNumeroCuenta(cuentaDto.getNumeroCuenta())) {
            throw new CuentaAlreadyExistsException(
                "Ya existe una cuenta con el número: " + cuentaDto.getNumeroCuenta()
            );
        }

        cuentaMapper.updateEntity(cuentaDto, cuentaExistente);
        Cuenta cuentaActualizada = cuentaRepository.save(cuentaExistente);
        
        log.info("Cuenta actualizada exitosamente con ID: {}", id);
        return cuentaMapper.toDto(cuentaActualizada);
    }

    public CuentaDto desactivarCuenta(Long id) {
        log.info("Desactivando cuenta con ID: {}", id);
        
        Cuenta cuenta = cuentaRepository.findById(id)
            .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));
        
        cuenta.setEstado(false);
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        
        log.info("Cuenta desactivada exitosamente con ID: {}", id);
        return cuentaMapper.toDto(cuentaActualizada);
    }

}

