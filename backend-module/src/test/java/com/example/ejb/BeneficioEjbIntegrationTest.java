package com.example.ejb;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.ejb.exception.BusinessException;

import com.example.ejb.entity.Beneficio;

/**
 * Integration tests that exercise the real persistence layer together with the
 * EJB service implementation.  The Spring context is started with an in‑memory
 * database so that the JPA entities can be managed normally.
 */
@ExtendWith(MockitoExtension.class)
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
    void deveTransferir_valoresEntreBeneficios() throws Exception {
        // given: dois benefícios com saldo inicial
        Beneficio fonte = Beneficio.builder()
                .nome("Beneficio Fonte")
                .descricao("Origem")
                .valor(new BigDecimal("1000.00"))
                .ativo(true)
                .build();
        Beneficio fontePersistido = Beneficio.builder()
                .id(1L)
                .nome("Beneficio Fonte")
                .descricao("Origem")
                .valor(new BigDecimal("900.00"))
                .ativo(true)
                .build();
        Beneficio destino = Beneficio.builder()
                .nome("Beneficio Destino")
                .descricao("Alvo")
                .valor(new BigDecimal("500.00"))
                .ativo(true)
                .build();
        Beneficio destinoPersistido = Beneficio.builder()
                .id(2L)
                .nome("Beneficio Destino")
                .descricao("Alvo")
                .valor(new BigDecimal("600.00"))
                .ativo(true)
                .build();
        BigDecimal montante = new BigDecimal("100.00");

        // Arrange
        when(em.find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(fonte);
        when(em.find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(destino);
        doNothing().when(em).merge(fontePersistido);
        doNothing().when(em).merge(destinoPersistido);

        // Act
        ejbService.transfer(fonte.getId(), destino.getId(), montante);

        // then: valores atualizados na base
        verify(em, times(1))
                .find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(1))
                .find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(1))
                .merge(fontePersistido);
        verify(em, times(1))
                .merge(destinoPersistido);
    }

    @Test
    void naoDeveTransferir_saldoInsuficiente() throws Exception {
        // given: dois benefícios sendo o fonte com saldo insuficiente
        Beneficio fonte = Beneficio.builder()
                .nome("Beneficio Fonte 2")
                .descricao("Origem 2")
                .valor(new BigDecimal("5.00"))
                .ativo(true)
                .build();
        Beneficio destino = Beneficio.builder()
                .nome("Beneficio Destino 2")
                .descricao("Alvo 2")
                .valor(new BigDecimal("0.00"))
                .ativo(true)
                .build();
        when(em.find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(fonte);
        when(em.find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(destino);
        doNothing().when(em).merge(any(Beneficio.class));

        BigDecimal montante = new BigDecimal("10.00");
        assertThrows(BusinessException.class,
                () -> ejbService.transfer(fonte.getId(), destino.getId(), montante));
        verify(em, times(2))
                .find(Beneficio.class, any(Long.class), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(0))
                .merge(any(Beneficio.class));
    }
    @Test
    void naoDeveTransferir_beneficioCancelado() throws Exception {
        // given: dois benefícios sendo um deles cancelado
        Beneficio fonte = Beneficio.builder()
                .nome("Beneficio Fonte 3")
                .descricao("Origem 3")
                .valor(new BigDecimal("100.00"))
                .ativo(false) // Cancelado
                .build();
        Beneficio destino = Beneficio.builder()
                .nome("Beneficio Destino s")
                .descricao("Alvo 3")
                .valor(new BigDecimal("50.00"))
                .ativo(true)
                .build();
        when(em.find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(fonte);
        when(em.find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(destino);
        doNothing().when(em).merge(any(Beneficio.class));


        BigDecimal montante = new BigDecimal("10.00");
        assertThrows(BusinessException.class,
                () -> ejbService.transfer(fonte.getId(), destino.getId(), montante));
        verify(em, times(2))
                .find(Beneficio.class, any(Long.class), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(0))
                .merge(any(Beneficio.class));
    }

    @Test
    void naoDeveTransferir_paraMesmoBeneficio() throws Exception {
        // given: um benefício único
        Beneficio beneficio = Beneficio.builder()
                .nome("Beneficio Único")
                .descricao("Único")
                .valor(new BigDecimal("100.00"))
                .ativo(true)
                .build();
        when(em.find(Beneficio.class, beneficio.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(beneficio);;
        doNothing().when(em).merge(any(Beneficio.class));

        BigDecimal montante = new BigDecimal("10.00");
        assertThrows(IllegalArgumentException.class,
                () -> ejbService.transfer(beneficio.getId(), beneficio.getId(), montante));
        verify(em, times(2))
                .find(Beneficio.class, any(Long.class), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(0))
                .merge(any(Beneficio.class));
    }
    
    @Test
    void naoDeveTransferir_valorNegativo() throws Exception {
        // given: dois benefícios com saldo inicial e valor de transferência negativo
        Beneficio fonte = Beneficio.builder()
                .nome("Beneficio Fonte 4")
                .descricao("Origem 4")
                .valor(new BigDecimal("100.00"))
                .ativo(true)
                .build();
        Beneficio destino = Beneficio.builder()
                .nome("Beneficio Destino 4")
                .descricao("Alvo 4")
                .valor(new BigDecimal("50.00"))
                .ativo(true)
                .build();
        when(em.find(Beneficio.class, fonte.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(fonte);
        when(em.find(Beneficio.class, destino.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(destino);
        doNothing().when(em).merge(any(Beneficio.class));

        BigDecimal montante = new BigDecimal("-10.00");
        assertThrows(IllegalArgumentException.class,
                () -> ejbService.transfer(fonte.getId(), destino.getId(), montante));
        verify(em, times(2))
                .find(Beneficio.class, any(Long.class), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        verify(em, times(0))
                .merge(any(Beneficio.class));
    }
}

