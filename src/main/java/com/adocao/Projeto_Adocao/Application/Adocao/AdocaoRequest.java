package com.adocao.Projeto_Adocao.Application.Adocao;

import jakarta.validation.constraints.NotNull;

public record AdocaoRequest(

        @NotNull(message = "O ID do animal é obrigatório")
        Long animalId,
        String mensagem

) {}