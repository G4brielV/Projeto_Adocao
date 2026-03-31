package com.adocao.Projeto_Adocao.Application.Usuario;

import com.adocao.Projeto_Adocao.Application.Animal.Animal;
import com.adocao.Projeto_Adocao.Application.Animal.AnimalRepository;
import com.adocao.Projeto_Adocao.Application.Animal.Porte;
import com.adocao.Projeto_Adocao.Application.DTO.StatusRequestDTO;
import com.adocao.Projeto_Adocao.Application.Endereco.Endereco;
import com.adocao.Projeto_Adocao.Application.Endereco.EnderecoRepository;
import com.adocao.Projeto_Adocao.Application.Usuario.Roles.Role;
import com.adocao.Projeto_Adocao.Application.Usuario.Roles.RoleRepository;
import com.adocao.Projeto_Adocao.Infra.Security.TokenJWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class UsuarioControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TokenJWTService tokenJWTService;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final EnderecoRepository enderecoRepository;
    private final AnimalRepository animalRepository;

    private Usuario usuario;
    private Endereco endereco;
    private Animal animal;
    private String tokenUsuario;

    @BeforeEach
    void setUp() {
        animalRepository.deleteAll();
        enderecoRepository.deleteAll();
        usuarioRepository.deleteAll();

        Role roleUser = roleRepository.findByNome("USER").orElseThrow();

        // 1. Cria o Usuário Ativo
        usuario = Usuario.builder()
                .nome("João da Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .senha("senha123")
                .ativo(true)
                .build();
        usuario.adicionarRoles(roleUser);
        usuario = usuarioRepository.save(usuario);

        // 2. Cria o Endereço Ativo e vincula ao usuário
        endereco = Endereco.builder()
                .rua("Rua Principal")
                .numero("123")
                .bairro("Centro")
                .cidade("Recife")
                .estado("PE")
                .usuario(usuario)
                .ativo(true)
                .build();
        endereco = enderecoRepository.save(endereco);

        // Atualizando o usuário com o endereço para o Hibernate gerenciar
        usuario.atualizarEndereco(endereco);
        usuarioRepository.save(usuario);

        // 3. Cria um Animal Ativo e vincula ao usuário
        animal = Animal.builder()
                .nome("Rex")
                .raca("Vira-lata")
                .cor("Caramelo")
                .porte(Porte.MEDIO)
                .nascimento(LocalDate.of(2022, 1, 1))
                .castracao(true)
                .usuario(usuario)
                .ativo(true)
                .build();
        animal = animalRepository.save(animal);

        tokenUsuario = tokenJWTService.gerarToken(usuario);
    }

    @Test
    @DisplayName("Deve retornar 204 No Content e desativar o usuário, seu endereço e seus animais em cascata")
    void alterarStatus_DeveDesativarEmCascata_QuandoStatusForFalse() throws Exception {

        // Prepara o JSON da requisição
        StatusRequestDTO request = new StatusRequestDTO(false);
        String jsonBody = objectMapper.writeValueAsString(request);

        // PATCH simulando o usuário desativando a própria conta
        mockMvc.perform(patch("/usuarios/status")
                        .header("Authorization", "Bearer " + tokenUsuario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                // Assert HTTP: O Controller retorna 204 No Content quando desativa
                .andExpect(status().isNoContent());

        // Assert DB: Buscar entidades atualizadas no banco de dados e garantir a desativação em cadeia!
        Usuario usuarioAtualizado = usuarioRepository.findById(usuario.getId()).get();
        assertFalse(usuarioAtualizado.getAtivo(), "O usuário deveria estar inativo.");

        Endereco enderecoAtualizado = enderecoRepository.findById(endereco.getId()).get();
        assertFalse(enderecoAtualizado.getAtivo(), "O endereço deveria ter sido desativado junto com o usuário.");

        Animal animalAtualizado = animalRepository.findById(animal.getId()).get();
        assertFalse(animalAtualizado.getAtivo(), "O animal do usuário deveria ter sido desativado.");
    }
}