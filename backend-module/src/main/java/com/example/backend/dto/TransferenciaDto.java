package com.example.backend.dto;

import java.math.BigDecimal;

import com.example.backend.validator.BeneficioValidator;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "Transferencia", description = "Dados necessários para realizar uma transferência entre benefícios")
public class TransferenciaDto {

    @Schema(description = "ID do benefício de origem (onde o valor será debitado)", 
        example = "1")
    @NotNull(message = "ID de origem é obrigatório")
    private Long fromId;
    @Schema(description = "ID do benefício de destino (onde o valor será creditado)", 
        example = "2")
    @NotNull(message = "ID de destino é obrigatório")
    private Long toId;
    @Schema(description = "Valor a ser transferido entre benefícios", 
        example = "150.50", 
        minimum = BeneficioValidator.MIN_VALOR_TRANSFERENCIA)
    @NotNull(message = "Valor da transferência é obrigatório")
    @DecimalMin(value = BeneficioValidator.MIN_VALOR_TRANSFERENCIA, 
        message = "Valor de transferência deve ser maior que " + BeneficioValidator.MIN_VALOR_TRANSFERENCIA)
    private BigDecimal valor;
}
