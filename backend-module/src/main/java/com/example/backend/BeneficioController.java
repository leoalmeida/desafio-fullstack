package com.example.backend;

import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.TransferenciaDTO;
import com.example.backend.service.BeneficioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;



@RestController
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Benefícios", description = "Endpoint de gestão de benefícios bancários")
public class BeneficioController {

    @Autowired
    private BeneficioService beneficioService;

    @GetMapping
    public ResponseEntity<List<BeneficioDto>> buscarTodosBeneficios() {
        List<BeneficioDto> beneficios = beneficioService.buscarTodosBeneficios();
        return ResponseEntity.ok(beneficios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficioDto> buscarBeneficioById(@PathVariable Long id) {
        return ResponseEntity.ok(beneficioService.buscarBeneficioPorId(id));
    }

    @PostMapping
    public ResponseEntity<BeneficioDto> criarBeneficio(@Valid @RequestBody BeneficioDto beneficioDto) {
        BeneficioDto savedBeneficio = beneficioService.criarBeneficio(beneficioDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedBeneficio);
    }

    @PostMapping("/transferir")
    public ResponseEntity<Void> realizarTransferencia(@Valid @RequestBody TransferenciaDTO dto) {
        beneficioService.realizarTransferencia(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficioDto> alterarBeneficio(@PathVariable Long id, @RequestBody BeneficioDto beneficio) {
        return ResponseEntity.ok(beneficioService.alterarBeneficio(id, beneficio));
    }

    @PutMapping("/{id}/ativar")
    public ResponseEntity<BeneficioDto> ativarBeneficio(@PathVariable Long id) {
        return ResponseEntity.ok(beneficioService.alterarStatusBeneficio(id, true));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<BeneficioDto> cancelarBeneficio(@PathVariable Long id) {
        return ResponseEntity.ok(beneficioService.alterarStatusBeneficio(id, false));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerBeneficio(@PathVariable Long id) {
        beneficioService.removerBeneficio(id);
        return ResponseEntity.noContent().build();
    }

}
