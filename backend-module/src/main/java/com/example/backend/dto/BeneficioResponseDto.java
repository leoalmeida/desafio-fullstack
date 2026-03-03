package com.example.backend.dto;

import com.example.backend.validator.BeneficioValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO para a entidade Beneficio.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "BeneficioResponse", description = "Dados de um benefício bancário")
@ToString
public class BeneficioResponseDto {

    @Schema(description = "Identificador único do benefício", example = "1")
    private Long id;

    @Schema(
            description = "Nome do benefício",
            example = "Vale Refeição",
            maxLength = BeneficioValidator.NOME_MAX_LENGTH)
    @Size(
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
    @DecimalMin(
            value = BeneficioValidator.MIN_VALOR_BENEFICIO,
            message = "Valor deve ser maior que " + BeneficioValidator.MIN_VALOR_BENEFICIO)
    private BigDecimal valor;

    @Schema(description = "Status do benefício (ativo ou cancelado)", example = "true")
    private Boolean ativo;

    @Schema(description = "Versão do benefício", example = "1")
    private Long version;
}
