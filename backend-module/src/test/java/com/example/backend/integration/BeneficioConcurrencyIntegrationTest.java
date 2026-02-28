package com.example.backend.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.backend.repository.BeneficioRepository;
import com.example.backend.service.BeneficioService;
import com.example.backend.util.ObjectsValidator;
import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.ejb.BeneficioEjbService;
import com.example.ejb.entity.Beneficio;
import com.example.ejb.BusinessException;

import jakarta.persistence.EntityManager;

@DataJpaTest
public class BeneficioConcurrencyIntegrationTest {

    
    @Autowired
    private BeneficioRepository repository;

    @Mock
    private BeneficioEjbService ejbService;

    @Mock
    private ObjectsValidator<BeneficioDto> validator;

    @Mock
    private ModelMapper mapper;
    
    @InjectMocks
    private BeneficioService service;

    private Beneficio fonte;
    private Beneficio destino;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        fonte = Beneficio.builder()
                .nome("FonteConcur")
                .descricao("Origem")
                .valor(new BigDecimal("100.00"))
                .ativo(true)
                .build();
        destino = Beneficio.builder()
                .nome("DestinoConcur")
                .descricao("Alvo")
                .valor(new BigDecimal("0.00"))
                .ativo(true)
                .build();
        fonte = repository.save(fonte);
        destino = repository.save(destino);
    }

    @Test
    void validaConcorrencia_eLockingPrevineSaldoNegativo() throws Exception {
        int threads = 2;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger failures = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                ready.countDown();
                start.await();
                TransferenciaDto dto = new TransferenciaDto(fonte.getId(), destino.getId(), new BigDecimal("60.00"));
                service.realizarTransferencia(dto);
            } catch (BusinessException e) {
                failures.incrementAndGet();
            } catch (Exception e) {
                failures.incrementAndGet();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();

        // wait threads ready and release simultaneously
        ready.await();
        start.countDown();

        t1.join();
        t2.join();

        // Ao tentar multiplas transferências em paralelo, confirmar que a soma total de saldos 
        //  está sendo preservada garantindo que apenas uma transferência seja bem sucedida e
        //  a outra falhe devido a saldo insuficiente, confirmando o funcionamento do locking pessimista.
        Beneficio fonteAtual = repository.findById(fonte.getId()).orElseThrow();
        Beneficio destinoAtual = repository.findById(destino.getId()).orElseThrow();

        BigDecimal soma = fonteAtual.getValor().add(destinoAtual.getValor());
        assertEquals(new BigDecimal("100.00"), soma);
        assertTrue(fonteAtual.getValor().compareTo(BigDecimal.ZERO) >= 0);
        // expecting one of the transfers to fail (saldo insuficiente)
        assertEquals(1, failures.get());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public BeneficioEjbService beneficioEjbService(EntityManager em) {
            BeneficioEjbService s = new BeneficioEjbService(em);
            return s;
        }
    }
}
