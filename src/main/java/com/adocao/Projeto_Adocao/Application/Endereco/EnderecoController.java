package com.adocao.Projeto_Adocao.Application.Endereco;

import com.adocao.Projeto_Adocao.Application.DTO.StatusRequestDTO;
import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("/enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    @Operation(
            summary = "Retorna o endereço do Usuario",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    public ResponseEntity<EnderecoResponse> getEndereco(@AuthenticationPrincipal JWTUserData jwtUserData,
                             UriComponentsBuilder uriComponentsBuilder){

        EnderecoResponse enderecoResponse = enderecoService.getEndereco(jwtUserData);

        return ResponseEntity.ok(enderecoResponse);
    }


    @PostMapping()
    public ResponseEntity<EnderecoResponse> cadastrarEndereco(@RequestBody @Valid EnderecoRequest enderecoRequest,
                                                              @AuthenticationPrincipal JWTUserData jwtUserData,
                                                              UriComponentsBuilder uriComponentsBuilder) {

        EnderecoResponse enderecoResponse = enderecoService.cadastrarEndereco(enderecoRequest, jwtUserData);

        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/enderecos/me")  // Caminho do endpoint da class para a API
                .build()
                .toUri();

        return ResponseEntity.created(uri).body(enderecoResponse);
    }

    @PutMapping()
    public ResponseEntity<EnderecoResponse> editarEndereco(@RequestBody @Valid EnderecoUpdate enderecoUpdate,
                                                           @AuthenticationPrincipal JWTUserData jwtUserData,
                                                           UriComponentsBuilder uriComponentsBuilder){

        EnderecoResponse enderecoResponse = enderecoService.editarEndereco(jwtUserData, enderecoUpdate);

        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/enderecos/me")  // Caminho do endpoint da class para a API
                .build()
                .toUri();

        return ResponseEntity.created(uri).body(enderecoResponse);
  }

    /*
    * TODO:
    *  - Desativar endereço? -> Ai desativaria todos os animais do usuario tbm*/


    @PatchMapping("/status")
    public ResponseEntity<EnderecoResponse> alterarStatus (@AuthenticationPrincipal JWTUserData jwtUserData,
                                               @RequestBody StatusRequestDTO request){
        EnderecoResponse response = enderecoService.alterarStatus(jwtUserData, request.status());
        return ResponseEntity.ok().body(response);
    }

}


