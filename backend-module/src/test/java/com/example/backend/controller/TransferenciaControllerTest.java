package com.example.backend.controller;

import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.backend.dto.BeneficioResponseDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.backend.factory.TestFactory;
import com.example.backend.service.BeneficioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@WebMvcTest(TransferenciaController.class) 
//@AutoConfigureRestDocs(outputDir = "target/snippets")
@ActiveProfiles("test")
@Slf4j
public class TransferenciaControllerTest {
    
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
    @DisplayName("Deve realizar transferência com sucesso entre benefícios ativos e com saldo suficiente")
    public void testRealizarTransferencia_Sucesso() throws Exception {
        // Cenário
        Long origemId = beneficioResponse1.getId();
        Long destinoId = beneficioResponse2.getId();
        BigDecimal valorTransferencia = new BigDecimal("100.00");
        TransferenciaDto dto = new TransferenciaDto(origemId, destinoId, valorTransferencia);
        // Executa e verifica
        ResultActions response = mockMvc.perform(
                post(TestFactory.TRANSFERENCIAS_API_ENDPOINT)
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON));

        // Verifica se o método do serviço foi chamado e se a resposta está correta
        response.andExpect(status().isCreated());
        then(beneficioService).should(times(1)).realizarTransferencia(any());
    }
}
