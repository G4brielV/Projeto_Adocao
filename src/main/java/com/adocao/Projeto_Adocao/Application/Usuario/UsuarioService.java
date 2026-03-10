package com.adocao.Projeto_Adocao.Application.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UsuarioService{

    private final UsuarioRepository usuarioRepository;

    public Usuario findUserById(Long id) throws UsernameNotFoundException {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o ID: " + id));
    }
}
