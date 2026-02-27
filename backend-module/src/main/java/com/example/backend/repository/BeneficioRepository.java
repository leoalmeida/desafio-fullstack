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

    @Query("select p from Beneficio p where p.nome = ?1")
    List<Beneficio> searchByNome(String nome);

}
