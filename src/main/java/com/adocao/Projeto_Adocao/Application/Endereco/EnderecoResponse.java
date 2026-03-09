package com.adocao.Projeto_Adocao.Application.Endereco;

import lombok.Builder;

@Builder
public record EnderecoResponse(
        Long id,
        String estado,
        String cidade,
        String rua,
        String numero){
}
