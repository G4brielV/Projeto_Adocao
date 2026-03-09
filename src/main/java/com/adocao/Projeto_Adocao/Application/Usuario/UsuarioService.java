package com.adocao.Projeto_Adocao.Application.Usuario;

import com.adocao.Projeto_Adocao.Infra.Security.PasswordEncryptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UsuarioService{


    private final UsuarioRepository usuarioRepository;
    private final PasswordEncryptService passwordEncryptService;

    @Transactional
    public CadastroResponse adicionarUsuario(CadastroRequest cadastroRequest){
        Usuario usuario = UsuarioMapper.toUsuario(cadastroRequest);
        String senhaCriptografada = passwordEncryptService.encryptPassword(usuario.getSenha());
        usuario.atualizarSenha(senhaCriptografada);
        usuarioRepository.save(usuario);
        return UsuarioMapper.toLoginResponse(usuario);
    }
}
