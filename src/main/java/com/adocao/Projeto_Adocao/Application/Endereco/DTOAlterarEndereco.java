package com.adocao.Projeto_Adocao.Application.Endereco;

import jakarta.validation.constraints.NotBlank;

public record DTOAlterarEndereco(
        String rua,

        String numero,

        String bairro,

        String cidade,

        String estado
) { }