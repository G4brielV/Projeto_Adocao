package com.adocao.Projeto_Adocao.Application.Animal;


import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
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
@RequestMapping("/animal")
public class AnimalController {

    private  final AnimalService animalService;

    @Operation(
            summary = "Retorna lista de todos animais do usuario",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping()
    public ResponseEntity<List<AnimalResponse>> listar(@AuthenticationPrincipal Usuario usuario, UriComponentsBuilder uriComponentsBuilder){
            List<AnimalResponse> lista = animalService.getAnimalsFromUser(usuario);
            return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Cadastra um animal",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/cadastro")
    public ResponseEntity<AnimalResponse> cadastrarAnimal(@RequestBody @Valid AnimalRequest animalRequest,
                                                          @AuthenticationPrincipal Usuario usuario,
                                                          UriComponentsBuilder uriComponentsBuilder) {
        AnimalResponse response = animalService.cadastrarAnimal(animalRequest, usuario);
        var uri = uriComponentsBuilder
                .path("/endereco/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(response.id()) // Pegar o ID do novo usuario
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/editar/{animalId}")
    public ResponseEntity<AnimalResponse> editarAnimal(
            @PathVariable Long animalId,
            @RequestBody @Valid AnimalUpdate animalUpdate,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriComponentsBuilder){

        AnimalResponse response = animalService.editarAnimal(animalId, usuario, animalUpdate);

        var uri = uriComponentsBuilder
                .path("/usuario/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(response.id()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/inativar/{animalId}")
    public ResponseEntity<?> inativarAnimal(
            @PathVariable Long animalId,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriComponentsBuilder){

        animalService.inativarAnimal(animalId, usuario);
        var uri = uriComponentsBuilder
                .path("/animal/{id}")
                .buildAndExpand(animalId)
                .toUri();
        return ResponseEntity.ok(uri);
    }

    @PutMapping("/ativar/{animalId}")
    public ResponseEntity<?> ativarAnimal(
            @PathVariable Long animalId,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriComponentsBuilder){
        animalService.ativarAnimal(animalId, usuario);
        var uri = uriComponentsBuilder
                .path("/animal/{id}")
                .buildAndExpand(animalId)
                .toUri();
        return ResponseEntity.ok(uri);
    }
}

