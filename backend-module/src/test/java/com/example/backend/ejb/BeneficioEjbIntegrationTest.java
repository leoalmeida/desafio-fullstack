package com.example.backend.ejb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.ejb.BusinessException;
import com.example.ejb.BeneficioEjbService;
import com.example.ejb.entity.Beneficio;

/**
 * Testes integrados que exercitam a camada de persistência juntamente com a
 * implementação do serviço EJB. O contexto do Spring é iniciado com um banco de dados em memória
 * para que as entidades JPA possam ser gerenciadas normalmente.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class BeneficioEjbIntegrationTest{

    @Mock 
    private EntityManager em;

    @InjectMocks
    private BeneficioEjbService ejbService;
    
    @Test
    public void deveEncontrarModuloEJB(){
        assertNotNull(ejbService);
    }

    @Test
    @DisplayName("Deve transferir valores entre benefícios com sucesso")
    void deveTransferir_valoresEntreBeneficios() throws Exception {
        // given: dois benefícios com saldo inicial
        Beneficio fonte = Beneficio.builder()
                .id(1L)
                .nome("Beneficio Fonte")
                .descricao("Origem")
                .valor(new BigDecimal("1000.00"))
                .ativo(true)
                .build();
        Beneficio destino = Beneficio.builder()
                .id(2L)
                .nome("Beneficio Destino")
                .descricao("Alvo")
                .valor(new BigDecimal("500.00"))
                .ativo(true)
                .build();
        BigDecimal montante = new BigDecimal("100.00");

        // Arrange
        given(em.find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE))
                .willReturn(fonte);
        given(em.find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE))
                .willReturn(destino);
        given(em.merge(any(Beneficio.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // Act
        ejbService.transfer(fonte.getId(), destino.getId(), montante);

        // then: valores atualizados na base
        then(em).should(times(1)).find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        then(em).should(times(1)).find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        then(em).should(times(2)).merge(any(Beneficio.class));
    }

    @Test
    @DisplayName("Não deve transferir valores se o saldo do benefício de origem for insuficiente")
    void naoDeveTransferir_saldoInsuficiente() throws Exception {
        // given: dois benefícios sendo o fonte com saldo insuficiente
        Beneficio fonte = Beneficio.builder()
                .id(1L)
                .nome("Beneficio Fonte 2")
                .descricao("Origem 2")
                .valor(new BigDecimal("5.00"))
                .ativo(true)
                .build();
        Beneficio destino = Beneficio.builder()
                .id(2L)
                .nome("Beneficio Destino 2")
                .descricao("Alvo 2")
                .valor(new BigDecimal("0.00"))
                .ativo(true)
                .build();
        when(em.find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(fonte);
        when(em.find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(destino);
        
        BigDecimal montante = new BigDecimal("10.00");
        Throwable  throwable  = assertThrows(BusinessException.class,
                () -> ejbService.transfer(fonte.getId(), destino.getId(), montante));
        assertEquals(BusinessException.class, throwable.getClass());
        assertEquals("Saldo insuficiente para transferência", throwable.getMessage());
        verify(em, times(1))
                .find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(1))
                .find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(0))
                .merge(any(Beneficio.class));
    }
    @Test
    @DisplayName("Não deve transferir valores se um dos benefícios estiver cancelado")
    void naoDeveTransferir_beneficioCanceladof() throws Exception {
        // given: dois benefícios sendo um deles cancelado
        Beneficio fonte = Beneficio.builder()
                .id(1L)
                .nome("Beneficio Fonte 3")
                .descricao("Origem 3")
                .valor(new BigDecimal("100.00"))
                .ativo(false) // Cancelado
                .build();
        Beneficio destino = Beneficio.builder()
                .id(2L)
                .nome("Beneficio Destino 3")
                .descricao("Alvo 3")
                .valor(new BigDecimal("50.00"))
                .ativo(true)
                .build();
        when(em.find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(fonte);
        when(em.find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(destino);
        

        BigDecimal montante = new BigDecimal("10.00");
        Throwable  throwable  = assertThrows(BusinessException.class,
                () -> ejbService.transfer(fonte.getId(), destino.getId(), montante));
        assertEquals(BusinessException.class, throwable.getClass());
        assertEquals("Benefício de origem está cancelado", throwable.getMessage());
        verify(em, times(1))
                .find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(1))
                .find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(0))
                .merge(any(Beneficio.class));
    }

    @Test
    @DisplayName("Não deve transferir valores entre benefícios com o mesmo ID")
    void naoDeveTransferir_paraBeneficioMesmoId() throws Exception {
        // given: um benefício único
        Beneficio beneficio = Beneficio.builder()
                .id(1L)
                .nome("Beneficio Único")
                .descricao("Único")
                .valor(new BigDecimal("100.00"))
                .ativo(true)
                .build();
        
        BigDecimal montante = new BigDecimal("10.00");
        Throwable  throwable  = assertThrows(IllegalArgumentException.class,
                () -> ejbService.transfer(beneficio.getId(), beneficio.getId(), montante));
        assertEquals(IllegalArgumentException.class, throwable.getClass());
        assertEquals("Não é possível realizar transferência para o mesmo benefício", throwable.getMessage());
        verify(em, times(0))
                .find(Beneficio.class, beneficio.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(0))
                .merge(any(Beneficio.class));
    }
    
    @Test
    @DisplayName("Não deve transferir valores negativos")
    void naoDeveTransferir_valorNegativo() throws Exception {
        // given: dois benefícios com saldo inicial e valor de transferência negativo
        Beneficio fonte = Beneficio.builder()
                .id(1L)
                .nome("Beneficio Fonte 4")
                .descricao("Origem 4")
                .valor(new BigDecimal("100.00"))
                .ativo(true)
                .build();
        Beneficio destino = Beneficio.builder()
                .id(2L)
                .nome("Beneficio Destino 4")
                .descricao("Alvo 4")
                .valor(new BigDecimal("50.00"))
                .ativo(true)
                .build();
               
        BigDecimal montante = new BigDecimal("-10.00");
        Throwable  throwable  = assertThrows(IllegalArgumentException.class,
                () -> ejbService.transfer(fonte.getId(), destino.getId(), montante));
        assertEquals(IllegalArgumentException.class, throwable.getClass());
        assertEquals("Valor de transferência deve ser positivo", throwable.getMessage());
        verify(em, times(0))
                .find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(0))
                .find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(0))
                .merge(any(Beneficio.class));
    }
}

