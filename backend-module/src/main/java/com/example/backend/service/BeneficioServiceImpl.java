package com.example.backend.service;

import com.example.backend.dto.BeneficioRequestDto;
import com.example.backend.dto.BeneficioResponseDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.backend.mapper.BeneficioMapper;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.validator.ObjectsValidator;
import com.example.ejb.BeneficioEjbService;
import com.example.ejb.entity.Beneficio;
import com.example.ejb.exception.BusinessException;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação do serviço responsável pelo gerenciamento de beneficios.
 */
@Service
@Slf4j
public class BeneficioServiceImpl implements BeneficioService {

    private final BeneficioRepository repository;

    private final BeneficioEjbService ejbService;

    private final ObjectsValidator<BeneficioRequestDto> validador;

    @Autowired
    public BeneficioServiceImpl(final BeneficioRepository repository, final BeneficioEjbService ejbService) {
        this.repository = Objects.requireNonNull(repository, "BeneficioRepository não pode ser nulo");
        this.ejbService = Objects.requireNonNull(ejbService, "BeneficioEjbService não pode ser nulo");
        this.validador = new ObjectsValidator<BeneficioRequestDto>();
    }

    /**
     * Cria um novo benefício no sistema.
     * Valida os dados fornecidos antes de persistir no banco de dados.
     *
     * @param dto Dados do benefício a ser criado
     * @return BeneficioResponseDto com os dados do benefício criado, incluindo ID gerado
     * @throws IllegalArgumentException se os dados fornecidos forem inválidos
     * @throws BusinessException se ocorrer um erro de negócio ao criar o benefício
     */
    @Override
    @Transactional
    public BeneficioResponseDto criarBeneficio(@Nonnull final BeneficioRequestDto dto)
            throws IllegalArgumentException, BusinessException {
        if (null == dto.getNome() || null == dto.getValor()) {
            throw new IllegalArgumentException("Objeto inválido");
        }
        log.info("Criando novo benefício: {}", dto.getNome());
        // Valida o beneficio e converte antess de salvar
        Beneficio entityIn = BeneficioMapper.mapRequest(validador.validate(dto));
        if (entityIn == null) {
            throw new BusinessException("Erro ao converter dados.");
        }
        log.info("Benefício mapeado para entidade: {}", entityIn.getNome());
        Beneficio entityOut = repository.saveAndFlush(entityIn); // Salva a beneficio no repositório

        return BeneficioMapper.mapResponse(entityOut);
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
    @Override
    @Transactional
    public void realizarTransferencia(@Nonnull final TransferenciaDto dto)
            throws IllegalArgumentException, BusinessException {
        validateTransferenciaDto(dto);
        try {
            ejbService.transfer(dto.getFromId(), dto.getToId(), dto.getValor());
            log.info(
                    "Transferência realizada com sucesso entre benefícios ID={}, ID={}",
                    dto.getFromId(),
                    dto.getToId());
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

    private void validateTransferenciaDto(@Nonnull final TransferenciaDto dto) {
        // Validação básica dos dados de transferência
        if (null == dto.getFromId() || null == dto.getToId()) {
            throw new IllegalArgumentException("IDs de origem e destino são obrigatórios");
        }
        log.info(
                "Iniciando transferência entre benefícios ID={}, ID={}, VALOR={}",
                dto.getFromId(),
                dto.getToId(),
                dto.getValor());
        if (dto.getFromId().equals(dto.getToId())) {
            throw new IllegalArgumentException("Não é possível realizar transferência para o mesmo benefício");
        }
        if (dto.getValor() == null || dto.getValor().signum() <= 0) {
            throw new IllegalArgumentException("Valor de transferência deve ser positivo");
        }
        log.info("Iniciando transferência entre benefícios ID={}, ID={}", dto.getFromId(), dto.getToId());
    }

    /**
     * Altera o status (ativo/cancelado) de um benefício existente.
     *
     * @param id Identificador único do benefício
     * @param status Novo status a ser definido (true = ativo, false = cancelado)
     * @return BeneficioResponseDto atualizado com o novo status
     * @throws BusinessException se o benefício não for encontrado
     * @throws IllegalArgumentException se o ID fornecido for inválido
     * @throws EntityNotFoundException se o benefício não for encontrado
     */
    @Override
    @Transactional
    public BeneficioResponseDto alterarStatusBeneficio(@Nonnull final Long id, final boolean status)
            throws BusinessException, IllegalArgumentException, EntityNotFoundException {
        if (id <= 0) {
            throw new IllegalArgumentException("Identificador do benefício inválido");
        }
        log.info("Alterando status do benefício ID: {}", id);
        Beneficio entity =
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Beneficio não encontrado"));
        entity.setAtivo(status);
        Beneficio saved = repository.saveAndFlush(entity);
        log.info("Status do benefício ID={} alterado para: {}", id, status ? "Ativo" : "Cancelado");
        return BeneficioMapper.mapResponse(saved);
    }

    /**
     * Atualiza os dados de um benefício existente.
     * Valida os dados fornecidos antes de persistir as alterações.
     *
     * @param id Identificador único do benefício a ser atualizado
     * @param dto Novos dados do benefício
     * @return BeneficioResponseDto com os dados atualizados
     * @throws EntityNotFoundException se o benefício não for encontrado
     * @throws IllegalArgumentException se os dados fornecidos forem inválidos
     * @throws BusinessException se ocorrer um erro de negócio ao atualizar o benefício
     */
    @Override
    @Transactional
    public BeneficioResponseDto alterarBeneficio(@Nonnull final Long id, @Nonnull final BeneficioRequestDto dto)
            throws EntityNotFoundException, IllegalArgumentException, BusinessException {
        if (id <= 0) {
            throw new IllegalArgumentException("Identificador inválido");
        }
        log.info("Alterando dados do benefício ID={} com os seguintes dados: {}", id, dto.toString());
        Beneficio validated = BeneficioMapper.mapRequest(validador.validate(dto)); // Valida o beneficio antes de salvar

        if (validated == null) {
            throw new BusinessException("Erro ao validar objeto");
        }

        Beneficio entity =
                repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Beneficio não encontrado"));

        entity.updateData(validated);
        Beneficio saved = repository.saveAndFlush(entity);
        log.info("Benefício ID={} alterado com sucesso", id);
        return BeneficioMapper.mapResponse(saved);
    }

    /**
     * Busca um benefício específico pelo seu identificador único.
     *
     * @param id Identificador único do benefício
     * @return BeneficioResponseDto com os dados do benefício encontrado
     * @throws EntityNotFoundException se o benefício não for encontrado
     * @throws IllegalArgumentException se o ID fornecido for inválido
     */
    @Override
    @Transactional(readOnly = true)
    public BeneficioResponseDto buscarBeneficioPorId(@Nonnull final Long id)
            throws EntityNotFoundException, IllegalArgumentException {
        if (id <= 0) {
            throw new IllegalArgumentException("Identificador inválido");
        }
        return repository
                .findById(id)
                .map(BeneficioMapper::mapResponse)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio não encontrado"));
    }

    /**
     * Retorna uma lista de todos os benefícios cadastrados no sistema.
     *
     * @return Lista contendo BeneficioResponseDtos de todos os benefícios
     */
    @Override
    @Transactional(readOnly = true)
    public List<BeneficioResponseDto> buscarTodosBeneficios() {
        return repository.findAll().stream().map(BeneficioMapper::mapResponse).collect(Collectors.toList());
    }

    /**
     * Filtra benefícios pelo status (ativo ou cancelado).
     * Útil para listar benefícios ativos ou inativos.
     *
     * @param ativo Status de filtro (true = ativos, false = cancelados)
     * @return Lista de BeneficioResponseDtos que correspondem ao status informado
     */
    @Override
    @Transactional(readOnly = true)
    public List<BeneficioResponseDto> filtrarBeneficiosPorStatus(final boolean ativo) {
        return repository.searchByStatus(ativo).stream()
                .map(BeneficioMapper::mapResponse)
                .collect(Collectors.toList());
    }

    /**
     * Filtra benefícios pelo nome usando busca parcial.
     * Útil para realizar buscas por nome ou parte do nome do benefício.
     *
     * @param nome Nome ou parte do nome a ser pesquisado
     * @return Lista de BeneficioResponseDtos que correspondem ao critério de busca
     */
    @Override
    @Transactional(readOnly = true)
    public List<BeneficioResponseDto> filtrarBeneficiosPorNome(final String nome) {
        return repository.searchByNome(nome).stream()
                .map(BeneficioMapper::mapResponse)
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
    @Override
    @Transactional
    public void removerBeneficio(@Nonnull final Long beneficioId)
            throws BusinessException, IllegalArgumentException, EntityNotFoundException {
        if (beneficioId <= 0) {
            throw new IllegalArgumentException("Identificador inválido para remoção.");
        }

        BeneficioResponseDto beneficio = repository
                .findById(beneficioId)
                .map(BeneficioMapper::mapResponse)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio não encontrado"));
        if (beneficio == null) {
            throw new BusinessException("Beneficio não encontrado para remoção.");
        }
        repository.deleteById(beneficioId);
        log.info("Benefício removido: ID={}", beneficioId);
    }
}
