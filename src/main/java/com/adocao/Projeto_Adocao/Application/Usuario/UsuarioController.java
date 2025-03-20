package com.adocao.Projeto_Adocao.Application.Usuario;


import com.adocao.Projeto_Adocao.Security.DTOTokenJWT;
import com.adocao.Projeto_Adocao.Security.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/autenticacao")
public class UsuarioController {


    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @PostMapping("/cadastro")
    public ResponseEntity<DTODetalhamentoNovoUsuario> cadastrarUsuario (@RequestBody @Valid DTOCadastroUsuario novoUsuario, UriComponentsBuilder uriComponentsBuilder){
        return usuarioService.adicionarUsuario(novoUsuario, uriComponentsBuilder);
    }

    // Não entendi pq n posso deixar no UsuarioService
    @PostMapping("/login")
    public ResponseEntity<DTOTokenJWT> loginUsuario(@RequestBody @Valid DTOLoginUsuario dadosUsuario, UriComponentsBuilder uriComponentsBuilder){
        return usuarioService.logarUsuario(dadosUsuario, uriComponentsBuilder);
    }

}
