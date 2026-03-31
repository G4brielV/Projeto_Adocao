package com.adocao.Projeto_Adocao.Application.Endereco;

import com.adocao.Projeto_Adocao.Application.DTO.StatusRequestDTO;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class EnderecoControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TokenJWTService tokenJWTService;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final EnderecoRepository enderecoRepository;

    private Endereco endereco;
    private String tokenUsuario;

    @BeforeEach
    void setUp() {
        enderecoRepository.deleteAll();
        usuarioRepository.deleteAll();

        Role roleUser = roleRepository.findByNome("USER").orElseThrow();

        Usuario usuario = Usuario.builder()
                .nome("Maria Silva")
                .cpf("98765432100")
                .email("maria@email.com")
                .senha("senha123")
                .ativo(true)
                .build();
        usuario.adicionarRoles(roleUser);
        usuario = usuarioRepository.save(usuario);

        endereco = Endereco.builder()
                .rua("Rua da Aurora")
                .numero("456")
                .bairro("Boa Vista")
                .cidade("Recife")
                .estado("PE")
                .usuario(usuario)
                .ativo(true)
                .build();
        endereco = enderecoRepository.save(endereco);

        tokenUsuario = tokenJWTService.gerarToken(usuario);
    }

    @Nested
    @DisplayName("PATCH /enderecos/status")
    class PatchEnderecoEndpoint {
        @Test
        @DisplayName("Deve retornar 200 OK e desativar o endereço do usuário no banco")
        void alterarStatus_DeveDesativarEndereco_QuandoStatusForFalse() throws Exception {

            StatusRequestDTO request = new StatusRequestDTO(false);
            String jsonBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(patch("/enderecos/status")
                            .header("Authorization", "Bearer " + tokenUsuario)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andDo(print())

                    // Assert HTTP: Retorna 200 e os dados do endereço
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado").value("PE"))
                    .andExpect(jsonPath("$.rua").value("Rua da Aurora"));

            // Assert DB: Garantia final de que o banco de dados refletiu a mudança
            Endereco enderecoAtualizado = enderecoRepository.findById(endereco.getId()).get();
            assertFalse(enderecoAtualizado.getAtivo(), "O endereço deveria estar inativo no banco de dados.");
        }

        @Test
        @DisplayName("Deve retornar 403 Forbidden se o usuário não enviar o token JWT")
        void alterarStatus_DeveRetornarForbidden_QuandoSemToken() throws Exception {
            StatusRequestDTO request = new StatusRequestDTO(false);
            String jsonBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(patch("/enderecos/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isForbidden());
        }
    }
}