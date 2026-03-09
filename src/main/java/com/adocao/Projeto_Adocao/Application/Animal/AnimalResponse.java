package com.adocao.Projeto_Adocao.Application.Animal;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AnimalResponse(
        Long id,
        String nome,
        String raca,
        String cor,
        Porte porte,
        LocalDate nascimento,
        Boolean castracao) {
}
