package com.adocao.Projeto_Adocao.Application.Animal;


import com.adocao.Projeto_Adocao.Application.DTO.StatusRequestDTO;
import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Tag(name = "4. Animais", description = "Gerenciamento dos animais")
@RestController
@RequiredArgsConstructor
@RequestMapping("/animais")
public class AnimalController {

    private final AnimalService animalService;

    @Operation(summary = "Lista todos os animais disponíveis para adoção (Vitrine) com filtros opcionais")
    @GetMapping()
    public ResponseEntity<Page<AnimalResponse>> listarDisponiveis(
            @RequestParam(required = false) Porte porte,
            @RequestParam(required = false) Boolean castracao,
            @RequestParam(required = false) String raca,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<AnimalResponse> pagina = animalService.buscarAnimaisDisponiveis(porte, castracao, raca, pageable);
        return ResponseEntity.ok(pagina);
    }

    @Operation(summary = "Retorna a lista paginada de todos os animais cadastrados pelo usuário autenticado")
    @GetMapping("/me")
    public ResponseEntity<Page<AnimalResponse>> listarMeusAnimais(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<AnimalResponse> pagina = animalService.buscarMeusAnimais(jwtUserData, pageable);
        return ResponseEntity.ok(pagina);
    }

    @Operation(
            summary = "Cadastra um animal",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping()
    public ResponseEntity<AnimalResponse> cadastrarAnimal(@RequestBody @Valid AnimalRequest animalRequest,
                                                          @AuthenticationPrincipal JWTUserData jwtUserData,
                                                          UriComponentsBuilder uriComponentsBuilder) {

        AnimalResponse response = animalService.cadastrarAnimal(jwtUserData, animalRequest);
        var uri = uriComponentsBuilder
                .path("/animais/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{animalId}")
    public ResponseEntity<AnimalResponse> editarAnimal(@PathVariable Long animalId,
                                                       @RequestBody @Valid AnimalUpdate animalUpdate,
                                                       @AuthenticationPrincipal JWTUserData jwtUserData,
                                                       UriComponentsBuilder uriComponentsBuilder){

        AnimalResponse response = animalService.editarAnimal(jwtUserData, animalId, animalUpdate);

        var uri = uriComponentsBuilder
                .path("/animais/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    /*Por como delete? ja que ele vai sumir?*/
    @PatchMapping("/status/{animalId}")
    public ResponseEntity<AnimalResponse> alterarStatus (@PathVariable Long animalId,
                                               @AuthenticationPrincipal JWTUserData jwtUserData,
                                               @RequestBody StatusRequestDTO request){
        AnimalResponse response = animalService.alterarStatus(jwtUserData, animalId, request.status());
        if (request.status()){
            return ResponseEntity.ok().body(response);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
}

