package com.adocao.Projeto_Adocao.Security;

import com.adocao.Projeto_Adocao.Application.Usuario.UserDetails_Service;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenJWTService tokenJWTService;

    @Autowired
    private UserDetails_Service userDetails_service;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var JWTToken = recuperarToken(request);

        // Valida se é nulo
        if (JWTToken != null) {
            var subject = tokenJWTService.getSubject(JWTToken);
            var usuario = userDetails_service.loadUserById(subject); // Pegando o Usuario/Login pelo "id" enviado no subject do Token

            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization"); // Pega o cabeçalho do Token

        // Valida se foi enviado algum Token
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
