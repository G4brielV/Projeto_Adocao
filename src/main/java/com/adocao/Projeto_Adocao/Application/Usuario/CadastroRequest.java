package com.adocao.Projeto_Adocao.Application.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record CadastroRequest(
        @NotBlank
        String nome,

        @NotBlank
        @ValidacaoDocumento
        String cpf,

        @NotBlank
        @Email(message = "modelo de email inválido")
        String email,

        @NotBlank
        String senha,

        @NotBlank
        String telefone) { }

