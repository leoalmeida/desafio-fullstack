package com.example.backend.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.backend.dto.BeneficioRequestDto;
import com.example.backend.dto.BeneficioResponseDto;
import com.example.backend.factory.TestFactory;
import com.example.backend.mapper.BeneficioMapper;
import com.example.backend.service.BeneficioService;
import com.example.backend.util.ObjectsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@WebMvcTest(BeneficioController.class) 
//@AutoConfigureRestDocs(outputDir = "target/snippets")
@ActiveProfiles("test")
@Slf4j
public class BeneficioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BeneficioService beneficioService;

    @Autowired
    private ObjectMapper mapper;

    private BeneficioResponseDto beneficioResponse1;
    private BeneficioResponseDto beneficioResponse2;
    private BeneficioResponseDto beneficioResponseInativo;
    
    @BeforeEach
    public void setUp() {
        
        // Cria beneficios para testes
        beneficioResponse1 = TestFactory.gerarBeneficioResponseDto(true);
        beneficioResponse2 = TestFactory.gerarBeneficioResponseDto(true);
        beneficioResponseInativo = TestFactory.gerarBeneficioResponseDto(false);
    }

    @Test
    @DisplayName("Deve retornar lista com todos os benefícios")
    public void deveRetornarTodosBeneficios() throws Exception {
        // Configura o mock
        given(beneficioService.buscarTodosBeneficios())
            .willReturn(Arrays.asList(beneficioResponse1, beneficioResponseInativo, beneficioResponse2));

        // Executa e verifica
        ResultActions response = mockMvc.perform(get(TestFactory.BENEFICIOS_API_ENDPOINT));

        // Verifica se o método do serviço foi chamado e se a resposta está correta
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", CoreMatchers.is(beneficioResponse1.getId().intValue())))
                .andExpect(jsonPath("$[0].nome", CoreMatchers.is(beneficioResponse1.getNome())))
                .andExpect(jsonPath("$[1].id", CoreMatchers.is(beneficioResponseInativo.getId().intValue())))
                .andExpect(jsonPath("$[1].nome", CoreMatchers.is(beneficioResponseInativo.getNome())))
                .andExpect(jsonPath("$[2].id", CoreMatchers.is(beneficioResponse2.getId().intValue())))
                .andExpect(jsonPath("$[2].nome", CoreMatchers.is(beneficioResponse2.getNome())));

    }

    @Test
    @DisplayName("Deve retornar um benefício a partir do ID")
    public void deveRetornarUmBeneficioAPartirDoId() throws Exception {
		// Configura entidade utilizada
        // Configura o mock
        given(beneficioService.buscarBeneficioPorId(beneficioResponse1.getId()))
            .willReturn(beneficioResponse1);
 
        // Executa e verifica
        ResultActions response = mockMvc.perform(get(TestFactory.BENEFICIOS_API_ENDPOINT+"/{id}",beneficioResponse1.getId()));
        
        // Verifica se o método do serviço foi chamado e se a resposta está correta
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", CoreMatchers.is(beneficioResponse1.getId().intValue())))
                .andExpect(jsonPath("$.nome", CoreMatchers.is(beneficioResponse1.getNome())))
                .andExpect(jsonPath("$.descricao", CoreMatchers.is(beneficioResponse1.getDescricao())))
                .andExpect(jsonPath("$.valor", CoreMatchers.is(beneficioResponse1.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo", CoreMatchers.is(beneficioResponse1.getAtivo())));

	}
   
    @Test
    @DisplayName("Deve ativar um benefício solicitado")
    public void deveAtivarBeneficioSolicitado() throws Exception {
        // Configura entidade utilizada
        beneficioResponseInativo.setAtivo(true);
        // Configura o mock
        given(beneficioService.alterarStatusBeneficio(beneficioResponseInativo.getId(), true))
            .willReturn(beneficioResponseInativo);
        // Executa e verifica
        ResultActions response = mockMvc.perform(put(TestFactory.BENEFICIOS_API_ENDPOINT+"/{id}/ativar",beneficioResponseInativo.getId()));
        // Verifica se o método do serviço foi chamado e se a resposta está correta
        response.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", CoreMatchers.is(beneficioResponseInativo.getId().intValue())))
                .andExpect(jsonPath("$.nome", CoreMatchers.is(beneficioResponseInativo.getNome())))
                .andExpect(jsonPath("$.descricao", CoreMatchers.is(beneficioResponseInativo.getDescricao())))
                .andExpect(jsonPath("$.valor", CoreMatchers.is(beneficioResponseInativo.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo", CoreMatchers.is(beneficioResponseInativo.getAtivo())));

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        then(beneficioService).should(times(1))
                .alterarStatusBeneficio(beneficioResponseInativo.getId(), true);

    }

    @Test
    @DisplayName("Deve cancelar um benefício solicitado")
    public void deveCancelarBeneficioSolicitado() throws Exception {
        // Configura entidade utilizada
        beneficioResponse2.setAtivo(false);
        // Configura o mock
        given(beneficioService.alterarStatusBeneficio(beneficioResponse2.getId(), false))
            .willReturn(beneficioResponse2);

        // Executa e verifica
        ResultActions response = mockMvc.perform(put(TestFactory.BENEFICIOS_API_ENDPOINT+"/{id}/cancelar",beneficioResponse2.getId()));
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", CoreMatchers.is(beneficioResponse2.getId().intValue())))
                .andExpect(jsonPath("$.nome", CoreMatchers.is(beneficioResponse2.getNome())))
                .andExpect(jsonPath("$.descricao", CoreMatchers.is(beneficioResponse2.getDescricao())))
                .andExpect(jsonPath("$.valor", CoreMatchers.is(beneficioResponse2.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo", CoreMatchers.is(beneficioResponse2.getAtivo())));

    }

	@Test
    @DisplayName("Deve remover um benefício solicitado")
    public void deveRemoverBeneficioComIdSolicitado() throws Exception {
		// Configura entidade utilizada
		// Executa e verifica
        ResultActions response = mockMvc.perform(delete(TestFactory.BENEFICIOS_API_ENDPOINT+"/{id}",beneficioResponse1.getId()));
        
        // Verifica se o método do serviço foi chamado e se a resposta está correta
        response.andExpect(status().isNoContent());
	}
    @Test
    @DisplayName("Deve retornar novo benefício criado")
    public void deveRetornarNovoBeneficioCriado() throws Exception {
        // Configura entidade utilizada
        BeneficioRequestDto beneficioRequest1 = BeneficioMapper.mapRequest(beneficioResponse1);

        // Configura o mock
        given(beneficioService.criarBeneficio(ArgumentMatchers.any(BeneficioRequestDto.class)))
            .willReturn(beneficioResponse1);

        // Executa o teste para o comportamento esperado
        ResultActions response = mockMvc.perform(post(TestFactory.BENEFICIOS_API_ENDPOINT)
                            .content(mapper.writeValueAsString(beneficioRequest1))
                            .contentType(MediaType.APPLICATION_JSON));
        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", CoreMatchers.is(beneficioResponse1.getId().intValue())))
                .andExpect(jsonPath("$.nome", CoreMatchers.is(beneficioResponse1.getNome())))
                .andExpect(jsonPath("$.descricao").value(CoreMatchers.is(beneficioResponse1.getDescricao())))
                .andExpect(jsonPath("$.valor").value(CoreMatchers.is(beneficioResponse1.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo").value(CoreMatchers.is(beneficioResponse1.getAtivo())));
    }
    
    @Test
    @DisplayName("Deve alterar um benefício existente")
    public void deveAlterarBeneficioSolicitado() throws Exception {
        // Configura entidade utilizada
        BeneficioRequestDto beneficioRequest = BeneficioMapper.mapRequest(beneficioResponse1);
        beneficioRequest.setNome("Beneficio Alterado");
        beneficioRequest.setDescricao("Descricao Alterado");
        beneficioRequest.setAtivo(!beneficioResponse1.getAtivo());
        beneficioRequest.setValor(new BigDecimal(new Random().nextDouble()).setScale(2, RoundingMode.HALF_UP));
        
		// Configura o mock
        given(beneficioService.alterarBeneficio(beneficioResponse1.getId(), beneficioRequest))
            .willReturn(beneficioResponse1);
        
		// Executa o teste para o comportamento esperado 
        ResultActions response = mockMvc.perform(put(TestFactory.BENEFICIOS_API_ENDPOINT+"/{id}",
                            beneficioResponse1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(beneficioRequest)));

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome",CoreMatchers.is(beneficioResponse1.getNome())))
                .andExpect(jsonPath("$.descricao",CoreMatchers.is(beneficioResponse1.getDescricao())))
                .andExpect(jsonPath("$.valor",CoreMatchers.is(beneficioResponse1.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo",CoreMatchers.is(beneficioResponse1.getAtivo())));
	}
}
