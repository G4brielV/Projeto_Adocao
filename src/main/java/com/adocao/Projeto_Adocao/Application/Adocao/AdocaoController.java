package com.adocao.Projeto_Adocao.Application.Adocao;

import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequiredArgsConstructor
@RequestMapping("/adocoes")
public class AdocaoController {

    private final AdocaoService adocaoService;

    @Operation(
            summary = "Solicita uma adoção de animal",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping()
    public ResponseEntity<AdocaoResponse> solicitarAdocao(@RequestBody @Valid AdocaoRequest adocaoRequest,
                                         @AuthenticationPrincipal JWTUserData jwtUserData,
                                         UriComponentsBuilder uriComponentsBuilder){

        AdocaoResponse response = adocaoService.solicitarAdocao(jwtUserData, adocaoRequest);
        var uri = uriComponentsBuilder
                .path("/adocoes/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @Operation(
            summary = "Lista as solicitações de adoção feitas pelo usuário autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/minhas-solicitacoes")
    public ResponseEntity<Page<AdocaoResponse>> listarMinhasSolicitacoes(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdocaoResponse> response = adocaoService.listarMinhasSolicitacoes(jwtUserData, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Lista as solicitações de adoção recebidas para os animais do usuário autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/meus-animais")
    public ResponseEntity<Page<AdocaoResponse>> listarSolicitacoesRecebidas(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdocaoResponse> response = adocaoService.listarSolicitacoesRecebidas(jwtUserData, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Lista as solicitações de adoção para um animal específico do usuário autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<Page<AdocaoResponse>> listarSolicitacoesPorAnimal(
            @RequestParam Long animalId,
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdocaoResponse> response = adocaoService.listarSolicitacoesPorAnimal(jwtUserData, animalId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Busca detalhes de uma solicitação de adoção",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{adocaoId}")
    public ResponseEntity<AdocaoResponse> buscarDetalhesAdocao(
            @PathVariable Long adocaoId,
            @AuthenticationPrincipal JWTUserData jwtUserData) {

        AdocaoResponse response = adocaoService.buscarDetalhesAdocao(jwtUserData, adocaoId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Aprova uma solicitação de adoção para o dono do animal",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{adocao_id}/aprovar")
    public ResponseEntity<AdocaoResponse> aprovarAdocao(@PathVariable Long adocao_id,
                                                        @AuthenticationPrincipal JWTUserData jwtUserData){
        AdocaoResponse response = adocaoService.aprovarAdocao(jwtUserData, adocao_id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Rejeita uma solicitação de adoção para o dono do animal",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{adocao_id}/rejeitar")
    public ResponseEntity<AdocaoResponse> rejeitarAdocao(@PathVariable Long adocao_id,
                                                         @AuthenticationPrincipal JWTUserData jwtUserData){
        AdocaoResponse response = adocaoService.rejeitarAdocao(jwtUserData, adocao_id);
        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Cancela uma solicitação de adoção para o dono da solicitaçao",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{adocao_id}/cancelar")
    public ResponseEntity<AdocaoResponse> cancelarAdocao(@PathVariable Long adocao_id,
                                                         @AuthenticationPrincipal JWTUserData jwtUserData){
        AdocaoResponse response = adocaoService.cancelarAdocao(jwtUserData, adocao_id);
        return ResponseEntity.ok(response);
    }
}
