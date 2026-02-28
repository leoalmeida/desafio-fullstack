package com.example.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.example.backend.factory.TestFactory;
import com.example.ejb.entity.Beneficio;

@DataJpaTest
public class BeneficioRepositoryIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(BeneficioRepositoryIntegrationTest.class);

    @Autowired
    private BeneficioRepository repository;

    @Test
    void tabelaNaoDeveEstarVazia() {
        assertTrue(repository.findAll().size()>0);
    }

    @Test
    void aoCriarBeneficio_entaoRegistroPersistidoComSucesso() {
        // given
        Beneficio novoItem = TestFactory.gerarBeneficio();

        // whens
        Beneficio saved = repository.save(Objects.requireNonNull(novoItem));
        logger.info("Benefício criado com ID: {}", saved.getId());
        // then
        Optional<Beneficio> retrieved = repository.findById(Objects.requireNonNull(saved.getId()));
        assertTrue(retrieved.isPresent());
        assertEquals(saved.getId(), retrieved.get().getId());
        assertEquals(saved.getNome(), retrieved.get().getNome());
        assertEquals(saved.getDescricao(), retrieved.get().getDescricao());
        assertEquals(saved.getValor(), retrieved.get().getValor());
        assertEquals(saved.getAtivo(), retrieved.get().getAtivo());
        
        repository.deleteById(Objects.requireNonNull(retrieved.get().getId()));
    }

    @Test
    void aoAlterarBeneficio_entaoQuantidadeRegistrosNaoAltera() {
        // given
        Beneficio beneficio1 = repository.save(Objects.requireNonNull(TestFactory.gerarBeneficio()));
        
        beneficio1.setNome("Beneficio Alterado");
        beneficio1.setDescricao("Descrição Alterada");
        beneficio1.setValor(new BigDecimal("1000.00"));
        beneficio1.setAtivo(!beneficio1.getAtivo());
        Beneficio saved = repository.save(beneficio1);

        // when
        List<Beneficio> retrieved = repository.findAll();
 
        // then
        assertFalse(retrieved.isEmpty());
        assertEquals(3, retrieved.size());

    }

    @Test
    void aoAlterarBeneficio_entaoRegistroAlteradoComSucesso() {
        // given
        Beneficio beneficio1 = repository.save(Objects.requireNonNull(TestFactory.gerarBeneficio()));
        
        beneficio1.setNome("Beneficio Alterado");
        beneficio1.setDescricao("Descrição Alterada");
        beneficio1.setValor(new BigDecimal("1000.00"));
        beneficio1.setAtivo(!beneficio1.getAtivo());
        repository.save(beneficio1);

        // when
        Optional<Beneficio> retrieved = repository.findById(Objects.requireNonNull(beneficio1.getId()));

        // then
        assertTrue(retrieved.isPresent());
        assertEquals(beneficio1.getId(), retrieved.get().getId());
        assertEquals(beneficio1.getNome(), retrieved.get().getNome());
        assertEquals(beneficio1.getDescricao(), retrieved.get().getDescricao());
        assertEquals(beneficio1.getValor(), retrieved.get().getValor());
        assertEquals(beneficio1.getAtivo(), retrieved.get().getAtivo());

        repository.deleteById(Objects.requireNonNull(retrieved.get().getId()));
    }

    @Test
    void aoRemoverBeneficioComIdValido_entaoRegistroRemovidoComSucesso() {
        // given
        Beneficio beneficio1 = repository.save(Objects.requireNonNull(TestFactory.gerarBeneficio()));

        // when
        repository.deleteById(Objects.requireNonNull(beneficio1.getId()));

        // when
        Optional<Beneficio> retrieved = repository.findById(Objects.requireNonNull(beneficio1.getId()));

        // then
        assertFalse(retrieved.isPresent());
    }

    @Test
    void aoRemoverBeneficioComIdInValido_entaoNenhumRegistroRemovido() {
        // given
        repository.deleteAll();
        Beneficio beneficio1 = TestFactory.gerarBeneficio();
        Beneficio beneficio2 = TestFactory.gerarBeneficio();
        Beneficio beneficio3 = TestFactory.gerarBeneficio();
        List<Beneficio> array = new ArrayList<>();
        array.addAll(Arrays.asList(beneficio1,beneficio2,beneficio3));
        repository.saveAll(array);
        // when
        repository.deleteById(1L);
        List<Beneficio> retrieved = repository.findAll();

        // then
        assertFalse(retrieved.isEmpty());
        assertEquals(3, retrieved.size());

        repository.deleteAll();
    }

    @Test
    void aoConsultarBeneficioComIdValido_entaoRetornaRegistroComSucesso() {
        // given
        Beneficio saved = repository.save(Objects.requireNonNull(TestFactory.gerarBeneficio()));

        // when
        Optional<Beneficio> retrieved = repository.findById(Objects.requireNonNull(saved.getId()));

        // then
        assertTrue(retrieved.isPresent());
        assertEquals(saved.getId(), retrieved.get().getId());
        assertEquals(saved.getNome(), retrieved.get().getNome());
        assertEquals(saved.getDescricao(), retrieved.get().getDescricao());
        assertEquals(saved.getValor(), retrieved.get().getValor());
        assertEquals(saved.getAtivo(), retrieved.get().getAtivo());

        repository.deleteById(Objects.requireNonNull(saved.getId()));
    }

    @Test
    void aoConsultarBeneficioComIdInvalido_entaoNenhumRegistroRetornado() {
        // when
        Optional<Beneficio> retrieved = repository.findById(885L);

        // then
        assertFalse(retrieved.isPresent());
    }

    @Test
    void aoConsultarTodosBeneficios_entaoRetornaListaComSucesso() {
        // given
        repository.deleteAll();
        Beneficio beneficio1 = TestFactory.gerarBeneficio();
        Beneficio beneficio2 = TestFactory.gerarBeneficio();
        Beneficio beneficio3 = TestFactory.gerarBeneficio();
        List<Beneficio> array = new ArrayList<>();
        array.addAll(Arrays.asList(beneficio1,beneficio2,beneficio3));
        repository.saveAll(array);

        // when
        List<Beneficio> retrieved = repository.findAll();

        // then
        assertFalse(retrieved.isEmpty());
        assertEquals(3, retrieved.size());

        repository.deleteAll();
    }

    
    @Test
    void comTabelaVaziaAoConsultarTodosBeneficios_entaoRetornaListaVazia() {
        // given
        repository.deleteAll();

        // when
        List<Beneficio> retrieved = repository.findAll();

        // then
        assertTrue(retrieved.isEmpty());

    }

}
