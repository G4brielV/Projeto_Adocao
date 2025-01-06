package com.adocao.Projeto_Adocao.Application.Animal;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/adocao")
public class AnimalController {

    @Autowired
    AnimalRepository animalRepository;

    @GetMapping
    public String Hello(){
        return "Hello";
    }


    @PostMapping() // Cadastro
    @Transactional
    public void cadastrar(@RequestBody @Valid DTOCadastroAnimal dados, UriComponentsBuilder uriComponentsBuilder) {
        Animal novo_animal = new Animal(dados);
        animalRepository.save(novo_animal);

    }
}
