package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para a entidade Beneficio.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "Beneficio", description = "Dados de um benefício bancário")
public class BeneficioDto {

    @Schema(description = "Identificador único do benefício", example = "1")
    private Long id;
    @Schema(description = "Nome do benefício", example = "Vale Refeição", minLength = 3, maxLength = 100)
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    @Schema(description = "Descrição detalhada do benefício", example = "Vale refeição fornecido pela empresa", maxLength = 255)
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricao;
    @Schema(description = "Valor do benefício", example = "500.00", minimum = "0.00")
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.00", message = "Valor deve ser maior que 0.00")
    private BigDecimal valor;
    @Schema(description = "Status do benefício (ativo ou cancelado)", example = "true")
    @NotNull(message = "Status é obrigatório")
    private Boolean ativo;

}
