package com.adocao.Projeto_Adocao.Application.Admin;

import com.adocao.Projeto_Adocao.Application.Endereco.EnderecoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final EnderecoRepository enderecoRepository;

    @Operation(
            summary = "Endpoint de teste para administradores",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> helloAdmin(){
        return ResponseEntity.ok("Hello ADM");
    }
}
