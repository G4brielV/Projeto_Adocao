package com.adocao.Projeto_Adocao.Application.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetails_Service implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        return usuarioRepository.findByCpf(cpf);
    }

    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        return usuarioRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o ID: " + id));
    }

}
