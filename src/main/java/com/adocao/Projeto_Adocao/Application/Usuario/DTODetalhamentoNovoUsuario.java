package com.adocao.Projeto_Adocao.Application.Usuario;

public record DTODetalhamentoNovoUsuario(
        Long id,
        String nome,
        String identificador

) {
    public DTODetalhamentoNovoUsuario(Usuario novoUsuario) {
        this(
                novoUsuario.getId(),
                novoUsuario.getNome(),
                novoUsuario.getIdentificador()
        );
    }
}
