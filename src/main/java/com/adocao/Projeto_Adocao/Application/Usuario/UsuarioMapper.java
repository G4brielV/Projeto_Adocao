package com.adocao.Projeto_Adocao.Application.Usuario;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UsuarioMapper {
    public Usuario toUsuario(CadastroRequest dados) {
        return Usuario.builder()
                .nome(dados.nome())
                .cpf(dados.cpf())
                .email(dados.email())
                .senha(dados.senha())
                .telefone(dados.telefone())
                .build();
    }


    public CadastroResponse toLoginResponse(Usuario usuario) {
        return CadastroResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .cpf(usuario.getCpf())
                .build();
    }
}