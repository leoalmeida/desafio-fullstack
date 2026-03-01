package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.backend.mapper.BeneficioMapper;
import com.example.ejb.BusinessException;
import com.example.ejb.entity.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.util.ObjectsValidator;
import com.example.ejb.BeneficioEjbService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;

/**
 * Implementação do serviço responsável pelo gerenciamento de beneficios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficioService{

    @Autowired
    private BeneficioRepository repository;
    
    @Autowired
    private BeneficioEjbService ejbService;
    
    @Autowired
    private ObjectsValidator<BeneficioDto> validador;

    /**
     * Cria um novo benefício no sistema.
     * Valida os dados fornecidos antes de persistir no banco de dados.
     * 
     * @param dto Dados do benefício a ser criado
     * @return BeneficioDto com os dados do benefício criado, incluindo ID gerado
     * @throws IllegalArgumentException se os dados fornecidos forem inválidos
     * @throws BusinessException se ocorrer um erro de negócio ao criar o benefício
     */
    
    @Transactional
    public BeneficioDto criarBeneficio(@NonNull BeneficioDto dto) throws IllegalArgumentException, BusinessException {
        if (null == dto || null == dto.getNome() || null == dto.getValor()) {
            throw new IllegalArgumentException("Objeto inválido");
        }
        log.info("Criando novo benefício: {}", dto.getNome());
        // Valida o beneficio e converte antess de salvar
        Beneficio entityIn = BeneficioMapper.map(validador.validate(dto), true);
        if (entityIn == null) {
            throw new BusinessException("Erro ao converter dados.");
        }
        log.info("Benefício mapeado para entidade: {}", entityIn.getNome());
        Beneficio entityOut = repository.save(entityIn);// Salva a beneficio no repositório

        return BeneficioMapper.map(entityOut);
    }

    /**
     * Realiza uma transferência de valor entre dois benefícios.
     * Valida saldo suficiente e status ativo dos benefícios.
     * Utiliza o serviço EJB com locking pessimista para evitar condições de corrida.
     * 
     * @param dto Dados da transferência contendo ID de origem, destino e valor
     * @throws IllegalArgumentException se os dados forem inválidos
     * @throws BusinessException se ocorrer erro na operação
     */
    
    @Transactional
    public void realizarTransferencia(@NonNull TransferenciaDto dto) throws IllegalArgumentException, BusinessException {
        // Validação básica dos dados de transferência
        if (null == dto || null == dto.getFromId() || null == dto.getToId()) {
            throw new IllegalArgumentException("IDs de origem e destino são obrigatórios");
        }
        log.info("Iniciando transferência entre benefícios ID={}, ID={}, VALOR={}", dto.getFromId(), dto.getToId(),dto.getValor());
        if (dto.getFromId().equals(dto.getToId())) {
            throw new IllegalArgumentException("Não é possível realizar transferência para o mesmo benefício");
        }
        if (dto.getValor() == null || dto.getValor().signum() <= 0) {
            throw new IllegalArgumentException("Valor de transferência deve ser positivo");
        }
        log.info("Iniciando transferência entre benefícios ID={}, ID={}", dto.getFromId(), dto.getToId());
        
        try{
            ejbService.transfer(dto.getFromId(), dto.getToId(), dto.getValor());
            log.info("Transferência realizada com sucesso entre benefícios ID={}, ID={}", dto.getFromId(), dto.getToId());
        } catch (IllegalArgumentException ex) {
            log.error("Argumento inválido ao realizar transferência: {}", ex.getMessage());
            throw ex;
        } catch (BusinessException ex) {
            log.error("Erro de negócio ao realizar transferência: {}", ex.getMessage());
            throw ex;
        } catch (OptimisticLockException e) {
            throw new BusinessException("Erro de concorrência ao realizar transferência. Por favor, tente novamente.");
        }
    }

    /**
     * Altera o status (ativo/cancelado) de um benefício existente.
     * 
     * @param id Identificador único do benefício
     * @param status Novo status a ser definido (true = ativo, false = cancelado)
     * @return BeneficioDto atualizado com o novo status
     * @throws BusinessException se o benefício não for encontrado
     * @throws IllegalArgumentException se o ID fornecido for inválido
     * @throws EntityNotFoundException se o benefício não for encontrado
     */
    
    @Transactional
    public BeneficioDto alterarStatusBeneficio(@NonNull Long id, boolean status) throws BusinessException, IllegalArgumentException, EntityNotFoundException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identificador do benefício inválido");
        }
        log.info("Alterando status do benefício ID: {}", id);
        Beneficio entity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Beneficio não encontrado"));
        entity.setAtivo(status);
        Beneficio saved = repository.save(entity);
        log.info("Status do benefício ID={} alterado para: {}", id, status ? "Ativo" : "Cancelado");
        return BeneficioMapper.map(saved);
    }

    /**
     * Atualiza os dados de um benefício existente.
     * Valida os dados fornecidos antes de persistir as alterações.
     * 
     * @param id Identificador único do benefício a ser atualizado
     * @param dto Novos dados do benefício
     * @return BeneficioDto com os dados atualizados
     * @throws EntityNotFoundException se o benefício não for encontrado
     * @throws IllegalArgumentException se os dados fornecidos forem inválidos
     * @throws BusinessException se ocorrer um erro de negócio ao atualizar o benefício
     */
    
    @Transactional
    public BeneficioDto alterarBeneficio(@NonNull Long id, @NonNull BeneficioDto dto) throws EntityNotFoundException, IllegalArgumentException, BusinessException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identificador inválido");
        }

        log.info("Alterando dados do benefício ID={} com os seguintes dados: {}", id, dto.toString());
        Beneficio validated = BeneficioMapper.map(validador.validate(dto), false);// Valida o beneficio antes de salvar
        
        if (validated == null) {
            throw new BusinessException("Erro ao validar objeto");
        }

        Beneficio entity = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Beneficio não encontrado"));
        
        entity.updateData(validated);
        Beneficio saved = repository.save(entity);
        log.info("Benefício ID={} alterado com sucesso", id);
        return  BeneficioMapper.map(saved);   
    }

    /**
     * Busca um benefício específico pelo seu identificador único.
     * 
     * @param id Identificador único do benefício
     * @return BeneficioDto com os dados do benefício encontrado
     * @throws EntityNotFoundException se o benefício não for encontrado
     * @throws IllegalArgumentException se o ID fornecido for inválido
     */
    
    @Transactional(readOnly = true)
    public BeneficioDto buscarBeneficioPorId(@NonNull Long id) throws EntityNotFoundException, IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identificador inválido");
        }
        return repository.findById(id)
                    .map(BeneficioMapper::map)
                    .orElseThrow(() -> new EntityNotFoundException("Beneficio não encontrado"));
    }


    /**
     * Retorna uma lista de todos os benefícios cadastrados no sistema.
     * 
     * @return Lista contendo BeneficioDtos de todos os benefícios
     */
    
    @Transactional(readOnly = true)
    public List<BeneficioDto> buscarTodosBeneficios() {
        return repository.findAll().stream()
                    .map(BeneficioMapper::map)
                    .collect(Collectors.toList());
    }

    /**
     * Filtra benefícios pelo status (ativo ou cancelado).
     * Útil para listar benefícios ativos ou inativos.
     * 
     * @param ativo Status de filtro (true = ativos, false = cancelados)
     * @return Lista de BeneficioDtos que correspondem ao status informado
     */
    @Transactional(readOnly = true)
    public List<BeneficioDto> filtrarBeneficiosPorStatus(boolean ativo) {
        return repository.searchByStatus(ativo).stream()
                .map(BeneficioMapper::map)
                .collect(Collectors.toList());
    }

    /**
     * Filtra benefícios pelo nome usando busca parcial.
     * Útil para realizar buscas por nome ou parte do nome do benefício.
     * 
     * @param nome Nome ou parte do nome a ser pesquisado
     * @return Lista de BeneficioDtos que correspondem ao critério de busca
     */
    @Transactional(readOnly = true)
    public List<BeneficioDto> filtrarBeneficiosPorNome(String nome) {
        return repository.searchByNome(nome).stream()
                .map(BeneficioMapper::map)
                .collect(Collectors.toList());
    }

    /**
     * Remove um benefício do sistema.
     * Valida se o benefício existe antes de proceder com a remoção.
     * 
     * @param beneficioId Identificador único do benefício a ser removido
     * @throws BusinessException se o benefício não for encontrado ou ID for inválido
     * @throws IllegalArgumentException se o ID fornecido for inválido
     * @throws EntityNotFoundException se o benefício não for encontrado
     */
    
    @Transactional
    public void removerBeneficio(@NonNull Long beneficioId) throws BusinessException, IllegalArgumentException, EntityNotFoundException {
        if (beneficioId==null || beneficioId<=0){
            throw new IllegalArgumentException("Identificador inválido para remoção.");
        }
        
        BeneficioDto beneficio = repository.findById(beneficioId)
                    .map(BeneficioMapper::map)
                    .orElseThrow(() -> new EntityNotFoundException("Beneficio não encontrado"));
        if (beneficio == null){
            throw new BusinessException("Beneficio não encontrado para remoção.");
        }
        repository.deleteById(beneficioId);
        log.info("Benefício removido: ID={}", beneficioId);

    }

}