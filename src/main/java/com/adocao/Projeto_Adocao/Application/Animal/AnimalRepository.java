package com.adocao.Projeto_Adocao.Application.Animal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository <Animal, Long> {

    // Para o GET pegar apenas os Animais ativos do usuario logado
    Page<Animal> findAllByUsuarioId(Long usuarioId, Pageable pageable);

    // 2. Para o endpoint /animais (Vitrine com filtros dinâmicos)
    @Query("SELECT a FROM Animal a WHERE a.ativo = true " +
            "AND (:porte IS NULL OR a.porte = :porte) " +
            "AND (:castracao IS NULL OR a.castracao = :castracao)" +
            "AND (:raca IS NULL OR a.raca= :raca)")
    Page<Animal> findAnimaisDisponiveisComFiltros(
            @Param("porte") Porte porte,
            @Param("castracao") Boolean castracao,
            @Param("raca") String raca,
            Pageable pageable);

    // O clearAutomatically limpa o cache do JPA para evitar dados desatualizados na memória
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Animal a SET a.ativo = false WHERE a.usuario.id = :usuarioId")
    void desativarTodosPorUsuarioId(@Param("usuarioId") Long usuarioId);
}
