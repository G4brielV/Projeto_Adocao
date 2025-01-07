package com.adocao.Projeto_Adocao.Application.Usuario;

import com.adocao.Projeto_Adocao.Security.DTOTokenJWT;
import com.adocao.Projeto_Adocao.Security.PasswordEncryptService;
import com.adocao.Projeto_Adocao.Security.Roles.Role;
import com.adocao.Projeto_Adocao.Security.Roles.RoleRepository;
import com.adocao.Projeto_Adocao.Security.TokenJWTService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;

@Service
public class UsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncryptService passwordEncryptService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenJWTService tokenJWTService;


    public ResponseEntity<DTODetalhamentoNovoUsuario> adicionarUsuario(DTOCadastroUsuario dtoCadastroUsuario, UriComponentsBuilder uriComponentsBuilder){

        Usuario novoUsuario = new Usuario(dtoCadastroUsuario);

        String senhaCriptografada = passwordEncryptService.encryptPassword(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);

        // Se for CPF
        Role userRole;
        if (novoUsuario.getIdentificador().length() == 11){
            userRole = roleRepository.findByNome("PESSOA")
                    .orElseThrow(() -> new RuntimeException("Role PESSOA não encontrado"));

            novoUsuario.getRoles().add(userRole);

            novoUsuario.setTipo('P');
        }
        else{
            userRole = roleRepository.findByNome("CRECHE")
                    .orElseThrow(() -> new RuntimeException("Role CRECHE não encontrado"));

            novoUsuario.getRoles().add(userRole);
            novoUsuario.setSaldo(BigInteger.valueOf(0));

            novoUsuario.setTipo('J');
        }
        usuarioRepository.save(novoUsuario);

        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/usuario/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(novoUsuario.getId()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(new DTODetalhamentoNovoUsuario(novoUsuario));
    }

    public ResponseEntity<DTOTokenJWT> logarUsuario(DTOLoginUsuario dadosUsuario, UriComponentsBuilder uriComponentsBuilder) {
        var token = new UsernamePasswordAuthenticationToken(dadosUsuario.login(), dadosUsuario.senha());
        var autenticacao = authenticationManager.authenticate(token);

        var tokenJWT = tokenJWTService.gerarToken((Usuario) autenticacao.getPrincipal());

        return ResponseEntity.ok(new DTOTokenJWT(tokenJWT));
    }

}
