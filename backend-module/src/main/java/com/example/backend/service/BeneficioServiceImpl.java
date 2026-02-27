package com.example.backend.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.TransferenciaDTO;
import com.example.backend.exception.BusinessException;
import com.example.backend.repository.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.util.ObjectsValidator;
import com.example.ejb.BeneficioEjbService;

import jakarta.persistence.OptimisticLockException;

/**
 * Implementação do serviço responsável pelo gerenciamento de beneficios.
 */
@Service
@RequiredArgsConstructor
public class BeneficioServiceImpl implements BeneficioService {

    @Autowired
    private ObjectsValidator<Beneficio> validador;

    @Autowired
    private BeneficioRepository repository;
    
    @Autowired
    private BeneficioEjbService ejbService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public BeneficioDto criarBeneficio(@NonNull BeneficioDto dto) {
        Beneficio entityIn = modelMapper.map(dto,Beneficio.class);
        entityIn.setId(null); // Garante que o ID seja nulo para criação
        validador.validate(entityIn);// Valida o beneficio antes de salvar
        Beneficio entityOut = repository.save(entityIn);// Salva a beneficio no repositório

        return modelMapper.map(entityOut, BeneficioDto.class);
    }

    @Override
    @Transactional
    public void realizarTransferencia(@NonNull TransferenciaDTO dto) {
        // Validação básica dos dados de transferência
        if (null == dto.getFromId() || null == dto.getToId()) {
            throw new IllegalArgumentException("Benefícios de origem e destino são obrigatórios.");
        }
        if (dto.getFromId().equals(dto.getToId())) {
            throw new IllegalArgumentException("Não é possível realizar transferirência para o mesmo benefício.");
        }
        if (dto.getValor() == null || dto.getValor().signum() <= 0) {
            throw new IllegalArgumentException("Valor de transferência deve ser positivo.");
        }
        
        try{
            ejbService.transfer(dto.getFromId(), dto.getToId(), dto.getValor());
        } catch (OptimisticLockException e) {
            throw new BusinessException("Erro ao realizar transferência de benefício.");
        }
    }

    @Override
    @Transactional
    public BeneficioDto alterarStatusBeneficio(@NonNull Long id, boolean status) {
        Beneficio entity = repository.findById(id).orElseThrow(() -> new BusinessException("Beneficio não encontrado para atualização."));
        entity.setAtivo(status);
        return modelMapper.map(repository.save(entity), BeneficioDto.class);
    }

    @Override
    @Transactional
    public BeneficioDto alterarBeneficio(@NonNull Long id, @NonNull BeneficioDto dto) {
        Beneficio entity = validador.validate(modelMapper.map(dto,Beneficio.class));// Valida a beneficio antes  de realizar o merge
        if (this.buscarBeneficioPorId(id) != null) {;
            return  modelMapper.map(repository.save(Objects.requireNonNull(entity)), BeneficioDto.class);
        } else {
            throw new BusinessException("Beneficio não encontrado para atualização.");
        }        
    }

    @Override
    public BeneficioDto buscarBeneficioPorId(@NonNull Long id) {
        return repository.findById(id)
                    .map(this::fillDto)
                    .orElseThrow(() -> new BusinessException("Beneficio não encontrado para atualização."));
    }


    @Override
    public List<BeneficioDto> buscarTodosBeneficios() {
        return repository.findAll().stream()
                    .map(this::fillDto)
                    .collect(Collectors.toList());
    }

    private BeneficioDto fillDto(Beneficio entity){
        BeneficioDto dtoOut = modelMapper.map(entity, BeneficioDto.class);
        return dtoOut;
    }

    @Override
    @Transactional
    public void removerBeneficio(@NonNull Long beneficioId) {
        if (beneficioId==null){
            throw new BusinessException("Identificador inválido para remoção.");
        }
        BeneficioDto beneficio = this.buscarBeneficioPorId(beneficioId);
        if (beneficio == null){
            throw new BusinessException("Beneficio não encontrado para remoção.");
        }
        repository.deleteById(beneficioId);
    }
    
}