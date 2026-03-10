package com.adocao.Projeto_Adocao.Infra.Security;

import lombok.Builder;

import java.util.List;

@Builder
public record JWTUserData (Long id, String nome, List<String> roles){
}
