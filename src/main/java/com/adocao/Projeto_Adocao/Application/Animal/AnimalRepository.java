package com.adocao.Projeto_Adocao.Application.Animal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository <Animal, Long> {

    // Para o GET pegar apenas os Animais ativos do usuario logado
    List<Animal> findAllByAtivoTrueAndUsuarioId(Long usuarioId);
}
