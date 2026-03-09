package com.adocao.Projeto_Adocao.Application.Auth;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Infra.Security.TokenJWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenJWTService tokenJWTService;

    public LoginResponse logarUsuario(LoginRequest loginRequest) {
        var token = new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.senha());
        var autenticacao = authenticationManager.authenticate(token);
        var tokenJWT = tokenJWTService.gerarToken((Usuario) autenticacao.getPrincipal());
        return new LoginResponse(tokenJWT);
    }
}
