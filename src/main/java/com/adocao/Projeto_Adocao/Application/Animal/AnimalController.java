package com.adocao.Projeto_Adocao.Application.Animal;


import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/animal")
public class AnimalController {

    @Autowired
    AnimalService animalService;

    @Autowired
    AnimalRepository animalRepository;


//    @GetMapping
//    public String Hello(){
//        return "Hello";
//    }

    @Operation(
            summary = "Retorna lista de todos animais",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping()
    public ResponseEntity<List<DTODetalhamentoAnimal>> listar(@AuthenticationPrincipal Usuario usuario, UriComponentsBuilder uriComponentsBuilder){
            List<DTODetalhamentoAnimal> lista = animalRepository
                    .findAllByAtivoTrueAndUsuarioId(usuario.getId())
                    .stream()
                    .map(DTODetalhamentoAnimal::new)
                    .toList();

            return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Cadastra um animal",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/cadastro")
    public ResponseEntity<DTODetalhamentoAnimal> cadastrarAnimal(@RequestBody @Valid DTOCadastroAnimal dtoCadastroAnimal,
                                                                 @AuthenticationPrincipal Usuario usuario,
                                                                 UriComponentsBuilder uriComponentsBuilder) {
        return animalService.cadastrarAnimal(dtoCadastroAnimal, usuario, uriComponentsBuilder);
    }

    @PutMapping("/editar/{animalId}")
    public ResponseEntity<DTODetalhamentoAnimal> editarAnimal(
            @PathVariable Long animalId,
            @RequestBody @Valid DTOEditarAnimal dtoEditarAnimal,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriComponentsBuilder){
        return animalService.editarAnimal(animalId, usuario, dtoEditarAnimal, uriComponentsBuilder);
    }

    @PutMapping("/inativar/{animalId}")
    public ResponseEntity<?> inativarAnimal(
            @PathVariable Long animalId,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriComponentsBuilder){
        return animalService.inativarAnimal(animalId, usuario, uriComponentsBuilder);
    }

    @PutMapping("/ativar/{animalId}")
    public ResponseEntity<?> ativarAnimal(
            @PathVariable Long animalId,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriComponentsBuilder){
        return animalService.ativarAnimal(animalId, usuario, uriComponentsBuilder);
    }
}

