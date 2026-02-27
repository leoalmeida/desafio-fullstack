package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para a entidade Beneficio.
 */
@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

    /**
     * Filtra benefícios por parte do nome usando filtro case-insensitive com LIKE.
     * 
     * @param nome Nome ou parte do nome do benefício a ser pesquisado
     * @return Lista de benefícios que correspondem ao critério de busca
     */
    @Query("SELECT tab FROM Beneficio tab WHERE LOWER(tab.nome) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Beneficio> searchByNome(String nome);

    /**
     * Filtra benefícios pelo status (ativo ou cancelado).
     * 
     * @param status Status do benefício (true = ativo, false = cancelado)
     * @return Lista de benefícios com o status informado
     */
    @Query("select tab from Beneficio tab where tab.ativo = ?1")
    List<Beneficio> searchByStatus(Boolean status);
}
