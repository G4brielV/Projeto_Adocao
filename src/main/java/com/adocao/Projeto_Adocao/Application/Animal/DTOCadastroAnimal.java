package com.adocao.Projeto_Adocao.Application.Animal;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record DTOCadastroAnimal(
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
