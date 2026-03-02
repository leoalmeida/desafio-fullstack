package com.example.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.backend.factory.TestFactory;
import com.example.backend.mapper.BeneficioMapper;
import com.example.ejb.BusinessException;
import com.example.backend.repository.*;
import com.example.backend.util.ObjectsValidator;
import com.example.ejb.BeneficioEjbService;
import com.example.backend.dto.BeneficioRequestDto;
import com.example.backend.dto.BeneficioResponseDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.ejb.entity.Beneficio;

import lombok.extern.slf4j.Slf4j;

/**
 * Testes para o serviço de beneficios.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Slf4j
public class BeneficioServiceTest {

    @Mock
    private BeneficioRepository repository;

    @Mock
    private BeneficioEjbService ejbService;

    @Mock
    private ObjectsValidator<BeneficioRequestDto> validator;

    @InjectMocks
    private BeneficioService service;

    private BeneficioResponseDto beneficioDto1;
    private BeneficioResponseDto beneficioDto2;
    private BeneficioResponseDto beneficioDtoInvalido;

    @BeforeEach
    void setUp() {
        beneficioDto1 = TestFactory.gerarBeneficioResponseDto(true);
        beneficioDto2 = TestFactory.gerarBeneficioResponseDto(true);
        beneficioDtoInvalido = TestFactory.gerarBeneficioResponseDto(false);
        beneficioDtoInvalido.setNome("");
    }

    @Test
    @DisplayName("Deve gerar BusinessException ao criar benefício com dados inválidos")
    void deveGerarException_quandoBeneficioInvalido() {
        log.info("Criando novo benefício: {}", beneficioDtoInvalido.getNome());
        //given  - precondition or setup
        BeneficioRequestDto input = BeneficioMapper.mapRequest(beneficioDtoInvalido);

        // Act & Assert
        Throwable  throwable  = assertThrows(BusinessException.class, () -> {
            service.criarBeneficio(input);
        });
        
        assertEquals(BusinessException.class, throwable.getClass());
        then(repository).should(never()).findById(any(Long.class));
        then(repository).should(never()).save(any(Beneficio.class));
        then(validator).should().validate(input);
    }

    @Test
    @DisplayName("Deve criar benefício com dados válidos")
    void deveCriarBeneficio_quandoBeneficioValido() {
        //given  - precondition or setups
        Beneficio entity = BeneficioMapper.mapResponse(beneficioDto1, false);
        BeneficioRequestDto beneficioRequest = BeneficioMapper.mapRequest(beneficioDto1);
        given(repository.saveAndFlush(any(Beneficio.class)))
                .willReturn(entity);
        given(validator.validate(beneficioRequest))
                .willReturn(beneficioRequest);
        
        log.info("Criando novo benefício: {}", entity.getNome());
        // Act
        BeneficioResponseDto result = service.criarBeneficio(beneficioRequest);

        // Assert
        assertNotNull(result);
        then(repository).should().saveAndFlush(any(Beneficio.class));
        then(validator).should().validate(beneficioRequest);
    }

    @Test
    @DisplayName("Deve alterar benefício com dados válidos")
    public void deveAlterarBeneficio_quandoBeneficioValido() {
        //given  - precondition or setup
        Beneficio entity = BeneficioMapper.mapResponse(beneficioDto2, false);
        entity.setNome("BeneficioAlterado");
        entity.setDescricao("DescricaoAlterada");
        entity.setValor(new BigDecimal(1000.00));
        entity.setAtivo(true);
        BeneficioRequestDto beneficioRequest = BeneficioMapper.mapRequest(entity);
        given(validator.validate(beneficioRequest)).willReturn(beneficioRequest);
        given(repository.findById(entity.getId())).willReturn(Optional.of(entity));
        given(repository.saveAndFlush(any(Beneficio.class))).willReturn(entity);

        // Executa o método
        BeneficioResponseDto tested = service.alterarBeneficio(entity.getId(),beneficioRequest);

        // Verifica o resultado
        assertNotNull(tested, "Beneficio salvo não deveria ser nulo");
        assertEquals(entity.getId(), tested.getId(),"Beneficio salvo deveria ter o mesmo ID");
        assertEquals(beneficioRequest.getNome(), tested.getNome(),"Beneficio deveria ter o nome modificado");
        assertEquals(beneficioRequest.getDescricao(), tested.getDescricao(),"Beneficio deveria ter a descrição modificada");
        assertEquals(beneficioRequest.getValor(), tested.getValor(),"Beneficio deveria ter o valor modificado");
        assertEquals(beneficioRequest.getAtivo(), tested.getAtivo(),"Beneficio deveria ter o status modificado");

        // Verifica se o método do repositório foi chamado
        then(repository).should().findById(entity.getId());
        then(repository).should().saveAndFlush(any(Beneficio.class));
        then(validator).should().validate(beneficioRequest);
    }

    @Test
    @DisplayName("Deve gerar BusinessException ao alterar benefício com dados inválidos")
    public void deveGerarBusinessException_quandoAlterarBeneficioInvalido() {
        //given  - precondition or setup
        BeneficioRequestDto beneficioRequestInvalido = BeneficioMapper.mapRequest(beneficioDtoInvalido);
        
        given(validator.validate(beneficioRequestInvalido))
                .willThrow(new BusinessException("Nome é obrigatório"));

        // Act & Assert
        Throwable  throwable  = assertThrows(BusinessException.class, () -> {
            service.alterarBeneficio(beneficioDtoInvalido.getId(),beneficioRequestInvalido);
        });

        assertEquals(BusinessException.class, throwable.getClass());

        // Verifica se o método do repositório foi chamado
        then(repository).should(never()).findById(any(Long.class));
        then(repository).should(never()).saveAndFlush(any(Beneficio.class));
        then(validator).should().validate(beneficioRequestInvalido);
    }

    @Test
    @DisplayName("Deve alterar status do benefício com dados válidos")
    public void deveAlterarStatusBeneficio_quandoBeneficioValido() {
        //given  - precondition or setup
        Beneficio entity = BeneficioMapper.mapResponse(beneficioDto2,false);
        entity.setAtivo(!entity.getAtivo());
        given(repository.findById(entity.getId()))
                .willReturn(Optional.of(entity));
        given(repository.saveAndFlush(entity))
                .willReturn(entity);
        
        // Executa o método
        BeneficioResponseDto tested = service.alterarStatusBeneficio(entity.getId(), entity.getAtivo());

        // Verifica o resultado
        assertNotNull(tested, "Beneficio salvo não deveria ser nulo");
        assertEquals(entity.getId(), tested.getId(),"Beneficio salvo deveria ter o mesmo ID");
        assertEquals(entity.getAtivo(), tested.getAtivo(),"Beneficio salvo deveria ter status ativo");

        // Verifica se o método do repositório foi chamado
        then(repository).should().findById(entity.getId());
        then(repository).should().saveAndFlush(entity);
    }

    @Test
    @DisplayName("Deve encontrar benefício com ID válido")
    public void deveEncontrarBeneficio_quandoIdBeneficioValido() {
        Beneficio mockBeneficio = mock(Beneficio.class);
        mockBeneficio.setId(1L);
        
        // Configura o mock
        given(repository.findById(1L))
                .willReturn(Optional.of(mockBeneficio));

        // Executa o método
        BeneficioResponseDto tested = service.buscarBeneficioPorId(1L);

        // Verifica o resultado
        assertNotNull(tested, "Beneficio deveria ser encontrado");
        assertEquals(mockBeneficio.getId(), 
                    tested.getId(),
                    "Beneficio encontrado deveria ter o ID correto");

        // Verifica se o método do repositório foi chamado
        then(repository).should().findById(1L);
    }

    @Test
    @DisplayName("Deve gerar IllegalArgumentException ao consultar benefício com ID inválido")
    public void deveGerarIllegalArgumentException_quandoConsultarComIdBeneficioInvalido() {
        Long idInvalido = -1L;

        // Executa o método
        Throwable  throwable  = 
                assertThrows(IllegalArgumentException.class, () ->{
                    service.buscarBeneficioPorId(idInvalido);
                });
        assertEquals(IllegalArgumentException.class, throwable.getClass());

        // Verifica se o método do repositório foi chamado
        then(repository).should(never()).findById(idInvalido);
    }

    @Test
    @DisplayName("Deve retornar todos os benefícios quando existirem múltiplos benefícios")
    public void deveRetornarTodosBeneficios_QuandoExistirMultiplosBeneficios() {
        // Configura o mock
        Beneficio mockBeneficio1 = mock(Beneficio.class);
        Beneficio mockBeneficio2 = mock(Beneficio.class);
        Beneficio mockBeneficio3 = mock(Beneficio.class);
        given(repository.findAll())
                .willReturn(Arrays.asList(mockBeneficio1, mockBeneficio2, mockBeneficio3));

        // Executa o método
        List<BeneficioResponseDto> beneficios = service.buscarTodosBeneficios();

        // Verifica o resultado
        assertEquals(3, beneficios.size(),"Deveria encontrar 3 beneficios");

        // Verifica se o método do repositório foi chamado
        then(repository).should().findAll();
    }

    @Test
    @DisplayName("Deve retornar vazio quando não existirem benefícios")
    public void deveRetornarVazio_QuandoNaoExistirBeneficios() {
        // Configura o mock
        given(repository.findAll())
                .willReturn(new ArrayList<>());

        // Executa o método
        List<BeneficioResponseDto> beneficios = service.buscarTodosBeneficios();

        // Verifica o resultado
        assertNotNull(beneficios, "Lista não deveria retornar nulo");
        assertEquals(0, beneficios.size(),"Deveria encontrar 0 beneficios");

        // Verifica se o método do repositório foi chamado
        then(repository).should().findAll();
    }

    @Test
    @DisplayName("Deve remover benefício quando ID válido")
    public void deveRemoverBeneficio_quandoIdBeneficioValido() {
        Beneficio entity = BeneficioMapper.mapResponse(beneficioDto2,false);
        // Configura o mock
        given(repository.findById(entity.getId()))
                .willReturn(Optional.of(entity));
        //doNothing().when(repository).deleteById(entity.getId());

        // Executa o método
        service.removerBeneficio(entity.getId());

        then(repository).should().findById(entity.getId());
        then(repository).should().deleteById(entity.getId());

    }

    @Test
    @DisplayName("Deve gerar IllegalArgumentException ao remover benefício com ID inválido")
    public void deveGerarIllegalArgumentException_quandoIdBeneficioInvalido() {
        Long idInvalido = -1L;
    
        // Executa o método
        Throwable  throwable  = 
                assertThrows(IllegalArgumentException.class, () ->{
                    service.removerBeneficio(idInvalido);
                });
        assertEquals(IllegalArgumentException.class, throwable.getClass());

        // Verifica se o método do repositório foi chamado
        then(repository).should(never()).findById(anyLong());
        then(repository).should(never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve realizar transferência com dados válidos")
    public void deveRealizarTransferencia_quandoDadosValidos() {
        //Given        
        TransferenciaDto dto = TransferenciaDto.builder()
            .fromId(1L)
            .toId(2L)
            .valor(new BigDecimal(100.00))
            .build();
        
        //doNothing().when(ejbService).transfer(dto.getFromId(), dto.getToId(), dto.getValor());

        // Executa o método
        service.realizarTransferencia(dto);
        
        // Verifica o resultado
        then(ejbService).should().transfer(dto.getFromId(), dto.getToId(), dto.getValor());
    }

    @Test
    @DisplayName("Deve gerar IllegalArgumentException ao realizar transferência com identificadores iguais")
    public void deveGerarIllegalArgumentException_quandoTransferenciaComIdentificadoresIguais() {
        //Given        
        TransferenciaDto dto = TransferenciaDto.builder()
            .fromId(2L)
            .toId(2L)
            .valor(new BigDecimal(100.00))
            .build();

         // Executa o método
        Throwable  throwable  = 
            assertThrows(IllegalArgumentException.class, () ->{
                service.realizarTransferencia(dto);
            });

        // Verifica o resultado
        assertEquals(IllegalArgumentException.class, throwable.getClass());
        then(ejbService).should(never()).transfer(dto.getFromId(), dto.getToId(), dto.getValor());
    }

    @Test
    @DisplayName("Deve gerar BusinessException ao realizar transferência com valores inválidos")
    public void deveGerarBusinessException_quandoTransferenciaValoresInvalidos() {
        // Configura o mock para o serviço EJB
        TransferenciaDto dto = TransferenciaDto.builder()
            .fromId(1L)
            .toId(2L)
            .valor(new BigDecimal(100.00))
            .build();

        willThrow(new BusinessException("Saldo insuficiente para transferência."))
            .given(ejbService)
            .transfer(dto.getFromId(), dto.getToId(), dto.getValor());

        // Executa o método
        Throwable  throwable  = 
                assertThrows(BusinessException.class, () ->{
                    service.realizarTransferencia(dto);
                });

        // Verifica o resultado
        assertEquals(BusinessException.class, throwable.getClass());
        then(ejbService).should().transfer(dto.getFromId(), dto.getToId(), dto.getValor());
    }
}
