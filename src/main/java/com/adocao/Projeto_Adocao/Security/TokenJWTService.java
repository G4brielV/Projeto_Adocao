package com.adocao.Projeto_Adocao.Security;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenJWTService {

    @Value("{$api.security.token.secret}")
    // Direto do application.properties
    private String secret;

    public String gerarToken(Usuario usuario){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Projeto_Adocao")
                    .withSubject(String.valueOf(usuario.getId()))
                    .withExpiresAt(dataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar Token", exception);
        }
    }


    private Instant dataExpiracao() {
        return LocalDateTime
                .now()
                .plusHours(2)           // Adiciona 2 horas, TEMPO QUE DURARÁ O TOKEN
                .toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubject(String TokenJWT){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("Projeto_Adocao")
                    .build()
                    .verify(TokenJWT)
                    .getSubject();

        } catch (JWTVerificationException exception){
            throw new RuntimeException("Token inválido ou expirado");
        }
    }
}
