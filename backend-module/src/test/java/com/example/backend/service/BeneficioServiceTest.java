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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.backend.factory.TestFactory;
import com.example.backend.mapper.BeneficioMapper;
import com.example.ejb.BusinessException;
import com.example.backend.repository.*;
import com.example.backend.util.ObjectsValidator;
import com.example.ejb.BeneficioEjbService;
import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.ejb.entity.Beneficio;

import lombok.extern.slf4j.Slf4j;

/**
 * Testes para o serviço de beneficios.
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
public class BeneficioServiceTest {

    @Mock
    private BeneficioRepository repository;

    @Mock
    private BeneficioEjbService ejbService;

    @Mock
    private ObjectsValidator<BeneficioDto> validator;

    @InjectMocks
    private BeneficioService service;

    private BeneficioDto beneficioDto1;
    private BeneficioDto beneficioDto2;
    private BeneficioDto beneficioDtoInvalido;

    @BeforeEach
    void setUp() {
        beneficioDto1 = TestFactory.gerarBeneficioDto();
        beneficioDto2 = TestFactory.gerarBeneficioDto();
        beneficioDtoInvalido = TestFactory.gerarBeneficioDto();
        beneficioDtoInvalido.setNome("");
    }

    @Test
    @DisplayName("Deve gerar BusinessException ao criar benefício com dados inválidos")
    void deveGerarException_quandoBeneficioInvalido() {
        log.info("Criando novo benefício: {}", beneficioDtoInvalido.getNome());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            service.criarBeneficio(beneficioDtoInvalido);
        });
    }

    @Test
    @DisplayName("Deve criar benefício com dados válidos")
    void deveCriarBeneficio_quandoBeneficioValido() {
        //given  - precondition or setups
        Beneficio inputEntity = BeneficioMapper.map(beneficioDto1, true);
        Beneficio outputEntity = BeneficioMapper.map(beneficioDto1, false);
        given(repository.save(inputEntity))
                .willReturn(outputEntity);
        given(validator.validate(beneficioDto1))
                .willReturn(beneficioDto1);
        
        log.info("Criando novo benefício: {}", beneficioDto1.getNome());
        // Act
        BeneficioDto result = service.criarBeneficio(beneficioDto1);

        // Assert
        assertNotNull(result);
        verify(repository, times(1)).save(inputEntity);
        verify(validator, times(1)).validate(beneficioDto1);
    }

    @Test
    @DisplayName("Deve criar benefício com dados válidos")
    public void deveCriarBeneficio_quandoEnviadoObjetoValido() {
        //given  - precondition or setup
        Beneficio inputEntity = BeneficioMapper.map(beneficioDto1, true);
        Beneficio outputEntity = BeneficioMapper.map(beneficioDto1, false);
        given(validator.validate(beneficioDto1)).willReturn(beneficioDto1);
        given(repository.save(inputEntity)).willReturn(outputEntity);

        // Executa o método
        BeneficioDto tested = service.criarBeneficio(beneficioDto1);

        // Verifica o resultado
        assertNotNull(tested, "Beneficio salvo não deveria ser nulo");
        assertEquals(beneficioDto1.getId(), tested.getId(),"Beneficio salvo deveria ter o mesmo ID");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).save(inputEntity);
        verify(validator, times(1)).validate(beneficioDto1);
    }

    @Test
    @DisplayName("Deve alterar benefício com dados válidos")
    public void deveAlterarBeneficio_quandoBeneficioValido() {
        //given  - precondition or setup
        beneficioDto2.setNome("BeneficioAlterado");
        beneficioDto2.setDescricao("DescricaoAlterada");
        beneficioDto2.setValor(new BigDecimal(1000.00));
        beneficioDto2.setAtivo(true);
        Beneficio outputEntity = BeneficioMapper.map(beneficioDto2, false);
        given(validator.validate(beneficioDto2)).willReturn(beneficioDto2);
        given(repository.save(outputEntity)).willReturn(outputEntity);
        given(repository.findById(beneficioDto2.getId())).willReturn(Optional.of(outputEntity));

        // Executa o método
        BeneficioDto tested = service.alterarBeneficio(beneficioDto2.getId(),beneficioDto2);

        // Verifica o resultado
        assertNotNull(tested, "Beneficio salvo não deveria ser nulo");
        assertEquals(beneficioDto2.getId(), tested.getId(),"Beneficio salvo deveria ter o mesmo ID");
        assertEquals(beneficioDto2.getNome(), tested.getNome(),"Beneficio deveria ter o nome modificado");
        assertEquals(beneficioDto2.getDescricao(), tested.getDescricao(),"Beneficio deveria ter a descrição modificada");
        assertEquals(beneficioDto2.getValor(), tested.getValor(),"Beneficio deveria ter o valor modificado");
        assertEquals(beneficioDto2.getAtivo(), tested.getAtivo(),"Beneficio deveria ter o status modificado");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).save(outputEntity);
        verify(repository, times(1)).findById(any(Long.class));
        verify(validator, times(1)).validate(any(BeneficioDto.class));
    }

    @Test
    @DisplayName("Deve gerar BusinessException ao alterar benefício com dados inválidos")
    public void deveGerarBusinessException_quandoAlterarBeneficioInvalido() {
        //given  - precondition or setup
        Beneficio entity = BeneficioMapper.map(beneficioDtoInvalido,false);

        given(validator.validate(beneficioDtoInvalido))
                .willThrow(new BusinessException("Nome é obrigatório"));

        // Act & Assert
        Throwable  throwable  = assertThrows(BusinessException.class, () -> {
            service.alterarBeneficio(beneficioDtoInvalido.getId(),beneficioDtoInvalido);
        });

        assertEquals(BusinessException.class, throwable.getClass());

        // Verifica se o método do repositório foi chamado
        verify(repository, times(0)).save(any(Beneficio.class));
        verify(repository, times(0)).findById(any(Long.class));
        verify(validator, times(1)).validate(any(BeneficioDto.class));
    }

    @Test
    @DisplayName("Deve alterar status do benefício com dados válidos")
    public void deveAlterarStatusBeneficio_quandoBeneficioValido() {
        //given  - precondition or setup
        Beneficio entity = BeneficioMapper.map(beneficioDto2,false);
        entity.setAtivo(!entity.getAtivo());
        given(repository.save(entity))
                .willReturn(entity);
        given(repository.findById(beneficioDto2.getId()))
                .willReturn(Optional.of(entity));
        
        
        // Executa o método
        BeneficioDto tested = service.alterarStatusBeneficio(beneficioDto2.getId(), beneficioDto2.getAtivo());

        // Verifica o resultado
        assertNotNull(tested, "Beneficio salvo não deveria ser nulo");
        assertEquals(beneficioDto2.getId(), tested.getId(),"Beneficio salvo deveria ter o mesmo ID");
        assertEquals(entity.getAtivo(), tested.getAtivo(),"Beneficio salvo deveria ter status ativo");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findById(beneficioDto2.getId());
        verify(repository, times(1)).save(any(Beneficio.class));
    }

    @Test
    @DisplayName("Deve encontrar benefício com ID válido")
    public void deveEncontrarBeneficio_quandoIdBeneficioValido() {
        Beneficio mockBeneficio = mock(Beneficio.class);
        mockBeneficio.setId(1L);
        
        // Configura o mock
        doReturn(Optional.of(mockBeneficio)).when(repository).findById(1L);

        // Executa o método
        BeneficioDto tested = service.buscarBeneficioPorId(1L);

        // Verifica o resultado
        assertNotNull(tested, "Beneficio deveria ser encontrado");
        assertEquals(mockBeneficio.getId(), 
                    tested.getId(),
                    "Beneficio encontrado deveria ter o ID correto");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findById(1L);
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
        verify(repository, times(0)).findById(idInvalido);
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
        List<BeneficioDto> beneficios = service.buscarTodosBeneficios();

        // Verifica o resultado
        assertEquals(3, beneficios.size(),"Deveria encontrar 3 beneficios");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar vazio quando não existirem benefícios")
    public void deveRetornarVazio_QuandoNaoExistirBeneficios() {
        // Configura o mock
        given(repository.findAll())
                .willReturn(new ArrayList<>());

        // Executa o método
        List<BeneficioDto> beneficios = service.buscarTodosBeneficios();

        // Verifica o resultado
        assertNotNull(beneficios, "Lista não deveria retornar nulo");
        assertEquals(0, beneficios.size(),"Deveria encontrar 0 beneficios");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve remover benefício quando ID válido")
    public void deveRemoverBeneficio_quandoIdBeneficioValido() {
        Beneficio entity = BeneficioMapper.map(beneficioDto2,false);
        // Configura o mock
        given(repository.findById(entity.getId()))
                .willReturn(Optional.of(entity));
        doNothing().when(repository).deleteById(entity.getId());

        // Executa o método
        service.removerBeneficio(entity.getId());

        verify(repository, times(1)).findById(entity.getId());
        verify(repository, times(1)).deleteById(entity.getId());

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
        verify(repository, times(0)).findById(idInvalido);
        verify(repository, times(0)).deleteById(idInvalido);
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
        doNothing().when(ejbService).transfer(dto.getFromId(), dto.getToId(), dto.getValor());

        // Executa o método
        service.realizarTransferencia(dto);
        
        // Verifica o resultado
        verify(ejbService, times(1)).transfer(dto.getFromId(), dto.getToId(), dto.getValor());
    }

    @Test
    @DisplayName("Deve gerar BusinessException ao realizar transferência com identificadores iguais")
    public void deveGerarBusinessException_quandoTransferenciaComIdentificadoresIguais() {
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
        verify(ejbService, times(0)).transfer(dto.getFromId(), dto.getToId(), dto.getValor());
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

        doThrow(new BusinessException("Saldo insuficiente para transferência."))
            .when(ejbService).transfer(dto.getFromId(), dto.getToId(), dto.getValor());

        // Executa o método
        Throwable  throwable  = 
                assertThrows(BusinessException.class, () ->{
                    service.realizarTransferencia(dto);
                });


        // Verifica o resultado
        assertEquals(BusinessException.class, throwable.getClass());
        verify(ejbService, times(1)).transfer(dto.getFromId(), dto.getToId(), dto.getValor());
    }
}
