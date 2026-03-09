package com.adocao.Projeto_Adocao.Application.Auth;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Login de usuario"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUsuario(@RequestBody @Valid LoginRequest loginRequest){
        LoginResponse token = authService.logarUsuario(loginRequest);
        return ResponseEntity.ok(token);
    }
}
