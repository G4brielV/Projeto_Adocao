package com.adocao.Projeto_Adocao.Application.Animal;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AnimalMapper {

    public Animal toAnimal(AnimalRequest animalRequest, Usuario dono){
        return Animal.builder()
                .nome(animalRequest.nome())
                .raca(animalRequest.raca())
                .cor(animalRequest.cor())
                .porte(animalRequest.porte())
                .nascimento(animalRequest.nascimento())
                .castracao(animalRequest.castracao())
                .descricao(animalRequest.descricao())
                .usuario(dono)
                .build();
    }

    public AnimalResponse toAnimalResponse(Animal animal){
        return AnimalResponse.builder()
                .id(animal.getId())
                .nome(animal.getNome())
                .raca(animal.getRaca())
                .cor(animal.getCor())
                .porte(animal.getPorte())
                .nascimento(animal.getNascimento())
                .castracao(animal.getCastracao())
                .build();
    }
}
