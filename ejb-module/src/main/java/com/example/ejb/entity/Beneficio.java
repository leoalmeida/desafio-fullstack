package com.example.ejb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.io.Serial;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Entidade que representa um benefício bancário.
 */
@Entity
@Table(name = "BENEFICIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficio {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    @Column(length = 255)
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.00", inclusive = true, message = "Valor deve ser positivo")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "Status é obrigatório")
    @Column(nullable = false)
    private Boolean ativo;

    /*
     * Campo Version usado pelo JPA para o optimistic locking. Sem ele atualizações
     * concorrentes podem se sobrescrever (lost‑update). O Serviço EJB irá também
     * tentar adquirir um "pessimistic lock" quando realizar transferencias para evitar saldos negativos.
     */
    @Version
    private Long version;

    public Beneficio updateData(Beneficio beneficio) {
        this.nome = beneficio.getNome();
        this.descricao = beneficio.getDescricao();
        this.valor = beneficio.getValor();
        this.ativo = beneficio.getAtivo();
        return this;
    }
}
