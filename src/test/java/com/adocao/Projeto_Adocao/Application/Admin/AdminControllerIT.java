package com.adocao.Projeto_Adocao.Application.Admin;

import com.adocao.Projeto_Adocao.Application.Usuario.Roles.Role;
import com.adocao.Projeto_Adocao.Application.Usuario.Roles.RoleRepository;
import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import com.adocao.Projeto_Adocao.Infra.Security.TokenJWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc // Simular as chamadas HTTP
@ActiveProfiles("test") // Ler o application-test.yaml
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class AdminControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper; // Para converter objetos Java em JSON
    private final TokenJWTService tokenJWTService;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    // Variáveis para guardar os dados criados no banco e usar nos testes
    private Usuario admin;
    private Usuario usuario;
    private String tokenAdmin;
    private String tokenUsuario;

    @BeforeEach
    void setUp() {
        // Limpa as tabelas transacionais
        usuarioRepository.deleteAll();

        // BUSCA a Role que o Flyway já inseriu no banco
        Role roleUser = roleRepository.findByNome("USER")
                .orElseThrow(() -> new RuntimeException("Role USER não encontrada no banco!"));

        Role roleAdmin = roleRepository.findByNome("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role ADMIN não encontrada no banco!"));

        // Criando os Usuários
        admin = Usuario.builder()
                .nome("admin")
                .cpf("11111111111")
                .email("admin@email.com")
                .senha("senha123")
                .ativo(true)
                .build();
        admin.adicionarRoles(roleUser);
        admin.adicionarRoles(roleAdmin);
        usuarioRepository.save(admin);

        usuario = Usuario.builder()
                .nome("usuario")
                .cpf("22222222222")
                .email("usuario@email.com")
                .senha("senha123")
                .ativo(true)
                .build();
        usuario.adicionarRoles(roleUser);
        usuarioRepository.save(usuario);

        // Tokens JWT do adotante e dono
        tokenAdmin = tokenJWTService.gerarToken(admin);
        tokenUsuario = tokenJWTService.gerarToken(usuario);
    }

    @Nested
    @DisplayName("GET /admin")
    class GetAdminEndpoint {

        @Test
        @DisplayName("Deve retornar 200 OK a chamada com a role ADMIN")
        void helloAdmin_ComSucesso() throws Exception {
            // token do ADMIN
            mockMvc.perform(get("/admin")
                            .header("Authorization", "Bearer " + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve retornar 403 Forbidden ao tentar acessar endpoint admin sem a role ADMIN")
        void helloAdmin_DeveRetornarForbidden_QuandoUsuario() throws Exception {
            // token do DONO
            mockMvc.perform(get("/admin")
                            .header("Authorization", "Bearer " + tokenUsuario)
                            .contentType(MediaType.APPLICATION_JSON))
                    // Aqui garantimos que a exceção virou o código HTTP correto!
                    .andExpect(status().isForbidden());
        }
    }
}