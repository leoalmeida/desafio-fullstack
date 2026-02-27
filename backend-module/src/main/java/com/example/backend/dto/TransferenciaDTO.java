package com.example.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransferenciaDTO {
    @NotNull(message = "ID de origem é obrigatório")
    private Long fromId;
    @NotNull(message = "ID de destino é obrigatório")
    private Long toId;
    @NotNull(message = "Valor da transferência é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor de transferência deve ser maior que 0.00")
    private BigDecimal valor;
}
