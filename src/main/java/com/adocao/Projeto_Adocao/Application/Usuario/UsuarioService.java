package com.adocao.Projeto_Adocao.Application.Usuario;

import com.adocao.Projeto_Adocao.Application.Animal.AnimalRepository;
import com.adocao.Projeto_Adocao.Infra.Exception.ResourceNotFoundException;
import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UsuarioService{

    private final UsuarioRepository usuarioRepository;
    private final AnimalRepository animalRepository;

    public UsuarioMeResponse findUserById(Long id) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        return UsuarioMapper.toUsuarioMeResponse(usuario);
    }


    @Transactional
    public void alterarStatus(JWTUserData jwtUserData) {
        Usuario usuario = usuarioRepository.findById(jwtUserData.id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + jwtUserData.id()));
        usuario.alterarStatus(false);
        if (usuario.getEndereco() != null) usuario.getEndereco().alterarStatus(false);
        // Problema de N+1 com For each, com a query direto no banco melhora um pouco
        animalRepository.desativarTodosPorUsuarioId(jwtUserData.id());
        usuarioRepository.save(usuario);

    }
}
