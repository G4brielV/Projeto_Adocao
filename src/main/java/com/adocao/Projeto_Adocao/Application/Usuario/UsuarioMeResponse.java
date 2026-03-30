package com.adocao.Projeto_Adocao.Application.Usuario;

import lombok.Builder;

import java.math.BigInteger;

@Builder
public record UsuarioMeResponse(
        String nome,
        String cpf,
        String email,
        String telefone
) {
}
