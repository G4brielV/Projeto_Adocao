package com.adocao.Projeto_Adocao.Application.Adocao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdocaoRepository extends JpaRepository<Adocao, Long> {

    Boolean existsByAnimalIdAndAdotanteIdAndStatus(Long animal_id, Long usuario_id, StatusAdocao statusAdocao);

    @Modifying
    @Query("UPDATE Adocao a SET a.status = 'REJEITADA' WHERE a.animal.id = :animalId AND a.id != :adocaoAprovadaId AND a.status = 'PENDENTE'")
    void rejeitarOutrasAdocoesPendentesDoAnimal(Long animalId, Long adocaoAprovadaId);

    Page<Adocao> findByAdotanteId(Long adotanteId, Pageable pageable);

    Page<Adocao> findByDonoOrigemId(Long donoOrigemId, Pageable pageable);

    Page<Adocao> findByAnimalId(Long animalId, Pageable pageable);

    boolean existsByAnimalIdAndStatus(Long animalId, StatusAdocao status);
}
