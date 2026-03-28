package com.adocao.Projeto_Adocao.Application.Animal;


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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/animais")
public class AnimalController {

    private final AnimalService animalService;

    @Operation(
            summary = "Retorna lista de todos animais do usuario",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping()
    public ResponseEntity<List<AnimalResponse>> listar(@AuthenticationPrincipal JWTUserData jwtUserData,
                                                       UriComponentsBuilder uriComponentsBuilder){

            List<AnimalResponse> lista = animalService.getAnimalsFromUser(jwtUserData);
            return ResponseEntity.ok(lista);
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
                .path("/endereco/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(response.id()) // Pegar o ID do novo usuario
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
                .path("/usuario/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(response.id()) // Pegar o ID do novo usuario
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

