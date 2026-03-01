package com.example.backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.backend.factory.TestFactory;
import com.example.backend.repository.BeneficioRepository;
import com.example.ejb.entity.Beneficio;
import com.fasterxml.jackson.databind.ObjectMapper;

//@WebMvcTest(BeneficioController.class)
//@Transactional
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc 
public class BeneficioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BeneficioRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Beneficio beneficio1;
    private Beneficio beneficio2;
    private Beneficio beneficio3;
    private List<Beneficio> beneficioAssets = new ArrayList<Beneficio>();

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        // Cria beneficios para testes
        beneficio1 = repository.save(TestFactory.gerarBeneficio(true));
        beneficio2 = repository.save(TestFactory.gerarBeneficio(true));
        beneficio3 = repository.save(TestFactory.gerarBeneficio(false));
        beneficioAssets.addAll(Arrays.asList(beneficio1,beneficio2,beneficio3));
    }

    @Test
    @DisplayName("Deve criar um novo benefício válido e retornar o benefício criado")
    void integradoAoCriarNovoBeneficioValido_RetornaBeneficioCriado() throws Exception {
        BeneficioDto dto = TestFactory.gerarBeneficioDto();
        dto.setId(null);

        mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is(dto.getNome())))
                .andExpect(jsonPath("$.descricao", is(dto.getDescricao())))
                .andExpect(jsonPath("$.valor", is(dto.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo", is(dto.getAtivo())));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar um novo benefício inválido")
    void integradoAoCriarNovoBeneficioInvalido_RetornaErro() throws Exception {
        BeneficioDto dto = TestFactory.gerarBeneficioDto();
        dto.setId(null);
        dto.setNome(""); 

        mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve atualizar um benefício válido e retornar o benefício atualizado")
    void integradoAoAtualizarBeneficio_RetornaBeneficioAtualizado() throws Exception {
        BeneficioDto dto = BeneficioDto.builder()
                .id(beneficio1.getId())
                .nome("Beneficio Atualizado")
                .descricao(beneficio1.getDescricao())
                .valor(new BigDecimal("1500.00"))
                .ativo(!beneficio1.getAtivo())
                .build();
                

        mockMvc.perform(put("/api/v1/beneficios/{id}", beneficio1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(dto.getNome())))
                .andExpect(jsonPath("$.descricao", is(dto.getDescricao())))
                .andExpect(jsonPath("$.valor", is(dto.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo", is(dto.getAtivo())))
                .andDo(print());
    }

    @Test
    @DisplayName("Deve cancelar um benefício e retornar o benefício cancelado")
    void integradoAoCancelarBeneficio_RetornaBeneficioCancelado() throws Exception {
        mockMvc.perform(put("/api/v1/beneficios/{id}/cancelar", beneficio1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo", is(false)));
    }

    @Test
    @DisplayName("Deve ativar um benefício e retornar o benefício ativado")
    void integradoAoAtivarBeneficio_RetornaBeneficioAtivado() throws Exception {
        mockMvc.perform(put("/api/v1/beneficios/{id}/ativar", beneficio1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo", is(true)));
    }

    @Test
    @DisplayName("Deve remover um benefício e retornar status No Content")
    void integradoAoRemoverBeneficio_RetornaNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/beneficios/{id}", beneficio1.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar a lista de todos os benefícios")
    void integradoAoConsultarTodosBeneficios_RetornaListaDeBeneficios() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].nome", is(beneficio1.getNome())))
                .andExpect(jsonPath("$[1].nome", is(beneficio2.getNome())))
                .andExpect(jsonPath("$[2].nome", is(beneficio3.getNome())));
    }
    @Test
    @DisplayName("Deve retornar um benefício pelo ID")
    void integradoAoBuscarBeneficioPorId_RetornaBeneficioEncontrado() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios/{id}", beneficio1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(beneficio1.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(beneficio1.getNome())))
                .andExpect(jsonPath("$.valor", is(beneficio1.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo", is(beneficio1.getAtivo())));
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao buscar um benefício inexistente")
    void integradoAoBuscarBeneficioPorIdInexistente_RetornaStatusNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios/875"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Beneficio não encontrado")))
                .andDo(print());
    }

    @Test
    @DisplayName("Deve realizar transferência válida entre benefícios e retornar status Ok")
    void integradoAoRealizarTransferenciaValida_RetornaStatusOk() throws Exception {
        TransferenciaDto dto = new TransferenciaDto(
                beneficio1.getId(),
                beneficio2.getId(),
                new BigDecimal("50.00")
        );

        mockMvc.perform(post("/api/v1/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Verifica se os saldos foram atualizados
        Optional<Beneficio> origemDto = repository.findById(dto.getFromId());
        Optional<Beneficio> destinoDto = repository.findById(dto.getToId());
        assertTrue(origemDto.isPresent());
        assertTrue(destinoDto.isPresent());
        assertEquals(beneficio1.getValor().subtract(new BigDecimal("50.00")), origemDto.get().getValor());
        assertEquals(beneficio2.getValor().add(new BigDecimal("50.00")), destinoDto.get().getValor());
    }
    
    @Test
    @DisplayName("Deve retornar status Bad Request ao tentar transferir para o mesmo benefício")
    void integradoAoRealizarTransferenciaMesmoBeneficio_RetornaStatusBadRequest() throws Exception {
        TransferenciaDto dto = new TransferenciaDto(
                beneficio1.getId(),
                beneficio1.getId(),
                new BigDecimal("50.00") 
        );

        mockMvc.perform(post("/api/v1/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Não é possível realizar transferência para o mesmo benefício")));
    }

    @Test
    @DisplayName("Deve retornar status Unprocessable Entity ao tentar transferir saldo insuficiente")
    void integradoAoRealizarTransferenciaSaldoInsuficiente_RetornaStatusUnprocessableEntity() throws Exception {
        TransferenciaDto dto = new TransferenciaDto(
                beneficio1.getId(),
                beneficio2.getId(),
                beneficio1.getValor().add(new BigDecimal("50.00")) 
        );

        mockMvc.perform(post("/api/v1/beneficios/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error", is("Saldo insuficiente para transferência")));
    }
}
