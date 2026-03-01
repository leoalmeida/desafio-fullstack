package com.example.backend.controller;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.backend.factory.TestFactory;
import com.example.backend.service.BeneficioService;
import com.example.backend.util.ObjectsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import com.example.backend.dto.BeneficioDto;

@WebMvcTest(BeneficioController.class) 
//@AutoConfigureRestDocs(outputDir = "target/snippets")
@Slf4j
public class BeneficioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ObjectsValidator<BeneficioDto> beneficioValidator;

    @MockBean
    private BeneficioService beneficioService;

    @Autowired
    private ObjectMapper mapper;

    private BeneficioDto beneficio1;
    private BeneficioDto beneficioInativo;
    private BeneficioDto beneficioAtivo;
    private List<BeneficioDto> beneficioAssets = new ArrayList<BeneficioDto>();

    @BeforeEach
    public void setUp() {
        
        // Cria beneficios para testes
        beneficio1 = TestFactory.gerarBeneficioDto();
        beneficioInativo = TestFactory.gerarBeneficioDto();
        beneficioInativo.setAtivo(false);
        beneficioAtivo = TestFactory.gerarBeneficioDto();
        beneficioAtivo.setAtivo(true);
        beneficioAssets.addAll(Arrays.asList(beneficio1,beneficioInativo,beneficioAtivo));
    }

    @Test
    @DisplayName("Deve retornar lista com todos os benefícios")
    public void deveRetornarTodosBeneficios() throws Exception {
        // Configura o mock
        
        given(beneficioService.buscarTodosBeneficios())
            .willReturn(Arrays.asList(beneficio1, beneficioInativo, beneficioAtivo));

        // Executa e verifica
        mockMvc.perform(get(TestFactory.BENEFICIOS_API_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(beneficio1.getId().intValue())))
                .andExpect(jsonPath("$[0].nome", is(beneficio1.getNome())))
                .andExpect(jsonPath("$[1].id", is(beneficioInativo.getId().intValue())))
                .andExpect(jsonPath("$[1].nome", is(beneficioInativo.getNome())))
                .andExpect(jsonPath("$[2].id", is(beneficioAtivo.getId().intValue())))
                .andExpect(jsonPath("$[2].nome", is(beneficioAtivo.getNome())));

        // Verifica se o método do serviço foi chamado
        verify(beneficioService, times(1)).buscarTodosBeneficios();
    }

    @Test
    @DisplayName("Deve retornar um benefício a partir do ID")
    public void deveRetornarUmBeneficioAPartirDoId() throws Exception {
		// Configura entidade utilizada
        BeneficioDto beneficioSelecionado = beneficioAssets.get(new Random().nextInt(this.beneficioAssets.size()));
        // Configura o mock
        doReturn(beneficioSelecionado).when(beneficioService).buscarBeneficioPorId(beneficioSelecionado.getId());
		// Executa e verifica
        mockMvc.perform(get(TestFactory.BENEFICIOS_API_ENDPOINT+"/{id}",beneficioSelecionado.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(beneficioSelecionado.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(beneficioSelecionado.getNome())))
                .andExpect(jsonPath("$.descricao", is(beneficioSelecionado.getDescricao())))
                .andExpect(jsonPath("$.valor", is(beneficioSelecionado.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo", is(beneficioSelecionado.getAtivo())));

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(beneficioService, times(1))
            .buscarBeneficioPorId(beneficioSelecionado.getId());
	}
   
    @Test
    @DisplayName("Deve ativar um benefício solicitado")
    public void deveAtivarBeneficioSolicitado() throws Exception {
        // Configura entidade utilizada
        beneficioInativo.setAtivo(true);
        // Configura o mock
        doReturn(beneficioInativo).when(beneficioService)
                .alterarStatusBeneficio(beneficioInativo.getId(), true);
        // Executa e verifica
        mockMvc.perform(put(TestFactory.BENEFICIOS_API_ENDPOINT+"/{id}/ativar",beneficioInativo.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(beneficioInativo.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(beneficioInativo.getNome())))
                .andExpect(jsonPath("$.descricao", is(beneficioInativo.getDescricao())))
                .andExpect(jsonPath("$.valor", is(beneficioInativo.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo", is(beneficioInativo.getAtivo())));

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(beneficioService, times(1))
                .alterarStatusBeneficio(beneficioInativo.getId(), true);

    }

    @Test
    @DisplayName("Deve cancelar um benefício solicitado")
    public void deveCancelarBeneficioSolicitado() throws Exception {
        // Configura entidade utilizada
        beneficioAtivo.setAtivo(false);
        // Configura o mock
        doReturn(beneficioAtivo).when(beneficioService)
                .alterarStatusBeneficio(beneficioAtivo.getId(), false);

        // Executa e verifica
        mockMvc.perform(put(TestFactory.BENEFICIOS_API_ENDPOINT+"/{id}/cancelar",beneficioAtivo.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(beneficioAtivo.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(beneficioAtivo.getNome())))
                .andExpect(jsonPath("$.descricao", is(beneficioAtivo.getDescricao())))
                .andExpect(jsonPath("$.valor", is(beneficioAtivo.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo", is(beneficioAtivo.getAtivo())));

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(beneficioService, times(1))
                .alterarStatusBeneficio(beneficioAtivo.getId(), false);
    }

	@Test
    @DisplayName("Deve remover um benefício solicitado")
    public void deveRemoverBeneficioComIdSolicitado() throws Exception {
		// Configura entidade utilizada
        BeneficioDto beneficioRemovido = beneficioAssets.get(new Random().nextInt(this.beneficioAssets.size()));
        // Configura o mock
        doNothing().when(beneficioService)
            .removerBeneficio(beneficioRemovido.getId());
		// Executa e verifica
        mockMvc.perform(delete(TestFactory.BENEFICIOS_API_ENDPOINT+"/{id}",beneficioRemovido.getId()))
                .andExpect(status().isNoContent());

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(beneficioService, times(1))
            .removerBeneficio(beneficioRemovido.getId());
	}
    /*@Test
    @DisplayName("Deve retornar novo benefício criado")
    public void deveRetornarNovoBeneficioCriado() throws Exception {
        // Configura o mock
        given(beneficioService.criarBeneficio(beneficio1))
            .willReturn(beneficio1);

        // Executa e verifica
        String result = mockMvc.perform(post(TestFactory.BENEFICIOS_API_ENDPOINT)
                            .content(mapper.writeValueAsString(beneficio1))
                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(beneficio1.getId().intValue())))
                .andExpect(jsonPath("$.nome", is(beneficio1.getNome())))
                .andExpect(jsonPath("$.descricao").value(is(beneficio1.getDescricao())))
                .andExpect(jsonPath("$.valor").value(is(beneficio1.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo").value(is(beneficio1.getAtivo())))
                .andReturn()
                .getResponse().getContentAsString();
        log.info("Retorno: {}", result);
        // Verifica se o método do serviço foi chamado
        verify(beneficioService, times(1)).criarBeneficio(beneficio1);
    }

    @Test
    public void deveAlterarBeneficioSolicitado() throws Exception {
		// Configura entidade utilizada
        BeneficioDto beneficioAlterado = beneficioAssets.get(new Random().nextInt(this.beneficioAssets.size()));
        beneficioAlterado.setNome("Beneficio Alterado");
        beneficioAlterado.setDescricao("Descricao Alterado");
        beneficioAlterado.setAtivo(!beneficioAlterado.getAtivo());
        beneficioAlterado.setValor(new BigDecimal(new Random().nextDouble()).setScale(2, RoundingMode.HALF_UP));
        // Configura o mock
        doReturn(beneficioAlterado).when(beneficioService)
                .alterarBeneficio(beneficioAlterado.getId(), beneficioAlterado);
		// Executa e verifica
        mockMvc.perform(put(TestFactory.BENEFICIOS_API_ENDPOINT+"/{id}",beneficioAlterado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(beneficioAlterado)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(is(beneficioAlterado.getId().intValue())))
                .andExpect(jsonPath("$.nome").value(is(beneficioAlterado.getNome())))
                .andExpect(jsonPath("$.descricao").value(is(beneficioAlterado.getDescricao())))
                .andExpect(jsonPath("$.valor").value(is(beneficioAlterado.getValor().doubleValue())))
                .andExpect(jsonPath("$.ativo").value(is(beneficioAlterado.getAtivo())))
                .andDo(print());

        // Verifica se o método do serviço foi chamado com os parâmetros corretos
        verify(beneficioService, times(1))
                .alterarBeneficio(beneficioAlterado.getId(), beneficioAlterado);
	}*/
}
