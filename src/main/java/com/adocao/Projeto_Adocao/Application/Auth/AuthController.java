package com.adocao.Projeto_Adocao.Application.Auth;

import com.adocao.Projeto_Adocao.Infra.Security.TokenJWTService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;


@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final TokenJWTService tokenJWTService;

    @Operation(
            summary = "Login de usuario"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        LoginResponse login = authService.login(loginRequest);
        return ResponseEntity.ok(login);
    }

    @Operation(
            summary = "Cadastra um novo usuario"
    )
    @PostMapping("/cadastro")
    public ResponseEntity<CadastroResponse> cadastro(@RequestBody @Valid CadastroRequest cadastroRequest, UriComponentsBuilder uriComponentsBuilder){
        CadastroResponse response = authService.cadastro(cadastroRequest);

        var uri = uriComponentsBuilder
                .path("/usuario/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(response.id()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }
}
