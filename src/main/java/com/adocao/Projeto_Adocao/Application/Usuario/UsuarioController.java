package com.adocao.Projeto_Adocao.Application.Usuario;


import com.adocao.Projeto_Adocao.Application.DTO.StatusRequestDTO;
import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(
            summary = "Retorna os dados do usuário autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UsuarioMeResponse> findUserById(@AuthenticationPrincipal JWTUserData jwtUserData){
        UsuarioMeResponse user = usuarioService.findUserById(jwtUserData.id());
        return ResponseEntity.ok().body(user);
    }

    @Operation(
            summary = "Desativa a conta do usuário autenticado efeito em cascata para seu endereço e animais cadastrados",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping ("/inativar")
    public ResponseEntity<Void> inativarConta (@AuthenticationPrincipal JWTUserData jwtUserData){
        usuarioService.alterarStatus(jwtUserData);
        return ResponseEntity.noContent().build();

    }


}
