package com.adocao.Projeto_Adocao.Application.Adocao;

import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
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

    // Solicitações do usuario
    @GetMapping("/minhas-solicitacoes")
    public ResponseEntity<Page<AdocaoResponse>> listarMinhasSolicitacoes(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdocaoResponse> response = adocaoService.listarMinhasSolicitacoes(jwtUserData, pageable);
        return ResponseEntity.ok(response);
    }

    // Solicitações feitas para os meus animais
    @GetMapping("/meus-animais")
    public ResponseEntity<Page<AdocaoResponse>> listarSolicitacoesRecebidas(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdocaoResponse> response = adocaoService.listarSolicitacoesRecebidas(jwtUserData, pageable);
        return ResponseEntity.ok(response);
    }

    // Solicitações para um animal específico
    @GetMapping
    public ResponseEntity<Page<AdocaoResponse>> listarSolicitacoesPorAnimal(
            @RequestParam Long animalId,
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdocaoResponse> response = adocaoService.listarSolicitacoesPorAnimal(jwtUserData, animalId, pageable);
        return ResponseEntity.ok(response);
    }

    // Detalhes de uma única solicitação
    @GetMapping("/{adocaoId}")
    public ResponseEntity<AdocaoResponse> buscarDetalhesAdocao(
            @PathVariable Long adocaoId,
            @AuthenticationPrincipal JWTUserData jwtUserData) {

        AdocaoResponse response = adocaoService.buscarDetalhesAdocao(jwtUserData, adocaoId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/adocoes/{adocao_id}/aprovar")
    public ResponseEntity<AdocaoResponse> aprovarAdocao(@PathVariable Long adocao_id,
                                                        @AuthenticationPrincipal JWTUserData jwtUserData){
        AdocaoResponse response = adocaoService.aprovarAdocao(jwtUserData, adocao_id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/adocoes/{adocao_id}/rejeitar")
    public ResponseEntity<AdocaoResponse> rejeitarAdocao(@PathVariable Long adocao_id,
                                                         @AuthenticationPrincipal JWTUserData jwtUserData){
        AdocaoResponse response = adocaoService.rejeitarAdocao(jwtUserData, adocao_id);
        return ResponseEntity.ok(response);

    }

    @PatchMapping("/adocoes/{adocao_id}/cancelar")
    public ResponseEntity<AdocaoResponse> cancelarAdocao(@PathVariable Long adocao_id,
                                                         @AuthenticationPrincipal JWTUserData jwtUserData){
        AdocaoResponse response = adocaoService.cancelarAdocao(jwtUserData, adocao_id);
        return ResponseEntity.ok(response);
    }

 /*   @PatchMapping("/adocoes/{id}/status")

    @GetMapping("/me-solicitacoes")

    @GetMapping("/me-animais")

    @GetMapping("/{id}")
*/
}
