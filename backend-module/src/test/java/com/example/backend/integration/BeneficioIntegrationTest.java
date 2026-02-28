package com.example.backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.backend.factory.TestFactory;
import com.example.backend.repository.BeneficioRepository;
import com.example.ejb.entity.Beneficio;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@Transactional
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
        
        // Cria beneficios para testes
        beneficio1 = repository.save(TestFactory.gerarBeneficio());
        beneficio2 = repository.save(TestFactory.gerarBeneficio());
        beneficio3 = repository.save(TestFactory.gerarBeneficio());
        beneficioAssets.addAll(Arrays.asList(beneficio1,beneficio2,beneficio3));
    }

    @Test
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
                .andExpect(jsonPath("$.ativo", is(dto.getAtivo())));
    }

    @Test
    void integradoAoCancelarBeneficio_RetornaBeneficioCancelado() throws Exception {
        mockMvc.perform(put("/api/v1/beneficios/{id}/cancelar", beneficio1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo", is(false)));
    }

    @Test
    void integradoAoAtivarBeneficio_RetornaBeneficioAtivado() throws Exception {
        mockMvc.perform(put("/api/v1/beneficios/{id}/ativar", beneficio1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo", is(true)));
    }

    @Test
    void integradoAoRemoverBeneficio_RetornaNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/beneficios/{id}", beneficio1.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void integradoAoConsultarTodosBeneficios_RetornaListaDeBeneficios() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].nome", is(beneficio1.getNome())))
                .andExpect(jsonPath("$[1].nome", is(beneficio2.getNome())))
                .andExpect(jsonPath("$[2].nome", is(beneficio3.getNome())));
    }
    @Test
    void integradoAoBuscarBeneficioPorId_RetornaBeneficioEncontrado() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios/{id}", beneficio1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(beneficio1.getId())))
                .andExpect(jsonPath("$.nome", is(beneficio1.getNome())))
                .andExpect(jsonPath("$.valor", is(beneficio1.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo", is(beneficio1.getAtivo())));
    }

    @Test
    void integradoAoBuscarBeneficioPorIdInexistente_RetornaStatusNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios/875"))
                .andExpect(status().isNotFound());
    }

    @Test
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
                .andExpect(jsonPath("$.error", is("Não é possível realizar transferirência para o mesmo benefício.")));
    }

    @Test
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
                .andExpect(jsonPath("$.error", is("Saldo insuficiente para transferência.")));
    }
}
