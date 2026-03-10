package com.adocao.Projeto_Adocao.Infra.Security;

import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SecurityFilter extends OncePerRequestFilter {


    private final TokenJWTService tokenJWTService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UsuarioService usuarioService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String JWTToken = recuperarToken(request);

        if (JWTToken != null) {
            Optional<JWTUserData> optJwtUserData = tokenJWTService.verifyToken(JWTToken);
            if (optJwtUserData.isPresent()){
                JWTUserData userData = optJwtUserData.get();
                List<SimpleGrantedAuthority> authorities = userData.roles().stream().map(SimpleGrantedAuthority::new).toList();

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userData, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request,response);
        } else { filterChain.doFilter(request, response); }
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization"); // Pega o cabeçalho do Token

        // Valida se foi enviado algum Token
        if (authorizationHeader != null) {
            return authorizationHeader.substring("Bearer ".length());
        }
        return null;
    }
}
