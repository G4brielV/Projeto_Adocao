package com.adocao.Projeto_Adocao.Application.Usuario;


import com.adocao.Projeto_Adocao.Application.DTO.StatusRequestDTO;
import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
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

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UsuarioMeResponse> findUserById(@AuthenticationPrincipal JWTUserData jwtUserData){
        UsuarioMeResponse user = usuarioService.findUserById(jwtUserData.id());
        return ResponseEntity.ok().body(user);
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> alterarStatus (@AuthenticationPrincipal JWTUserData jwtUserData,
                                               @RequestBody StatusRequestDTO request){
        usuarioService.alterarStatus(jwtUserData, request.status());
        if (request.status()){
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }


}
