package com.adocao.Projeto_Adocao.Application.Usuario;

import lombok.Builder;

@Builder
public record CadastroResponse(
        Long id,
        String nome,
        String cpf) {

}
