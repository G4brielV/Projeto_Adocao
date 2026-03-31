package com.adocao.Projeto_Adocao.Application.Auth;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @Operation(
            summary = "Login de usuário"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        LoginResponse login = authService.login(loginRequest);
        return ResponseEntity.ok(login);
    }

    @Operation(
            summary = "Cadastra um novo usuário"
    )
    @PostMapping("/cadastro")
    public ResponseEntity<CadastroResponse> cadastro(@RequestBody @Valid CadastroRequest cadastroRequest, UriComponentsBuilder uriComponentsBuilder){
        CadastroResponse response = authService.cadastro(cadastroRequest);

        var uri = uriComponentsBuilder
                .path("/usuarios/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(
            summary = "Reativar conta de usuário ja cadastrado"
    )
    @PostMapping("/reativar")
    public ResponseEntity<CadastroResponse> reativarConta(@RequestBody LoginRequest loginRequest) {
        CadastroResponse response = authService.reativarConta(loginRequest);
        return ResponseEntity.ok(response);
    }
}
