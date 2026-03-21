package com.adocao.Projeto_Adocao.Application.Admin;

import com.adocao.Projeto_Adocao.Application.Endereco.Endereco;
import com.adocao.Projeto_Adocao.Application.Endereco.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final EnderecoRepository enderecoRepository;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> helloAdmin(){
        return ResponseEntity.ok("Hello ADM");
    }

    @GetMapping("/enderecos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Endereco> getEndereco(@PathVariable Long id){

        Optional<Endereco> endereco = enderecoRepository.findById(id);
        return endereco.map(ResponseEntity::ok).orElseGet(() -> (ResponseEntity<Endereco>) ResponseEntity.notFound());

    }
}
