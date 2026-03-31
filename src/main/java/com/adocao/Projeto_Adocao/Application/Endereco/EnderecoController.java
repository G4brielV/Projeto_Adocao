package com.adocao.Projeto_Adocao.Application.Endereco;

import com.adocao.Projeto_Adocao.Application.DTO.StatusRequestDTO;
import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "3. Endereços", description = "Gerenciamento do endereço do usuário")
@RequiredArgsConstructor
@RestController
@RequestMapping("/enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    @Operation(
            summary = "Retorna o endereço do usuário autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    public ResponseEntity<EnderecoResponse> getEndereco(@AuthenticationPrincipal JWTUserData jwtUserData,
                             UriComponentsBuilder uriComponentsBuilder){

        EnderecoResponse enderecoResponse = enderecoService.getEndereco(jwtUserData);
        return ResponseEntity.ok(enderecoResponse);
    }

    @Operation(
            summary = "Cadastra um novo endereço para o usuário autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping()
    public ResponseEntity<EnderecoResponse> cadastrarEndereco(@RequestBody @Valid EnderecoRequest enderecoRequest,
                                                              @AuthenticationPrincipal JWTUserData jwtUserData,
                                                              UriComponentsBuilder uriComponentsBuilder) {

        EnderecoResponse enderecoResponse = enderecoService.cadastrarEndereco(enderecoRequest, jwtUserData);

        var uri = uriComponentsBuilder
                .path("/enderecos/me")
                .build()
                .toUri();

        return ResponseEntity.created(uri).body(enderecoResponse);
    }

    @Operation(
            summary = "Edita o endereço do usuário autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping()
    public ResponseEntity<EnderecoResponse> editarEndereco(@RequestBody @Valid EnderecoUpdate enderecoUpdate,
                                                           @AuthenticationPrincipal JWTUserData jwtUserData,
                                                           UriComponentsBuilder uriComponentsBuilder){

        EnderecoResponse enderecoResponse = enderecoService.editarEndereco(jwtUserData, enderecoUpdate);

        var uri = uriComponentsBuilder
                .path("/enderecos/me")
                .build()
                .toUri();

        return ResponseEntity.created(uri).body(enderecoResponse);
  }

    @Operation(
            summary = "Altera o status do endereço do usuário autenticado (ativo/inativo)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/status")
    public ResponseEntity<EnderecoResponse> alterarStatus (@AuthenticationPrincipal JWTUserData jwtUserData,
                                               @RequestBody StatusRequestDTO request){
        EnderecoResponse response = enderecoService.alterarStatus(jwtUserData, request.status());
        return ResponseEntity.ok().body(response);
    }

}
