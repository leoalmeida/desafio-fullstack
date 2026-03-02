package com.example.backend.mapper;

import com.example.backend.dto.BeneficioRequestDto;
import com.example.backend.dto.BeneficioResponseDto;
import com.example.ejb.entity.Beneficio;

import jakarta.validation.constraints.NotNull;

/**
 * Classe Mapper responsável pela conversão entre Entity e DTO de benefícios.
 */
public class BeneficioMapper {

    /**
     * Converte Um BeneficioRequestDto para Beneficio Entity
     * @param dto DTO de requisição contendo os dados do benefício a ser criado ou atualizado
     * @return Beneficio Entity correspondente aos dados do DTO, ou null se o DTO for null
     */
    public static Beneficio mapRequest(@NotNull BeneficioRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Beneficio beneficio = Beneficio
                .builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .valor(dto.getValor())
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .build();
        return beneficio;
    }

    /**
     * Converte Entity para BeneficioRequestDto
     * @param beneficio Entity de benefício a ser convertida para DTO de requisição
     * @return BeneficioRequestDto correspondente aos dados da Entity, ou null se a Entity for null
     */
    public static BeneficioRequestDto mapRequest(@NotNull Beneficio beneficio) {
        if (beneficio == null) {
            return null;
        }
        return BeneficioRequestDto.builder()
                .nome(beneficio.getNome())
                .descricao(beneficio.getDescricao())
                .valor(beneficio.getValor())
                .ativo(beneficio.getAtivo())
                .build();
    }

    /**
     * Converte BeneficionResponseDto para BeneficioRequestDto
     * @param beneficioResponse DTO de resposta contendo os dados do benefício a ser convertido para DTO de requisição
     * @return BeneficioRequestDto correspondente aos dados da Entity, ou null se a Entity for null
     */
    public static BeneficioRequestDto mapRequest(@NotNull BeneficioResponseDto beneficioResponse) {
        if (beneficioResponse == null) {
            return null;
        }
        return BeneficioRequestDto.builder()
                .nome(beneficioResponse.getNome())
                .descricao(beneficioResponse.getDescricao())
                .valor(beneficioResponse.getValor())
                .ativo(beneficioResponse.getAtivo())
                .build();
    }

    /**
     * Converte Um BeneficioResponseDto para Beneficio Entity
     * @param dto DTO de resposta contendo os dados do benefício
     * @param isNewEntity Indica se a entidade é nova (true para criação, false para atualização)
     * @return Beneficio Entity correspondente aos dados do DTO, ou null se o DTO for null
     */
    public static Beneficio mapResponse(@NotNull BeneficioResponseDto dto, boolean isNewEntity) {
        if (dto == null) {
            return null;
        }
        Beneficio beneficio = Beneficio
                .builder()
                .id((isNewEntity) ? null : dto.getId())
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .valor(dto.getValor())
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .version((isNewEntity) ? null :dto.getVersion())
                .build();
        return beneficio;
    }

    /**
     * Converte Entity para BeneficioResponseDto
     * @param beneficio Entity de benefício a ser convertida para DTO de resposta
     * @return BeneficioResponseDto correspondente aos dados da Entity, ou null se a Entity for null
     */
    public static BeneficioResponseDto mapResponse(@NotNull Beneficio beneficio) {
        if (beneficio == null) {
            return null;
        }
        return BeneficioResponseDto.builder()
                .id(beneficio.getId())
                .nome(beneficio.getNome())
                .descricao(beneficio.getDescricao())
                .valor(beneficio.getValor())
                .ativo(beneficio.getAtivo())
                .version(beneficio.getVersion())
                .build();
    }

    /**
     * Faz o merge de um dto de request para um Entity existente, atualizando 
     * apenas os campos que foram modificados
     * @param objDestino Entity de benefício existente a ser atualizado
     * @param objOrigem DTO de requisição contendo os novos dados do benefício
     * @return Entity de benefício atualizado com os dados do DTO, ou null se o DTO for null
     * @throws IllegalArgumentException se o DTO de origem ou a Entity de destino forem nulos  
     */
    public static Beneficio map(@NotNull Beneficio objDestino, @NotNull BeneficioRequestDto objOrigem) throws IllegalArgumentException {
        if (objOrigem == null) {
            throw new IllegalArgumentException("Objeto de origem não pode ser nulo");
        }
        if (objDestino == null) {
            throw new IllegalArgumentException("Objeto de destino não pode ser nulo");
        }
        objDestino.setNome(objOrigem.getNome());
        objDestino.setDescricao(objOrigem.getDescricao());
        objDestino.setValor(objOrigem.getValor());
        objDestino.setAtivo(objOrigem.getAtivo());
        return objDestino;
    }
  
}