package com.adocao.Projeto_Adocao.Application.Endereco;

public record DTODetalhamentoEndereco (
        Long id,
        String estado,
        String cidade,
        String rua,
        String numero){
    public DTODetalhamentoEndereco(Endereco novoEndereco)  {
        this(
            novoEndereco.getId(),
            novoEndereco.getEstado(),
            novoEndereco.getCidade(),
            novoEndereco.getRua(),
            novoEndereco.getNumero()
        );
    }
}
