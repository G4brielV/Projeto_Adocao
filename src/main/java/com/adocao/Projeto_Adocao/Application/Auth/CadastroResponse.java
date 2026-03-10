package com.adocao.Projeto_Adocao.Application.Auth;

import lombok.Builder;

@Builder
public record CadastroResponse(
        Long id,
        String nome,
        String cpf) {

}
