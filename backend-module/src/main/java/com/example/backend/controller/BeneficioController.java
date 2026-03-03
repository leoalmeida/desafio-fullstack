package com.example.backend.controller;

import com.example.backend.dto.BeneficioRequestDto;
import com.example.backend.dto.BeneficioResponseDto;
import com.example.backend.dto.TransferenciaDto;
import com.example.backend.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = BeneficioController.MAX_AGE)
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Benefícios", description = "Endpoint de gestão de benefícios bancários")
@Slf4j
public class BeneficioController {
    public static final long MAX_AGE = 3600L;

    private final BeneficioService beneficioService;

    public BeneficioController(final BeneficioService service) {
        this.beneficioService = Objects.requireNonNull(service, "BeneficioService não pode ser nulo");
    }

    @Operation(
            summary = "Listar todos os benefícios",
            description = "Retorna uma lista com todos os benefícios cadastrados no sistema")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista de benefícios retornada com sucesso",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = List.class))),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BeneficioResponseDto>> buscarTodosBeneficios() {
        List<BeneficioResponseDto> beneficios = beneficioService.buscarTodosBeneficios();
        return ResponseEntity.ok(beneficios);
    }

    @Operation(
            summary = "Buscar benefício por ID",
            description = "Retorna um benefício específico baseado no ID fornecido")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Benefício encontrado e retornado com sucesso",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = BeneficioResponseDto.class))),
                @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            })
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BeneficioResponseDto> buscarBeneficioById(
            @Parameter(description = "ID único do benefício", required = true, example = "1") @PathVariable
                    final Long id) {
        return ResponseEntity.ok(beneficioService.buscarBeneficioPorId(id));
    }

    @Operation(
            summary = "Criar novo benefício",
            description = "Cria um novo benefício no sistema com os dados fornecidos")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Benefício criado com sucesso",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = BeneficioResponseDto.class))),
                @ApiResponse(responseCode = "400", description = "Dados inválidos ou incompletos"),
                @ApiResponse(responseCode = "422", description = "Erro de negócio ao criar benefício"),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            })
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BeneficioResponseDto> criarBeneficio(
            @Parameter(description = "Dados do benefício a ser criado", required = true) @Valid @RequestBody
                    final BeneficioRequestDto beneficioDto) {
        BeneficioResponseDto savedBeneficio = beneficioService.criarBeneficio(beneficioDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedBeneficio);
    }

    @Operation(
            summary = "Realizar transferência entre benefícios",
            description = "Transfere um valor de um benefício para outro, com validação de saldo e status")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
                @ApiResponse(responseCode = "400", description = "Dados de transferência inválidos"),
                @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
                @ApiResponse(responseCode = "422", description = "Erro de negócio ao realizar transferência"),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            })
    @PostMapping(path = "/transferir")
    public ResponseEntity<Void> realizarTransferencia(
            @Parameter(description = "Dados da transferência (fromId, toId, valor)", required = true)
                    @Valid
                    @RequestBody
                    final TransferenciaDto dto) {
        beneficioService.realizarTransferencia(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Atualizar benefício", description = "Atualiza os dados de um benefício existente")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Benefício atualizado com sucesso",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = BeneficioResponseDto.class))),
                @ApiResponse(responseCode = "400", description = "Dados inválidos ou incompletos"),
                @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
                @ApiResponse(responseCode = "422", description = "Erro de negócio ao atualizar benefício"),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            })
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BeneficioResponseDto> alterarBeneficio(
            @Parameter(description = "ID único do benefício a atualizar", required = true, example = "1") @PathVariable
                    final Long id,
            @Parameter(description = "Novos dados do benefício", required = true) @Valid @RequestBody
                    final BeneficioRequestDto beneficio) {
        BeneficioResponseDto updBeneficio = beneficioService.alterarBeneficio(id, beneficio);
        log.info("Benefício atualizado: {}", updBeneficio);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updBeneficio);
    }

    @Operation(summary = "Ativar benefício", description = "Ativa um benefício que estava cancelado")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Benefício ativado com sucesso",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = BeneficioResponseDto.class))),
                @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
                @ApiResponse(responseCode = "422", description = "Erro de negócio ao ativar benefício"),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            })
    @PutMapping(path = "/{id}/ativar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BeneficioResponseDto> ativarBeneficio(
            @Parameter(description = "ID único do benefício a ativar", required = true, example = "1") @PathVariable
                    final Long id) {
        BeneficioResponseDto updBeneficio = beneficioService.alterarStatusBeneficio(id, true);
        return new ResponseEntity<>(updBeneficio, HttpStatus.OK);
    }

    @Operation(
            summary = "Cancelar benefício",
            description = "Cancela um benefício ativo, impedindo operações futuras com ele")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Benefício cancelado com sucesso",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = BeneficioResponseDto.class))),
                @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
                @ApiResponse(responseCode = "422", description = "Erro de negócio ao cancelar benefício"),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            })
    @PutMapping(path = "/{id}/cancelar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BeneficioResponseDto> cancelarBeneficio(
            @Parameter(description = "ID único do benefício a cancelar", required = true, example = "1") @PathVariable
                    final Long id) {
        BeneficioResponseDto updBeneficio = beneficioService.alterarStatusBeneficio(id, false);
        return new ResponseEntity<>(updBeneficio, HttpStatus.OK);
    }

    @Operation(summary = "Remover benefício", description = "Remove permanentemente um benefício do sistema")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Benefício removido com sucesso"),
                @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
                @ApiResponse(responseCode = "422", description = "Erro de negócio ao remover benefício"),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerBeneficio(
            @Parameter(description = "ID único do benefício a remover", required = true, example = "1") @PathVariable
                    final Long id) {
        beneficioService.removerBeneficio(id);
        return ResponseEntity.noContent().build();
    }
}
