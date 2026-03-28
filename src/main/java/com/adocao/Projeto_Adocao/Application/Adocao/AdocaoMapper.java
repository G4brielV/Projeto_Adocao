package com.adocao.Projeto_Adocao.Application.Adocao;

import com.adocao.Projeto_Adocao.Application.Animal.Animal;
import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AdocaoMapper {

    public Adocao toAdocao(AdocaoRequest adocaoRequest, Usuario usuario, Animal animal){
        return Adocao.builder()
                .animal(animal)
                .donoOrigem(animal.getUsuario())
                .adotante(usuario)
                .status(StatusAdocao.PENDENTE)
                .mensagem(adocaoRequest.mensagem())
                .build();
    }

    public AdocaoResponse toAdocaoResponse(Adocao adocao){
        return AdocaoResponse.builder()
                .id(adocao.getId())
                .animalId(adocao.getAnimal().getId())
                .nomeAnimal(adocao.getAnimal().getNome())
                .donoOrigemId(adocao.getDonoOrigem().getId())
                .nomeDonoOrigem(adocao.getDonoOrigem().getNome())
                .adotanteId(adocao.getAdotante().getId())
                .nomeAdotante(adocao.getAdotante().getNome())
                .status(adocao.getStatus())
                .mensagem(adocao.getMensagem())
                .createdAt(adocao.getCreatedAt())
                .updatedAt(adocao.getUpdatedAt())
                .build();
    }
}
