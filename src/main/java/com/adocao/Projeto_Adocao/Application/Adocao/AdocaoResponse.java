package com.adocao.Projeto_Adocao.Application.Adocao;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AdocaoResponse(
        Long id,

        Long animalId,
        String nomeAnimal,

        Long donoOrigemId,
        String nomeDonoOrigem,

        Long adotanteId,
        String nomeAdotante,

        StatusAdocao status,
        String mensagem,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
