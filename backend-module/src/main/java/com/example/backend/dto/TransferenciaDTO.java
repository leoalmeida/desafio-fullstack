package com.example.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Transferencia", description = "Dados necessários para realizar uma transferência entre benefícios")
public class TransferenciaDTO {
    @Schema(description = "ID do benefício de origem (onde o valor será debitado)", example = "1")
    @NotNull(message = "ID de origem é obrigatório")
    private Long fromId;
    @Schema(description = "ID do benefício de destino (onde o valor será creditado)", example = "2")
    @NotNull(message = "ID de destino é obrigatório")
    private Long toId;
    @Schema(description = "Valor a ser transferido entre benefícios", example = "150.50", minimum = "0.01")
    @NotNull(message = "Valor da transferência é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor de transferência deve ser maior que 0.00")
    private BigDecimal valor;
}
