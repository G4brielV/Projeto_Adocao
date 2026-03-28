package com.adocao.Projeto_Adocao.Application.Usuario;

import com.adocao.Projeto_Adocao.Application.Auth.CadastroRequest;
import com.adocao.Projeto_Adocao.Application.Auth.CadastroResponse;
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

    public UsuarioMeResponse toUsuarioMeResponse(Usuario usuario) {
        return UsuarioMeResponse.builder()
                .nome(usuario.getNome())
                .cpf(usuario.getCpf())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .saldo(usuario.getSaldo())
                .build();
    }
}