package com.adocao.Projeto_Adocao.Application.Animal;

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
    List<Animal> findAllByAtivoTrueAndUsuarioId(Long usuarioId);

    // O clearAutomatically limpa o cache do JPA para evitar dados desatualizados na memória
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Animal a SET a.ativo = false WHERE a.usuario.id = :usuarioId")
    void desativarTodosPorUsuarioId(@Param("usuarioId") Long usuarioId);
}
