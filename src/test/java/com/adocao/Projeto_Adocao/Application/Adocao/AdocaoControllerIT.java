package com.adocao.Projeto_Adocao.Application.Adocao;

import com.adocao.Projeto_Adocao.Application.Animal.Animal;
import com.adocao.Projeto_Adocao.Application.Animal.AnimalRepository;
import com.adocao.Projeto_Adocao.Application.Animal.Porte;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc // Simular as chamadas HTTP
@ActiveProfiles("test") // Ler o application-test.yaml
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class AdocaoControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper; // Para converter objetos Java em JSON
    private final TokenJWTService tokenJWTService;
    private final UsuarioRepository usuarioRepository;
    private final AnimalRepository animalRepository;
    private final RoleRepository roleRepository;
    private final AdocaoRepository adocaoRepository;

    // Variáveis para guardar os dados criados no banco e usar nos testes
    private Usuario dono;
    private Usuario adotante;
    private Usuario invasor;
    private String tokenDono;
    private String tokenAdotante;
    private String tokenInvasor;
    private Role roleUser;

    @BeforeEach
    void setUpGlobal() {
        // Limpa as tabelas transacionais
        adocaoRepository.deleteAll();
        animalRepository.deleteAll();
        usuarioRepository.deleteAll();

        // BUSCA a Role que o Flyway já inseriu no banco
        roleUser = roleRepository.findByNome("USER")
                .orElseThrow(() -> new RuntimeException("Role USER não encontrada no banco!"));

        // Criando os Usuários
        dono = criarSalvarUsuario("Dono", "11111111111", "dono@email.com");
        adotante = criarSalvarUsuario("Adotante", "22222222222", "adotante@email.com");
        invasor = criarSalvarUsuario("Invasor", "33333333333", "hacker@email.com");

        // Tokens JWT do adotante e dono
        tokenDono = tokenJWTService.gerarToken(dono);
        tokenAdotante = tokenJWTService.gerarToken(adotante);
        tokenInvasor = tokenJWTService.gerarToken(invasor);

    }

    private Usuario criarSalvarUsuario(String nome, String cpf, String email) {
        Usuario u = Usuario.builder()
                .nome(nome).cpf(cpf).email(email).senha("senha123").ativo(true).build();
        u.adicionarRoles(roleUser);
        return usuarioRepository.save(u);
    }

    private Animal criarSalvarAnimal(String nome, String raca, String cor, Porte porte, LocalDate nascimento, Boolean castracao, String descricao, Usuario usuario, Boolean ativo) {
        Animal a = Animal.builder().nome(nome).raca(raca).cor(cor).porte(porte)
                .nascimento(nascimento).castracao(castracao).descricao(descricao)
                .usuario(usuario).ativo(ativo).build();
        return animalRepository.save(a);
    }

    private Adocao criarAdocao(Animal animal, Usuario adotanteUser, StatusAdocao status) {
        Adocao adocao = Adocao.builder().animal(animal).adotante(adotanteUser).donoOrigem(dono)
                .status(status).mensagem("Mensagem padrão").build();
        return adocaoRepository.save(adocao);
    }

    @Nested
    @DisplayName("POST /adocoes (Solicitar Adoção)")
    class PostSolicitarAdocaoEndpoint {

        private Animal animalDisponivel;

        @BeforeEach
        void setUpContexto() {
            animalDisponivel = criarSalvarAnimal("Rex", "Vira-lata", "Caramelo", Porte.MEDIO,
                    LocalDate.of(2022, 1, 1), false, "Muito dócil", dono, true);
        }

        @Test
        @DisplayName("Deve retornar 201 Created ao solicitar adoção com sucesso")
        void solicitarAdocao_ComSucesso() throws Exception {
            AdocaoRequest request = new AdocaoRequest(animalDisponivel.getId(), "Prometo cuidar!");
            String jsonBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/adocoes")
                            .header("Authorization", "Bearer " + tokenAdotante)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nomeAnimal").value("Rex"))
                    .andExpect(jsonPath("$.nomeAdotante").value("Adotante"))
                    .andExpect(jsonPath("$.status").value("PENDENTE"));
        }
    }

    @Nested
    @DisplayName("GET /adocoes/minhas-solicitacoes")
    class GetMinhasSolicitacoes {

        @BeforeEach
        void setUp() {
            Animal bolinha = criarSalvarAnimal("Bolinha", "Poodle", "Branco", Porte.PEQUENO,
                    LocalDate.of(2021,5, 10), true, "Gosta de brincar", dono, true);
            criarAdocao(bolinha, adotante, StatusAdocao.PENDENTE);
        }

        @Test
        @DisplayName("Deve listar as solicitações que o adotante fez com paginação")
        void listarMinhasSolicitacoes_DeveRetornarPagina_QuandoSucesso() throws Exception {

            // token do Adotante
            mockMvc.perform(get("/adocoes/minhas-solicitacoes")
                            .header("Authorization", "Bearer " + tokenAdotante)
                            .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isOk())
                    // Validando a estrutura de paginação do Spring (Page<T>)
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].nomeAnimal").value("Bolinha"))
                    .andExpect(jsonPath("$.content[0].mensagem").value("Mensagem padrão"))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    @Nested
    @DisplayName("GET /adocoes/meus-animais")
    class GetSolicitacoesRecebidas {
        @BeforeEach
        void setUp() {
            Animal bolinha = criarSalvarAnimal("Bolinha", "Poodle", "Branco", Porte.PEQUENO,
                    LocalDate.of(2021, 5, 10), true, "Gosta de brincar", dono, true);
            criarAdocao(bolinha, adotante, StatusAdocao.PENDENTE);
        }

        @Test
        @DisplayName("Deve listar as solicitações recebidas pelo dono do animal")
        void listarSolicitacoesRecebidas_DeveRetornarPagina_QuandoSucesso() throws Exception {

            // token do DONO
            mockMvc.perform(get("/adocoes/meus-animais")
                            .header("Authorization", "Bearer " + tokenDono)
                            .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].nomeAdotante").value("Adotante"));

        }
    }

    @Nested
    @DisplayName("GET /adocoes/{adocao_id}")
    class GetDetalhesAdocao {
        @BeforeEach
        void setUp() {
            Animal bolinha = criarSalvarAnimal("Bolinha", "Poodle", "Branco", Porte.PEQUENO,
                    LocalDate.of(2021, 5, 10), true, "Gosta de brincar", dono, true);
            criarAdocao(bolinha, adotante, StatusAdocao.PENDENTE);
        }

        @Test
        @DisplayName("Deve retornar 200 Ok ao tentar acessar detalhes de solicitação de adoção para um animal seu")
        void buscarDetalhesAdocao_DeveRetornarOk_QuandoDono() throws Exception {

            // ID da adoção que criamos no setUp
            Long adocaoId = adocaoRepository.findAll().get(0).getId();

            mockMvc.perform(get("/adocoes/" + adocaoId)
                            .header("Authorization", "Bearer " + tokenDono)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nomeAnimal").value("Bolinha"))
                    .andExpect(jsonPath("$.nomeAdotante").value("Adotante"))
                    .andExpect(jsonPath("$.nomeDonoOrigem").value("Dono"));

        }

        @Test
        @DisplayName("Deve retornar 200 Ok ao tentar acessar detalhes de uma solicitação sua")
        void buscarDetalhesAdocao_DeveRetornarOk_QuandoSolicitante() throws Exception {

            // ID da adoção que criamos no setUp
            Long adocaoId = adocaoRepository.findAll().get(0).getId();

            mockMvc.perform(get("/adocoes/" + adocaoId)
                            .header("Authorization", "Bearer " + tokenAdotante)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nomeAnimal").value("Bolinha"))
                    .andExpect(jsonPath("$.nomeAdotante").value("Adotante"))
                    .andExpect(jsonPath("$.nomeDonoOrigem").value("Dono"));
        }

        @Test
        @DisplayName("Deve retornar 403 Forbidden ao tentar acessar detalhes de adoção de terceiros")
        void buscarDetalhesAdocao_DeveRetornarForbidden_QuandoInvasor() throws Exception {

            // ID da adoção que criamos no setUp
            Long adocaoId = adocaoRepository.findAll().get(0).getId();

            mockMvc.perform(get("/adocoes/" + adocaoId)
                            .header("Authorization", "Bearer " + tokenInvasor)
                            .contentType(MediaType.APPLICATION_JSON))

                    // Aqui garantimos que a exceção virou o código HTTP correto!
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("Forbidden"))
                    .andExpect(jsonPath("$.message").value("Você não tem permissão para acessar os detalhes desta adoção."));
        }
    }

    @Nested
    @DisplayName("PATCH /adocoes/{id}/aprovar")
    class PatchAprovarAdocao {

        private Adocao adocaoDoAdotante;
        private Adocao adocaoDoInvasor;

        @BeforeEach
        void setUp() {
            Animal bolinha = criarSalvarAnimal("Bolinha", "Poodle", "Branco", Porte.PEQUENO,
                    LocalDate.of(2021, 5, 10), true, "Gosta de brincar", dono, true);

            adocaoDoAdotante = criarAdocao(bolinha, adotante, StatusAdocao.PENDENTE);
            adocaoDoInvasor = criarAdocao(bolinha, invasor, StatusAdocao.PENDENTE);
        }

        @Test
        @DisplayName("Deve aprovar adoção, inativar animal e rejeitar outras adoções no banco")
        void aprovarAdocao_ComSucesso_EVerificarBanco() throws Exception {

            // O Dono faz a requisição PATCH para aprovar a adoção 1
            mockMvc.perform(patch("/adocoes/" + adocaoDoAdotante.getId() + "/aprovar")
                            .header("Authorization", "Bearer " + tokenDono)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())

                    // Valida o retorno HTTP
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("APROVADA"));

            /*Validar o Banco de Dados*/
            // O animal "Bolinha" ficou inativo?
            Animal bolinhaAtualizado = animalRepository.findById(adocaoDoAdotante.getAnimal().getId()).get();
            org.junit.jupiter.api.Assertions.assertFalse(bolinhaAtualizado.getAtivo(), "O animal deveria estar inativo após a adoção");

            // A adoção 2 (do invasor) foi alterada para REJEITADA no banco
            Adocao adocaoDoInvasorAtualizada = adocaoRepository.findById(adocaoDoInvasor.getId()).get();
            org.junit.jupiter.api.Assertions.assertEquals(
                    StatusAdocao.REJEITADA,
                    adocaoDoInvasorAtualizada.getStatus(),
                    "As outras adoções pendentes deveriam ter sido rejeitadas"
            );
        }
    }
}