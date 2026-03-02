package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.TransferenciaDto;
import com.example.backend.service.BeneficioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:3001", maxAge = BeneficioController.MAX_AGE)
@RequestMapping("/api/v1/transferencias")
@Tag(name = "Transferências", description = "Endpoint de gestão de transferências entre benefícios")
public class TransferenciaController {
    public static final long MAX_AGE = 3600L;

    private final BeneficioService beneficioService;

    public TransferenciaController(final BeneficioService service) {
            this.beneficioService = service;
    }

    @Operation(summary = "Realizar transferência entre benefícios", 
            description = "Transfere um valor de um benefício para outro, com validação de saldo e status")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados de transferência inválidos"),
                    @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
                    @ApiResponse(responseCode = "422", description = "Erro de negócio ao realizar transferência"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> realizarTransferencia(
                    @Parameter(description = "Dados da transferência", required = true)
                    @Valid @RequestBody final TransferenciaDto dto) {
        beneficioService.realizarTransferencia(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
