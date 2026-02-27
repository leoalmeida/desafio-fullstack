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

/**
 * DTO para a entidade Beneficio.
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BeneficioDto {

    private Long id;
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricao;
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.00", message = "Valor deve ser maior que 0.00")
    private BigDecimal valor;
    private Boolean ativo;

}
