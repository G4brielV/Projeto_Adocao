package com.adocao.Projeto_Adocao.Application.Usuario;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(
            summary = "Cadastra um novo usuario"
    )
    @PostMapping()
    public ResponseEntity<CadastroResponse> cadastrarUsuario (@RequestBody @Valid CadastroRequest cadastroRequest, UriComponentsBuilder uriComponentsBuilder){
        CadastroResponse response = usuarioService.adicionarUsuario(cadastroRequest);

        var uri = uriComponentsBuilder
                .path("/usuario/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(response.id()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

}
