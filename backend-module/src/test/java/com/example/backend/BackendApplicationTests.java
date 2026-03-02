package com.example.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.example.backend.controller.BeneficioController;
import com.example.backend.dto.BeneficioRequestDto;
import com.example.backend.dto.BeneficioResponseDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.backend.factory.AbstractIntegrationTest;
import com.example.backend.factory.TestFactory;
import com.example.backend.repository.BeneficioRepository;
import com.example.ejb.entity.Beneficio;

import lombok.extern.slf4j.Slf4j;


@ContextConfiguration(classes = BackendApplication.class)
@ActiveProfiles("integrado")
@Slf4j
class BackendApplicationTests extends AbstractIntegrationTest{

	@Autowired
	private BeneficioController controller;

	@Autowired
    private BeneficioRepository repository;

	@Autowired
    private WebApplicationContext webApplicationContext;

    private Beneficio beneficio1;
    private Beneficio beneficio2;
    private Beneficio beneficio3;
    private List<Beneficio> beneficioAssets = new ArrayList<Beneficio>();

    @BeforeEach
    public void setUp() {
        startMockMvc(webApplicationContext);
        repository.deleteAll();
        // Cria beneficios para testes
        beneficio1 = repository.save(TestFactory.gerarBeneficio(true));
        beneficio2 = repository.save(TestFactory.gerarBeneficio(true));
        beneficio3 = repository.save(TestFactory.gerarBeneficio(false));
        beneficioAssets.addAll(Arrays.asList(beneficio1,beneficio2,beneficio3));
    }

	@Test
	@DisplayName("Teste de contexto da aplicação carregado com sucesso")
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

