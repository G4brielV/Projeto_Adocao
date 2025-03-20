package com.adocao.Projeto_Adocao.Application.Animal;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record DTODetalhamentoAnimal(
        Long id,
        String nome,
        String raca,
        String cor,
        Porte porte,
        LocalDate nascimento,
        Boolean castracao) {
    public DTODetalhamentoAnimal(Animal novoAnimal){
        this(
                novoAnimal.getId(),
                novoAnimal.getNome(),
                novoAnimal.getRaca(),
                novoAnimal.getCor(),
                novoAnimal.getPorte(),
                novoAnimal.getNascimento(),
                novoAnimal.getCastracao()
        );
    }
}
