package com.adocao.Projeto_Adocao.Application.Endereco;

import jakarta.validation.constraints.NotBlank;

public record DTOCadastroEndereco(
        @NotBlank
        String rua,

        @NotBlank
        String numero,

        @NotBlank
        String bairro,

        @NotBlank
        String cidade,

        @NotBlank
        String estado
) { }
