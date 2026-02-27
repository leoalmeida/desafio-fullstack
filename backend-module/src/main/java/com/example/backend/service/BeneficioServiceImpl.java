package com.example.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BeneficioServiceImpl implements BeneficioService {

    private static final Logger logger = LoggerFactory.getLogger(BeneficioServiceImpl.class);

    private final BeneficioRepository repository;
    private final BeneficioEjbService ejbService;
    private final ObjectsValidator<Beneficio> validador;
    private final ModelMapper modelMapper;


    public BeneficioServiceImpl(BeneficioRepository repository,BeneficioEjbService ejbService,ObjectsValidator<Beneficio> validador, ModelMapper modelMapper) {
        this.repository = repository;
        this.ejbService = ejbService;
        this.validador = validador;
        this.modelMapper = modelMapper;
    }

    /**
     * Cria um novo benefício no sistema.
     * Valida os dados fornecidos antes de persistir no banco de dados.
     * 
     * @param dto Dados do benefício a ser criado
     * @return BeneficioDto com os dados do benefício criado, incluindo ID gerado
     */
    @Override
    @Transactional
    public BeneficioDto criarBeneficio(@NonNull BeneficioDto dto) {
        Beneficio entityIn = modelMapper.map(dto,Beneficio.class);
        entityIn.setId(null); // Garante que o ID seja nulo para criação
        validador.validate(entityIn);// Valida o beneficio antes de salvar
        Beneficio entityOut = repository.save(entityIn);// Salva a beneficio no repositório

        return modelMapper.map(entityOut, BeneficioDto.class);
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
    public void realizarTransferencia(@NonNull TransferenciaDTO dto) {
        // Validação básica dos dados de transferência
        if (null == dto || null == dto.getFromId() || null == dto.getToId()) {
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
            logger.info("Transferência realizada com sucesso entre benefícios ID={}, ID={}", dto.getFromId(), dto.getToId());
        } catch (OptimisticLockException e) {
            throw new BusinessException("Erro ao realizar transferência de benefício.");
        }
    }

    /**
     * Altera o status (ativo/cancelado) de um benefício existente.
     * 
     * @param id Identificador único do benefício
     * @param status Novo status a ser definido (true = ativo, false = cancelado)
     * @return BeneficioDto atualizado com o novo status
     * @throws BusinessException se o benefício não for encontrado
     */
    @Override
    @Transactional
    public BeneficioDto alterarStatusBeneficio(@NonNull Long id, boolean status) {
        Beneficio entity = repository.findById(id).orElseThrow(() -> new BusinessException("Beneficio não encontrado para atualização."));
        entity.setAtivo(status);
        return modelMapper.map(repository.save(entity), BeneficioDto.class);
    }

    /**
     * Atualiza os dados de um benefício existente.
     * Valida os dados fornecidos antes de persistir as alterações.
     * 
     * @param id Identificador único do benefício a ser atualizado
     * @param dto Novos dados do benefício
     * @return BeneficioDto com os dados atualizados
     * @throws BusinessException se o benefício não for encontrado
     */
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

    /**
     * Busca um benefício específico pelo seu identificador único.
     * 
     * @param id Identificador único do benefício
     * @return BeneficioDto com os dados do benefício encontrado
     * @throws BusinessException se o benefício não for encontrado
     */
    @Override
    @Transactional(readOnly = true)
    public BeneficioDto buscarBeneficioPorId(@NonNull Long id) {
        return repository.findById(id)
                    .map(this::fillDto)
                    .orElseThrow(() -> new BusinessException("Beneficio não encontrado para atualização."));
    }


    /**
     * Retorna uma lista de todos os benefícios cadastrados no sistema.
     * 
     * @return Lista contendo BeneficioDtos de todos os benefícios
     */
    @Override
    @Transactional(readOnly = true)
    public List<BeneficioDto> buscarTodosBeneficios() {
        return repository.findAll().stream()
                    .map(this::fillDto)
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
                .map(this::fillDto)
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
                .map(this::fillDto)
                .collect(Collectors.toList());
    }

    /**
     * Remove um benefício do sistema.
     * Valida se o benefício existe antes de proceder com a remoção.
     * 
     * @param beneficioId Identificador único do benefício a ser removido
     * @throws BusinessException se o benefício não for encontrado ou ID for inválido
     */
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
        logger.info("Benefício removido: ID={}", beneficioId);

    }

    private BeneficioDto fillDto(Beneficio entity) {
        return modelMapper.map(entity, BeneficioDto.class);
    }
}