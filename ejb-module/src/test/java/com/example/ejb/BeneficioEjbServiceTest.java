package com.example.ejb;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.ejb.entity.Beneficio;
import java.math.BigDecimal;
import java.util.logging.Logger;


/*
 * Classe de Testes unitários para o BeneficioEjbService, 
 * utilizando Mockito para simular o EntityManager e as entidades envolvidas. 
 */
public class BeneficioEjbServiceTest {
    private static final Logger logger = Logger.getLogger(BeneficioEjbService.class.getName());

    private BeneficioEjbService service;
    private EntityManager em;

    @BeforeEach
    void setup() {
        em = mock(EntityManager.class);
        service = new BeneficioEjbService(em);
    }

    @Test
    void deveTransferir_quandoSaldoSuficiente() {
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = new BigDecimal("25.00");

        Beneficio from = Beneficio.builder().id(fromId).valor(new BigDecimal("100.00")).ativo(true).build();
        Beneficio to   = Beneficio.builder().id(toId).valor(new BigDecimal("10.00")).ativo(true).build();

        when(em.find(Beneficio.class, fromId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(from);
        when(em.find(Beneficio.class, toId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(to);

        service.transfer(fromId, toId, amount);

        assertEquals(new BigDecimal("75.00"), from.getValor());
        assertEquals(new BigDecimal("35.00"), to.getValor());
        verify(em).merge(from);
        verify(em).merge(to);
    }

    @Test
    void naoDeveTransferir_quandoSaldoInsuficiente() {
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = new BigDecimal("200.00");

        Beneficio from = Beneficio.builder().id(fromId).valor(new BigDecimal("100.00")).ativo(true).build();
        Beneficio to   = Beneficio.builder().id(toId).valor(new BigDecimal("10.00")).ativo(true).build();

        when(em.find(Beneficio.class, fromId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(from);
        when(em.find(Beneficio.class, toId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(to);
        logger.info(String.format("Transferindo: %d -> %d, valor: %s", fromId, toId, amount));
        
        assertThrows(BusinessException.class,
                () -> service.transfer(fromId, toId, amount));

        verify(em, never()).merge(any());
    }

    @Test
    void naoDeveTransferir_quandoBeneficioInativo() {
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = new BigDecimal("10.00");

        Beneficio from = Beneficio.builder().id(fromId).nome("Benef 1").valor(new BigDecimal("100.00")).ativo(false).build();
        Beneficio to   = Beneficio.builder().id(toId).nome("Benef 2").valor(new BigDecimal("10.00")).ativo(true).build();

        when(em.find(Beneficio.class, fromId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(from);
        when(em.find(Beneficio.class, toId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)).thenReturn(to);

        assertThrows(BusinessException.class, () -> service.transfer(fromId, toId, amount));
        verify(em, never()).merge(any());
    }

    @Test
    void naoDeveTransferir_quandoParametrosInvalidos() {
        // identificadores nulos
        assertThrows(IllegalArgumentException.class, () -> service.transfer(null, 1L, new BigDecimal("1.00")));
        assertThrows(IllegalArgumentException.class, () -> service.transfer(1L, null, new BigDecimal("1.00")));

        // valor zerado/negativo 
        assertThrows(IllegalArgumentException.class, () -> service.transfer(1L, 2L, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> service.transfer(1L, 2L, new BigDecimal("-1.00")));

        // identificadores iguais
        assertThrows(IllegalArgumentException.class, () -> service.transfer(1L, 1L, new BigDecimal("1.00")));
    }
}