    @Test
    @DisplayName("Deve criar um novo benefício válido e retornar o benefício criado")
    void integradoAoCriarNovoBeneficioValido_RetornaBeneficioCriado() throws Exception {
        BeneficioRequestDto dto = TestFactory.gerarBeneficioRequestDto(true);
        BeneficioResponseDto response = performPostRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT, 
                        dto, 
                        BeneficioResponseDto.class,
                        status().isCreated(),
                        jsonPath("$.nome", is(dto.getNome())),
                        jsonPath("$.descricao", is(dto.getDescricao())),
                        jsonPath("$.valor", is(dto.getValor().doubleValue())),
                        jsonPath("$.ativo", is(dto.getAtivo())));
        //then
        assertNotNull(response);
        assertEquals(dto.getNome(), response.getNome());
        assertEquals(dto.getValor(), response.getValor());
    }

	@Test
    @DisplayName("Deve retornar erro ao criar um novo benefício Sem nome e retornar status Bad Request")
    void integradoAoCriarNovoBeneficioInvalido_RetornaErro() throws Exception {
        BeneficioRequestDto dto = TestFactory.gerarBeneficioRequestDto(false);
        dto.setNome(""); 

        performPostRequest(
                TestFactory.BENEFICIOS_API_ENDPOINT, 
                dto, 
                BeneficioResponseDto.class,
                status().is4xxClientError());
    }

    @Test
    @DisplayName("Deve atualizar um benefício válido e retornar o benefício atualizado")
    void integradoAoAtualizarBeneficio_RetornaBeneficioAtualizado() throws Exception {
        BeneficioRequestDto dto = BeneficioRequestDto.builder()
                .nome("Beneficio Atualizado")
                .descricao("Beneficio Atualizado")
                .valor(new BigDecimal("1500.00"))
                .ativo(!beneficio1.getAtivo())
                .build();
                
        BeneficioResponseDto response = performPutRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT + "/{id}", 
                        beneficio1.getId(),
                        dto, 
                        BeneficioResponseDto.class,
                        status().isOk(),
                        jsonPath("$.id", is(beneficio1.getId().intValue())),
                        jsonPath("$.nome", is(dto.getNome())),
                        jsonPath("$.descricao", is(dto.getDescricao())),
                        jsonPath("$.valor", is(dto.getValor().doubleValue())),
                        jsonPath("$.ativo", is(dto.getAtivo())));
        //then
        assertNotNull(response);
    }

    @Test
    @DisplayName("Deve cancelar um benefício e retornar o benefício cancelado")
    void integradoAoCancelarBeneficio_RetornaBeneficioCancelado() throws Exception {
        BeneficioResponseDto response = performPutRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT + "/{id}/cancelar", 
                        beneficio2.getId(),
                        null, 
                        BeneficioResponseDto.class,
                        status().isOk(),
                        jsonPath("$.ativo", is(false)));
        //then
        assertNotNull(response);
    }

    @Test
    @DisplayName("Deve ativar um benefício e retornar o benefício ativado")
    void integradoAoAtivarBeneficio_RetornaBeneficioAtivado() throws Exception {
        BeneficioResponseDto response = performPutRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT + "/{id}/ativar", 
                        beneficio3.getId(),
                        null, 
                        BeneficioResponseDto.class,
                        status().isOk(),
                        jsonPath("$.ativo", is(true)));
        //then
        assertNotNull(response);
    }

    @Test
    @DisplayName("Deve remover um benefício e retornar status No Content")
    void integradoAoRemoverBeneficio_RetornaNoContent() throws Exception {
        performDeleteRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT + "/{id}", 
                        beneficio1.getId(),
                        status().isNoContent());
    }
    
    @Test
    @DisplayName("Deve retornar a lista de todos os benefícios")
    void integradoAoConsultarTodosBeneficios_RetornaListaDeBeneficios() throws Exception {
        List<BeneficioResponseDto> response = performGetAllRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT, 
                        BeneficioResponseDto.class,
                        status().isOk(),
                        jsonPath("$", hasSize(3)),
                        jsonPath("$[0].nome", is(beneficio1.getNome())),
                        jsonPath("$[1].nome", is(beneficio2.getNome())),
                        jsonPath("$[2].nome", is(beneficio3.getNome())));
        //then
        assertNotNull(response);
    }
    
    @Test
    @DisplayName("Deve retornar um benefício pelo ID")
    void integradoAoBuscarBeneficioPorId_RetornaBeneficioEncontrado() throws Exception {
        BeneficioResponseDto response = performGetRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT + "/{id}", 
                        beneficio1.getId(), 
                        BeneficioResponseDto.class,
                        status().isOk(),
                        jsonPath("$.id", is(beneficio1.getId().intValue())),
                        jsonPath("$.nome", is(beneficio1.getNome())),
                        jsonPath("$.valor", is(beneficio1.getValor().doubleValue())),
                        jsonPath("$.ativo", is(beneficio1.getAtivo())));
        //then
        assertNotNull(response);        
    }

    @Test
    @DisplayName("Deve retornar status Not Found ao buscar um benefício inexistente")
    void integradoAoBuscarBeneficioPorIdInexistente_RetornaStatusNotFound() throws Exception {
        BeneficioResponseDto response = performGetRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT + "/{id}", 
                        875L, 
                        BeneficioResponseDto.class,
                        status().isNotFound(),
                        jsonPath("$.error", is("Beneficio não encontrado")));
        //then
        log.error("Response de erro: {}", response.toString());
        //assertNull(response);
    }

    @Test
    @DisplayName("Deve realizar transferência válida entre benefícios e retornar status Ok")
    void integradoAoRealizarTransferenciaValida_RetornaStatusOk() throws Exception {
        TransferenciaDto dto = new TransferenciaDto(
                beneficio1.getId(),
                beneficio2.getId(),
                new BigDecimal("50.00")
        );
        performPostRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT + "/transferir", 
                        dto, 
                        null,
                        status().isOk());
        
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
        performPostRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT + "/transferir", 
                        dto, 
                        null,
                        status().isBadRequest(),
                        jsonPath("$.error", is("Não é possível realizar transferência para o mesmo benefício")));
        
    }

    @Test
    @DisplayName("Deve retornar status Unprocessable Entity ao tentar transferir saldo insuficiente")
    void integradoAoRealizarTransferenciaSaldoInsuficiente_RetornaStatusUnprocessableEntity() throws Exception {
        TransferenciaDto dto = new TransferenciaDto(
                beneficio1.getId(),
                beneficio2.getId(),
                beneficio1.getValor().add(new BigDecimal("50.00")) 
        );

        performPostRequest(
                        TestFactory.BENEFICIOS_API_ENDPOINT + "/transferir", 
                        dto, 
                        null,
                        status().isUnprocessableEntity(),
                        jsonPath("$.error", is("Saldo insuficiente para transferência")));

    }
	
}
