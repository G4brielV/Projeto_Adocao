package com.adocao.Projeto_Adocao.Security;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private SecurityUtil() {}

    public static Long getIdUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario usuario) {
            return usuario.getId();
        }
        throw new RuntimeException("Usuário não está autenticado ou contexto inválido.");
    }
}