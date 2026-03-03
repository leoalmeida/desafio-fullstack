package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

import com.example.backend.dto.TransferenciaDto;
import com.example.backend.factory.TestFactory;
import com.example.backend.repository.*;
import com.example.ejb.BeneficioEjbService;
import com.example.ejb.entity.Beneficio;
import com.example.ejb.exception.BusinessException;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * Testes para o serviço de transferências.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Slf4j
public class TransferenciaServiceTest {

    @Mock
    private BeneficioRepository repository;

    @Mock
    private BeneficioEjbService ejbService;

    @InjectMocks
    private BeneficioServiceImpl service;

    private Beneficio beneficioDto1;
    private Beneficio beneficioDto2;
    private TransferenciaDto transferenciaDto1;
    private TransferenciaDto transferenciaDto2;

    @BeforeEach
    void setUp() {
        beneficioDto1 = TestFactory.gerarBeneficio(true);
        beneficioDto2 = TestFactory.gerarBeneficio(true);
        transferenciaDto1 = TransferenciaDto.builder()
                .fromId(beneficioDto1.getId())
                .toId(beneficioDto2.getId())
                .valor(new BigDecimal("10.00"))
                .build();
        transferenciaDto2 = TransferenciaDto.builder()
                .fromId(beneficioDto1.getId())
                .toId(beneficioDto2.getId())
                .valor(new BigDecimal("5000.00"))
                .build();
    }

    @Test
    @DisplayName("Deve realizar transferência com dados válidos")
    public void deveRealizarTransferencia_quandoDadosValidos() {

        // Given
        // Executa o método
        service.realizarTransferencia(transferenciaDto1);

        // Verifica o resultado
        then(ejbService)
                .should()
                .transfer(transferenciaDto1.getFromId(), transferenciaDto1.getToId(), transferenciaDto1.getValor());
    }

    @Test
    @DisplayName("Deve gerar IllegalArgumentException ao realizar transferência com identificadores iguais")
    public void deveGerarIllegalArgumentException_quandoTransferenciaComIdentificadoresIguais() {
        // Given
        TransferenciaDto dto = TransferenciaDto.builder()
                .fromId(2L)
                .toId(2L)
                .valor(new BigDecimal(100.00))
                .build();

        // Executa o método
        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> {
            service.realizarTransferencia(dto);
        });

        // Verifica o resultado
        assertEquals(IllegalArgumentException.class, throwable.getClass());
        then(ejbService).should(never()).transfer(dto.getFromId(), dto.getToId(), dto.getValor());
    }

    @Test
    @DisplayName("Deve gerar BusinessException ao realizar transferência com saldo insuficiente")
    public void deveGerarBusinessException_quandoTransferenciaSaldoInsuficiente() {
        // Configura o mock para o serviço EJB

        willThrow(new BusinessException("Saldo insuficiente para transferência."))
                .given(ejbService)
                .transfer(transferenciaDto2.getFromId(), transferenciaDto2.getToId(), transferenciaDto2.getValor());

        // Executa o método
        Throwable throwable = assertThrows(BusinessException.class, () -> {
            service.realizarTransferencia(transferenciaDto2);
        });

        // Verifica o resultado
        assertEquals(BusinessException.class, throwable.getClass());
        then(ejbService)
                .should()
                .transfer(transferenciaDto2.getFromId(), transferenciaDto2.getToId(), transferenciaDto2.getValor());
    }
}
