package com.adocao.Projeto_Adocao.Application.Endereco;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnderecoMapper {

    public Endereco toEndereco(EnderecoRequest enderecoRequest) {
        return Endereco.builder()
                .rua(enderecoRequest.rua())
                .numero(enderecoRequest.numero())
                .bairro(enderecoRequest.bairro())
                .cidade(enderecoRequest.cidade())
                .estado(enderecoRequest.estado())
                .build();
    }

    public EnderecoResponse toEnderecoResponse(Endereco endereco) {
        return EnderecoResponse.builder()
                .estado(endereco.getEstado())
                .cidade(endereco.getCidade())
                .rua(endereco.getRua())
                .numero(endereco.getNumero())
                .build();
    }
}
