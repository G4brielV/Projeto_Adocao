package com.adocao.Projeto_Adocao.Application.Endereco;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("/endereco")
public class EnderecoController {

    private final EnderecoService enderecoService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello User";
    }

    @PostMapping("/cadastro")
    public ResponseEntity<EnderecoResponse> cadastrarEndereco(@RequestBody @Valid EnderecoRequest enderecoRequest,
                                                              @AuthenticationPrincipal Usuario usuario,
                                                              UriComponentsBuilder uriComponentsBuilder) {

        EnderecoResponse enderecoResponse = enderecoService.cadastrarEndereco(enderecoRequest, usuario);

        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/endereco/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(enderecoResponse.id()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(enderecoResponse);
    }

    @PutMapping("/editar/{enderecoId}")
    public ResponseEntity<EnderecoResponse> editarEndereco(
            @PathVariable Long enderecoId,
            @RequestBody @Valid EnderecoUpdate enderecoUpdate,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriComponentsBuilder){

        EnderecoResponse enderecoResponse = enderecoService.editarEndereco(enderecoId, usuario, enderecoUpdate);

        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/usuario/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(enderecoResponse.id()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(enderecoResponse);
  }

    /*
    * TODO:
    *  - Desativar endereço? -> Ai desativaria todos os animais do usuario tbm*/

}


