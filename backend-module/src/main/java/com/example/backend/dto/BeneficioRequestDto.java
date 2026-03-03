package com.example.backend.dto;

import com.example.backend.validator.BeneficioValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para a entidade Beneficio.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(
        name = "BeneficioRequest",
        description = "Dados de um Request para criação ou alteração de um benefício bancário")
public class BeneficioRequestDto {

    @Schema(
            description = "Nome do benefício",
            example = "Vale Refeição",
            minLength = BeneficioValidator.NOME_MIN_LENGTH,
            maxLength = BeneficioValidator.NOME_MAX_LENGTH)
    @NotBlank(message = "Nome é obrigatório")
    @Size(
            min = BeneficioValidator.NOME_MIN_LENGTH,
            max = BeneficioValidator.NOME_MAX_LENGTH,
            message = "Nome deve ter entre " + BeneficioValidator.NOME_MIN_LENGTH + " e "
                    + BeneficioValidator.NOME_MAX_LENGTH + " caracteres")
    private String nome;

    @Schema(
            description = "Descrição detalhada do benefício",
            example = "Vale refeição fornecido pela empresa",
            maxLength = BeneficioValidator.DESCRICAO_MAX_LENGTH)
    @Size(
            max = BeneficioValidator.DESCRICAO_MAX_LENGTH,
            message = "Descrição deve ter no máximo " + BeneficioValidator.DESCRICAO_MAX_LENGTH + " caracteres")
    private String descricao;

    @Schema(description = "Valor do benefício", example = "500.00", minimum = BeneficioValidator.MIN_VALOR_BENEFICIO)
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(
            value = BeneficioValidator.MIN_VALOR_BENEFICIO,
            message = "Valor deve ser maior que " + BeneficioValidator.MIN_VALOR_BENEFICIO)
    private BigDecimal valor;

    @Schema(description = "Status do benefício (ativo ou cancelado)", example = "true")
    @NotNull(message = "Status é obrigatório")
    private Boolean ativo;
}
