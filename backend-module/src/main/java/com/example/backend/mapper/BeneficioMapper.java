package com.example.backend.mapper;

import com.example.backend.dto.BeneficioDto;
import com.example.ejb.entity.Beneficio;

import jakarta.validation.constraints.NotNull;

/**
 * Classe Mapper responsável pela conversão entre Entity e DTO de benefícios.
 */
public class BeneficioMapper {

    /**
     * Converte Um BeneficioDTO para Beneficio Entity
     */
    public static Beneficio map(@NotNull BeneficioDto dto, boolean isNewEntity) {
        if (dto == null) {
            return null;
        }
        Beneficio beneficio = Beneficio
                .builder()
                .id(isNewEntity ? null :dto.getId())
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .valor(dto.getValor())
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .version(dto.getVersion())
                .build();
        return beneficio;
    }

    /**
     * Converte Entity para BeneficioDto
     */
    public static BeneficioDto map(@NotNull Beneficio beneficio) {
        if (beneficio == null) {
            return null;
        }
        return BeneficioDto.builder()
                .id(beneficio.getId())
                .nome(beneficio.getNome())
                .descricao(beneficio.getDescricao())
                .valor(beneficio.getValor())
                .ativo(beneficio.getAtivo())
                .version(beneficio.getVersion())
                .build();
    }

    public static Beneficio map(@NotNull Beneficio objDestino, @NotNull BeneficioDto objOrigem) throws IllegalArgumentException {
        if (objOrigem == null) {
            throw new IllegalArgumentException("Objeto de origem não pode ser nulo");
        }
        if (objDestino == null) {
            throw new IllegalArgumentException("Objeto de destino não pode ser nulo");
        }
        return Beneficio.builder()
                .id(objDestino.getId())
                .nome(objOrigem.getNome())
                .descricao(objOrigem.getDescricao())
                .valor(objOrigem.getValor())
                .ativo(objOrigem.getAtivo())
                .version(objDestino.getVersion())
                .build();
    }
}