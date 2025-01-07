package com.adocao.Projeto_Adocao.Application.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record DTOCadastroUsuario(
        @NotBlank
        String nome,

        @NotBlank
        @ValidacaoDocumento
        String identificador,

        @NotBlank
        @Email(message = "modelo de email inválido")
        String email,

        @NotBlank
        String senha,

        @NotBlank
        String telefone) { }

