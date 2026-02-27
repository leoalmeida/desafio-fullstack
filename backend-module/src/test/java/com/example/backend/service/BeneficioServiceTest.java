package com.example.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.factory.TestFactory;
import com.example.backend.exception.BusinessException;
import com.example.backend.repository.*;
import com.example.backend.util.ObjectsValidator;
import com.example.backend.dto.BeneficioDto;

/**
 * Testes para o serviço de beneficios.
 */
@ExtendWith(MockitoExtension.class)
public class BeneficioServiceTest {

    @Mock
    private BeneficioRepository repository;

    @Mock
    private ObjectsValidator<Beneficio> validator;
    
    @InjectMocks
    private BeneficioServiceImpl service;

    @Test
    void deveGerarException_quandoBeneficioInvalido() {
        BeneficioDto beneficioInvalido = BeneficioDto.builder()
                    .id(0L)
                    .nome("")
                    .descricao("")
                    .build();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            service.criarBeneficio(beneficioInvalido);
        });
    }

    @Test
    void deveCriarBeneficio_quandoBeneficioValido() {
        BeneficioDto beneficio = TestFactory.gerarBeneficioDto();

        // Arrange
        when(repository.save(any(Beneficio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BeneficioDto result = service.criarBeneficio(beneficio);

        // Assert
        assertNotNull(result);
        verify(repository).save(any(Beneficio.class));
    }

    @Test
    public void deveCriarBeneficio_quandoEnviadoMockValido() {
        BeneficioDto mockBeneficio = mock(BeneficioDto.class);

        // Configura o mock
        when(validator.validate(any(Beneficio.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.save(any(Beneficio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Executa o método
        BeneficioDto tested = service.criarBeneficio(mockBeneficio);

        // Verifica o resultado
        assertNotNull(tested, "Beneficio salvo não deveria ser nulo");
        assertEquals(mockBeneficio.getId(), tested.getId(),"s salvo deveria ter o mesmo ID");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).save(any(Beneficio.class));
        verify(validator, times(1)).validate(any(Beneficio.class));
    }

    @Test
    public void deveAlterarBeneficio_quandoBeneficioValido() {
        BeneficioDto mockBeneficio = mock(BeneficioDto.class);
        mockBeneficio.setNome("BeneficioAlterado");
        mockBeneficio.setDescricao("DescricaoAlterada");
        mockBeneficio.setValor(new BigDecimal(1000.00));
        mockBeneficio.setAtivo(true);

        // Configura o mock
        when(validator.validate(any(Beneficio.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.save(any(Beneficio.class))).thenAnswer(invocation -> invocation.getArgument(0));
       
        // Executa o método
        BeneficioDto tested = service.alterarBeneficio(mockBeneficio.getId(),mockBeneficio);

        // Verifica o resultado
        assertNotNull(tested, "Beneficio salvo não deveria ser nulo");
        assertEquals(mockBeneficio.getId(), tested.getId(),"Beneficio salvo deveria ter o mesmo ID");
        assertEquals(mockBeneficio.getNome(), tested.getNome(),"Beneficio deveria ter o nome modificado");
        assertEquals(mockBeneficio.getDescricao(), tested.getDescricao(),"Beneficio deveria ter a descrição modificada");
        assertEquals(mockBeneficio.getValor(), tested.getValor(),"Beneficio deveria ter o valor modificado");
        assertEquals(mockBeneficio.getAtivo(), tested.getAtivo(),"Beneficio deveria ter o status modificado");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).save(any(Beneficio.class));
        verify(validator, times(1)).validate(any(Beneficio.class));
    }

    @Test
    public void deveGerarBusinessException_quandoAlterarBeneficioInvalido() {
        BeneficioDto mockBeneficio = mock(BeneficioDto.class);

        // Configura o mock
        doThrow(IllegalArgumentException.class).when(validator).validate(any(Beneficio.class));

        // Executa o método
        Throwable  throwable  = 
                assertThrows(BusinessException.class, () ->{
                    service.alterarBeneficio(mockBeneficio.getId(),mockBeneficio);
                });
        assertEquals(BusinessException.class, throwable.getClass());

        // Verifica se o método do repositório foi chamado
        verify(repository, times(0)).save(any(Beneficio.class));
        verify(validator, times(1)).validate(any(Beneficio.class));
    }

    @Test
    public void deveAlterarStatusBeneficio_quandoBeneficioValido() {
        BeneficioDto mockBeneficio = mock(BeneficioDto.class);
        mockBeneficio.setId(1L);
        mockBeneficio.setAtivo(false);

        // Configura o mock
        doReturn(mockBeneficio).when(repository).findById(1L);
        when(repository.save(any(Beneficio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Executa o método
        BeneficioDto tested = service.alterarStatusBeneficio(1L, true);

        // Verifica o resultado
        assertNotNull(tested, "Beneficio salvo não deveria ser nulo");
        assertEquals(mockBeneficio.getId(), tested.getId(),"Beneficio salvo deveria ter o mesmo ID");
        assertEquals(true, tested.getAtivo(),"Beneficio salvo deveria ter status ativo");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Beneficio.class));
    }

    @Test
    public void deveEncontrarBeneficio_quandoIdBeneficioValido() {
        BeneficioDto mockBeneficio = mock(BeneficioDto.class);
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
    public void deveGerarBusinessException_quandoConsultarComIdBeneficioInvalido() {
        Long idInvalido = -1L;

        // Configura o mock
        doThrow(NoSuchElementException.class)
            .when(repository).findById(idInvalido);

        // Executa o método
        Throwable  throwable  = 
                assertThrows(BusinessException.class, () ->{
                    service.buscarBeneficioPorId(idInvalido);
                });
        assertEquals(BusinessException.class, throwable.getClass());

        // Verifica se o método do repositório foi chamado
        verify(repository, times(0)).findById(idInvalido);
    }

    @Test
    public void deveRetornarTodosBeneficios_QuandoExistirMultiplosBeneficios() {
        // Configura o mock
        Beneficio mockBeneficio1 = mock(Beneficio.class);
        Beneficio mockBeneficio2 = mock(Beneficio.class);
        Beneficio mockBeneficio3 = mock(Beneficio.class);
        doReturn(Arrays.asList(mockBeneficio1, mockBeneficio2, mockBeneficio3))
            .when(repository).findAll();

        // Executa o método
        List<BeneficioDto> beneficios = service.buscarTodosBeneficios();

        // Verifica o resultado
        assertEquals(3, beneficios.size(),"Deveria encontrar 3 beneficios");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findAll();
    }

    @Test
    public void deveRetornarVazio_QuandoNaoExistirBeneficios() {
        // Configura o mock
        doReturn(new ArrayList<>())
            .when(repository).findAll();

        // Executa o método
        List<BeneficioDto> beneficios = service.buscarTodosBeneficios();

        // Verifica o resultado
        assertNotNull(beneficios, "Lista não deveria retornar nulo");
        assertEquals(0, beneficios.size(),"Deveria encontrar 0 beneficios");

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findAll();
    }

    @Test
    public void deveRemoverBeneficio_quandoIdBeneficioValido() {
        // Configura o mock
        doNothing().when(repository).deleteById(1L);

        // Executa o método
        service.removerBeneficio(1L);

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void deveGerarBusinessException_quandoIdBeneficioInvalido() {
        Long idInvalido = -1L;

        // Configura o mock
        doThrow(NoSuchElementException.class)
                .when(repository).deleteById(idInvalido);
    
        // Executa o método
        Throwable  throwable  = 
                assertThrows(BusinessException.class, () ->{
                    service.removerBeneficio(idInvalido);
                });
        assertEquals(BusinessException.class, throwable.getClass());

        // Verifica se o método do repositório foi chamado
        verify(repository, times(1)).findById(idInvalido);
        verify(repository, times(0)).deleteById(idInvalido);
    }

}
