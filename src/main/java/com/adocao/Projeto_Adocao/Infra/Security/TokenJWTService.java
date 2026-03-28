package com.adocao.Projeto_Adocao.Infra.Security;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class TokenJWTService {

    @Value("{$api.security.token.secret}")
    // Direto do application.properties
    private String secret;

    public String gerarToken(Usuario usuario){
        Algorithm algorithm = Algorithm.HMAC256(secret);

        // Converte as SimpleGrantedAuthority em uma lista de Strings
        List<String> roles = usuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JWT.create()
                .withIssuer("Projeto_Adocao")
                .withSubject(usuario.getId().toString())
                .withClaim("userName", usuario.getNome())
                .withClaim("roles", roles)
                .withIssuedAt(Instant.now())
                .withExpiresAt(dataExpiracao())
                .sign(algorithm);
    }


    private Instant dataExpiracao() {
        return LocalDateTime
                .now()
                .plusHours(2)           // Adiciona 2 horas, TEMPO QUE DURARÁ O TOKEN
                .toInstant(ZoneOffset.of("-03:00"));
    }

    // Usado no SecurityFilter, para validar o login do token
    public Optional<JWTUserData> verifyToken(String TokenJWT){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

             DecodedJWT jwt = JWT.require(algorithm)
                    .withIssuer("Projeto_Adocao")
                    .build()
                    .verify(TokenJWT);

             return Optional.of(JWTUserData
                     .builder()
                     .id(Long.valueOf(jwt.getSubject()))
                     .nome(jwt.getClaim("userName").asString())
                     .roles(jwt.getClaim("roles").asList(String.class))
                     .build());

        } catch (JWTVerificationException exception){
            return Optional.empty();
        }
    }
}
