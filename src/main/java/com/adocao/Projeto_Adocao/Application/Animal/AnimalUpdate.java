package com.adocao.Projeto_Adocao.Application.Animal;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record AnimalUpdate(
        @NotBlank
        String nome,

        @NotBlank
        String raca,

        @NotBlank
        String cor,

        @Enumerated(EnumType.STRING)
        Porte porte,

        @Past
        LocalDate nascimento,

        Boolean castracao,

        @NotBlank
        String descricao // Peso, altura, vacinas e vermifugação na descrição
) { }

