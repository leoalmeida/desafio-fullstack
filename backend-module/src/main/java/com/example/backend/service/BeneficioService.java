package com.example.backend.service;


import java.util.List;
import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.TransferenciaDTO;

import lombok.NonNull;

/**
 * Serviço responsável pelo gerenciamento de beneficios.
 */
public interface BeneficioService {
    
    BeneficioDto criarBeneficio(@NonNull BeneficioDto Beneficio);

    BeneficioDto alterarBeneficio(@NonNull Long id,@NonNull BeneficioDto Beneficio);
    
    BeneficioDto alterarStatusBeneficio(@NonNull Long id, boolean status);
    
    void realizarTransferencia(@NonNull TransferenciaDTO dto);

    BeneficioDto buscarBeneficioPorId(@NonNull Long id);
    
    List<BeneficioDto> buscarTodosBeneficios();

    List<BeneficioDto> filtrarBeneficiosPorStatus(boolean ativo);

    List<BeneficioDto> filtrarBeneficiosPorNome(String nome);
    
    void removerBeneficio(@NonNull Long BeneficioId);
    
}