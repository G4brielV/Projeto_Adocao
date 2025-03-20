package com.adocao.Projeto_Adocao.Application.Endereco;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/endereco")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @PostMapping("/cadastro")
    public ResponseEntity<DTODetalhamentoEndereco> cadastrarEndereco(@RequestBody @Valid DTOCadastroEndereco dtoCadastroEndereco,
                                                                     @AuthenticationPrincipal Usuario usuario,
                                                                     UriComponentsBuilder uriComponentsBuilder) {
        return enderecoService.cadastrarEndereco(dtoCadastroEndereco, usuario, uriComponentsBuilder);
    }

    @PutMapping("/editar/{enderecoId}")
    public ResponseEntity<DTODetalhamentoEndereco> editarEndereco(
            @PathVariable Long enderecoId,
            @RequestBody @Valid DTOEditarEndereco dtoEditarEndereco,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriComponentsBuilder){
        return enderecoService.editarEndereco(enderecoId, usuario, dtoEditarEndereco, uriComponentsBuilder);
  }

  // Desativar endereço? ai desativa todos os animais do usuario também

}


