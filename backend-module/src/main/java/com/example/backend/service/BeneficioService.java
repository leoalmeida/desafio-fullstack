package com.example.backend.service;

import java.util.List;

import com.example.backend.dto.BeneficioRequestDto;
import com.example.backend.dto.BeneficioResponseDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.ejb.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;

public interface BeneficioService {
    List<BeneficioResponseDto> buscarTodosBeneficios() throws BusinessException;
    BeneficioResponseDto buscarBeneficioPorId(@NonNull Long id) throws EntityNotFoundException, IllegalArgumentException, BusinessException;
    BeneficioResponseDto criarBeneficio(@NonNull BeneficioRequestDto beneficio) throws IllegalArgumentException, BusinessException;
    BeneficioResponseDto alterarBeneficio(@NonNull Long id, @NonNull BeneficioRequestDto beneficio) throws EntityNotFoundException, IllegalArgumentException, BusinessException;
    BeneficioResponseDto alterarStatusBeneficio(@NonNull Long id, boolean status) throws BusinessException, IllegalArgumentException, EntityNotFoundException; 
    void removerBeneficio(@NonNull Long id) throws EntityNotFoundException, IllegalArgumentException, BusinessException;
    void realizarTransferencia(@NonNull TransferenciaDto dto) throws IllegalArgumentException, BusinessException;
}
