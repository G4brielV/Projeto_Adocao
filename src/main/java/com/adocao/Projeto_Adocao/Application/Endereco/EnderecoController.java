package com.adocao.Projeto_Adocao.Application.Endereco;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<DTODetalhamentoEndereco> cadastrarEndereco(@RequestBody @Valid DTOCadastroEndereco dtoCadastroEndereco, UriComponentsBuilder uriComponentsBuilder) {
        return enderecoService.cadastrarEndereco(dtoCadastroEndereco, uriComponentsBuilder);
    }

    @PutMapping("/{enderecoId}/editar")
    public ResponseEntity<DTODetalhamentoEndereco> editarEndereco(
            @PathVariable Long enderecoId,
            @RequestBody @Valid DTOAlterarEndereco dtoAlterarEndereco, UriComponentsBuilder uriComponentsBuilder){
        return enderecoService.editarEndereco(enderecoId, dtoAlterarEndereco, uriComponentsBuilder);
  }
}


