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
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc // Simular as chamadas HTTP
@ActiveProfiles("test") // Ler o application-test.properties
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
    private Usuario adotante;
    private Usuario invasor;
    private Animal animalDisponivel;
    private String tokenAdotante;
    private String tokenDono;

    @BeforeEach
    void setUp() {
        // Limpa as tabelas transacionais
        adocaoRepository.deleteAll();
        animalRepository.deleteAll();
        usuarioRepository.deleteAll();

        // BUSCA a Role que o Flyway já inseriu no banco
        Role roleUser = roleRepository.findByNome("USER")
                .orElseThrow(() -> new RuntimeException("Role USER não encontrada no banco!"));

        // Criando os Usuários
        Usuario dono = Usuario.builder()
                .nome("Dono")
                .cpf("11111111111")
                .email("dono@email.com")
                .senha("senha123")
                .ativo(true)
                .build();
        dono.adicionarRoles(roleUser);
        usuarioRepository.save(dono);

        adotante = Usuario.builder()
                .nome("Adotante")
                .cpf("22222222222")
                .email("adotante@email.com")
                .senha("senha123")
                .ativo(true)
                .build();
        adotante.adicionarRoles(roleUser);
        usuarioRepository.save(adotante);

        // Novo usuário Hacker/Invasor que você sugeriu
        invasor = Usuario.builder()
                .nome("Invasor")
                .cpf("33333333333")
                .email("hacker@email.com")
                .senha("senha123")
                .ativo(true)
                .build();
        invasor.adicionarRoles(roleUser);
        usuarioRepository.save(invasor);

        // Criando animais
        // Disponivel para o Post
        animalDisponivel = Animal.builder()
                .nome("Rex")
                .raca("Vira-lata")
                .cor("Caramelo")
                .porte(Porte.MEDIO)
                .nascimento(LocalDate.of(2022, 1, 1))
                .castracao(true)
                .descricao("Muito dócil")
                .usuario(dono)
                .ativo(true)
                .build();

        animalRepository.save(animalDisponivel);

        // Animal para listagem
        Animal animalParaListagem = Animal.builder()
                .nome("Bolinha") // Este vamos usar para pré-cadastrar uma adoção
                .raca("Poodle")
                .cor("Branco")
                .porte(Porte.PEQUENO)
                .nascimento(LocalDate.of(2021, 5, 10))
                .castracao(true)
                .descricao("Gosta de brincar")
                .usuario(dono)
                .ativo(true)
                .build();
        animalRepository.save(animalParaListagem);


        // Criando adocoes
        // Adocao realizada pelo adotante ao animalParaListagem do dono
        Adocao adocao1 = Adocao.builder()
                .animal(animalParaListagem)
                .adotante(adotante)
                .donoOrigem(dono)
                .status(StatusAdocao.PENDENTE)
                .mensagem("Já deixei uma solicitação pronta no banco!")
                .build();
        adocaoRepository.save(adocao1);

        // Adocao realizada pelo invasor ao animalParaListagem do dono
        Adocao adocao2 = Adocao.builder()
                .animal(animalParaListagem)
                .adotante(invasor)
                .donoOrigem(dono)
                .status(StatusAdocao.PENDENTE)
                .mensagem("Eu também quero o Bolinha!")
                .build();
        adocaoRepository.save(adocao2);

        // Tokens JWT do adotante e dono
        tokenAdotante = tokenJWTService.gerarToken(adotante);
        tokenDono = tokenJWTService.gerarToken(dono);
    }


    @Test
    @DisplayName("Deve retornar 201 Created ao solicitar adoção com sucesso")
    void solicitarAdocao_ComSucesso() throws Exception {

        // Prepara o corpo da requisição (JSON)
        AdocaoRequest request = new AdocaoRequest(animalDisponivel.getId(), "Prometo cuidar muito bem!");
        String jsonBody = objectMapper.writeValueAsString(request);

        // Faz a requisição HTTP POST para /adocoes simulando o cliente
        mockMvc.perform(post("/adocoes")
                        .header("Authorization", "Bearer " + tokenAdotante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                        /*.andDo(print())*/


                // Validações
                .andExpect(status().isCreated()) // Retorno 201
                .andExpect(jsonPath("$.nomeAnimal").value("Rex")) // Json de resposta: nome do animal
                .andExpect(jsonPath("$.nomeAdotante").value("Adotante")) // Json de resposta: nome do animal
                .andExpect(jsonPath("$.status").value("PENDENTE")); // Json de resposta: status
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
                .andExpect(jsonPath("$.content[0].mensagem").value("Já deixei uma solicitação pronta no banco!"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Deve listar as solicitações recebidas pelo dono do animal")
    void listarSolicitacoesRecebidas_DeveRetornarPagina_QuandoSucesso() throws Exception {

        // token do DONO
        mockMvc.perform(get("/adocoes/meus-animais")
                        .header("Authorization", "Bearer " + tokenDono)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nomeAdotante").value("Invasor"))
                .andExpect(jsonPath("$.content[1].nomeAdotante").value("Adotante"));

    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar acessar detalhes de adoção de terceiros")
    void buscarDetalhesAdocao_DeveRetornarForbidden_QuandoInvasor() throws Exception {

        // ID da adoção que criamos no setUp
        Long adocaoId = adocaoRepository.findAll().get(0).getId();

        Usuario invasor = usuarioRepository.findByCpf("33333333333").get();
        String tokenInvasor = tokenJWTService.gerarToken(invasor);

        mockMvc.perform(get("/adocoes/" + adocaoId)
                        .header("Authorization", "Bearer " + tokenInvasor)
                        .contentType(MediaType.APPLICATION_JSON))

                // Aqui garantimos que a exceção virou o código HTTP correto!
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Você não tem permissão para acessar os detalhes desta adoção."));
    }

    @Test
    @DisplayName("Deve aprovar adoção, inativar animal e rejeitar outras adoções no banco")
    void aprovarAdocao_ComSucesso_EVerificarBanco() throws Exception {

        // Duas adoções pendentes que estão no banco
        List<Adocao> adocoesPendentes = adocaoRepository.findAll();
        // Vai ser aprovada
        Adocao adocaoDoAdotante = adocoesPendentes.stream()
                .filter(a -> a.getAdotante().getId().equals(adotante.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Adoção do adotante não encontrada"));

        // Vai ser recusada
        Adocao adocaoDoInvasor = adocoesPendentes.stream()
                .filter(a -> a.getAdotante().getId().equals(invasor.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Adoção do invasor não encontrada"));

        // O Dono faz a requisição PATCH para aprovar a adoção 1
        mockMvc.perform(patch("/adocoes/" + adocaoDoAdotante.getId() + "/aprovar")
                        .header("Authorization", "Bearer " + tokenDono)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

                // Valida o retorno HTTP
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"));

        // Validar o Banco de Dados

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