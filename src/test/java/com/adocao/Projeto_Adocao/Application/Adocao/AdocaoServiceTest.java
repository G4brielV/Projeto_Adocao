package com.adocao.Projeto_Adocao.Application.Adocao;

import com.adocao.Projeto_Adocao.Application.Animal.Animal;
import com.adocao.Projeto_Adocao.Application.Animal.AnimalRepository;
import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import com.adocao.Projeto_Adocao.Infra.Exception.BusinessRuleException;
import com.adocao.Projeto_Adocao.Infra.Exception.ForbiddenOperationException;
import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Uso do Mockito
@ExtendWith(MockitoExtension.class)
class AdocaoServiceTest {

    // Instância real do serviço que queremos testar
    @InjectMocks
    private AdocaoService adocaoService;
    // Versões "falsas" dos repositórios.
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AdocaoRepository adocaoRepository;

    @Test
    @DisplayName("Deve solicitar adoção com sucesso e retornar AdocaoResponse")
    void solicitarAdocao_DeveRetornarResponse_QuandoSucesso() {

        /*Preparação*/
        Long adotanteId = 1L;
        Long donoId = 2L;
        Long animalId = 10L;

        JWTUserData jwtUserData = new JWTUserData(adotanteId, "Lira", List.of("USER"));
        AdocaoRequest request = new AdocaoRequest(animalId, "Gostaria muito de adotar este pet!");

        Usuario adotante = Usuario.builder().id(adotanteId).nome("Lira").build();
        Usuario donoOriginal = Usuario.builder().id(donoId).nome("Dono Antigo").build();

        Animal animalDisponivel = Animal.builder()
                .id(animalId)
                .ativo(true)
                .usuario(donoOriginal)
                .build();

        when(usuarioRepository.getReferenceById(adotanteId)).thenReturn(adotante);
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animalDisponivel));
        when(adocaoRepository.existsByAnimalIdAndAdotanteIdAndStatus(animalId, adotanteId, StatusAdocao.PENDENTE))
                .thenReturn(false);


        /*Ação e verificação*/
        AdocaoResponse response = adocaoService.solicitarAdocao(jwtUserData, request);

        assertNotNull(response);
        assertEquals(animalId, response.animalId());
        assertEquals(adotanteId, response.adotanteId());
        assertEquals(StatusAdocao.PENDENTE, response.status());

        // Verifica se o método salvar foi invocado exatamente uma vez
        verify(adocaoRepository, times(1)).saveAndFlush(any(Adocao.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException ao tentar adotar um animal inativo")
    void solicitarAdocao_DeveFalhar_QuandoAnimalEstiverInativo() {

        /*Preparação*/
        Long adotanteId = 1L;
        Long animalId = 10L;
        JWTUserData jwtUserData = new JWTUserData(adotanteId, "Lira", List.of("USER"));
        AdocaoRequest request = new AdocaoRequest(animalId, "Gostaria muito de adotar este pet!");

        Usuario adotante = Usuario.builder().id(adotanteId).nome("Lira").build();

        Animal animalIndisponivel = Animal.builder()
                .id(animalId)
                .ativo(false)
                .build();

        when(usuarioRepository.getReferenceById(adotanteId)).thenReturn(adotante);
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animalIndisponivel));

        /*Ação e verificação*/
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            adocaoService.solicitarAdocao(jwtUserData, request);
        });

        // Mensagem do erro
        assertEquals("Animal indisponível para adoção", exception.getMessage());

        // Aplicação parou antes de tentar consultar se já existe adoção ou tentar salvar
        verify(adocaoRepository, never()).existsByAnimalIdAndAdotanteIdAndStatus(any(), any(), any());
        verify(adocaoRepository, never()).saveAndFlush(any(Adocao.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando já existir uma solicitação PENDENTE do mesmo usuário")
    void solicitarAdocao_DeveFalhar_QuandoJaExistirSolicitacaoPendente() {

        /* Preparação */
        Long adotanteId = 1L;
        Long donoId = 2L;
        Long animalId = 10L;

        JWTUserData jwtUserData = new JWTUserData(adotanteId, "Lira", List.of("USER"));
        AdocaoRequest request = new AdocaoRequest(animalId, "Tentei adotar de novo");

        Usuario adotante = Usuario.builder().id(adotanteId).nome("Lira").build();
        Usuario donoOriginal = Usuario.builder().id(donoId).nome("Dono").build();

        Animal animalDisponivel = Animal.builder()
                .id(animalId)
                .ativo(true)
                .usuario(donoOriginal)
                .build();

        when(usuarioRepository.getReferenceById(adotanteId)).thenReturn(adotante);
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animalDisponivel));

        // Simulamos que o banco encontrou uma solicitação anterior!
        when(adocaoRepository.existsByAnimalIdAndAdotanteIdAndStatus(animalId, adotanteId, StatusAdocao.PENDENTE))
                .thenReturn(true);

        /* Ação e verificação */
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            adocaoService.solicitarAdocao(jwtUserData, request);
        });

        // Validamos a mensagem de erro
        assertEquals("Você já possui uma solicitação pendente para este animal.", exception.getMessage());

        // Mais uma vez, garantimos que nada foi salvo
        verify(adocaoRepository, never()).saveAndFlush(any(Adocao.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException ao tentar adotar o próprio animal")
    void solicitarAdocao_DeveFalhar_QuandoAdotarProprioAnimal() {

        /*Preparação*/
        Long usuarioId = 1L;
        Long animalId = 10L;
        JWTUserData jwtUserData = new JWTUserData(usuarioId, "Lira", List.of("USER"));
        AdocaoRequest request = new AdocaoRequest(animalId, "Quero meu pet de volta");

        Usuario usuarioLogado = Usuario.builder().id(usuarioId).build();

        Animal animal = Animal.builder()
                .id(animalId)
                .ativo(true)
                .usuario(usuarioLogado)
                .build();

        when(usuarioRepository.getReferenceById(usuarioId)).thenReturn(usuarioLogado);
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));


        /*Ação e veriicação*/
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            adocaoService.solicitarAdocao(jwtUserData, request);
        });

        // Mensagem do erro
        assertEquals("Você não pode solicitar a adoção do seu próprio animal.", exception.getMessage());

        // Salvar NUNCA foi chamado, afinal, deu erro antes
        verify(adocaoRepository, never()).saveAndFlush(any(Adocao.class));
    }

    @Test
    @DisplayName("Deve lançar ForbiddenOperationException quando um usuário não envolvido tentar ver a adoção")
    void buscarDetalhesAdocao_DeveFalhar_QuandoUsuarioForUmInvasor() {

        /* Preparação */
        Long adotanteId = 1L;
        Long donoId = 2L;
        Long invasorId = 99L; // Usuario fora da adoção
        Long adocaoId = 100L;

        JWTUserData jwtUserDataInvasor = new JWTUserData(invasorId, "Hacker", List.of("USER"));

        Usuario adotante = Usuario.builder().id(adotanteId).nome("Adotante").build();
        Usuario donoOriginal = Usuario.builder().id(donoId).nome("Dono").build();
        Animal animal = Animal.builder().id(10L).usuario(donoOriginal).build();

        // Montamos a adoção com os usuários corretos
        Adocao adocao = Adocao.builder()
                .id(adocaoId)
                .animal(animal)
                .adotante(adotante)
                .donoOrigem(donoOriginal)
                .status(StatusAdocao.PENDENTE)
                .build();

        when(adocaoRepository.findById(adocaoId)).thenReturn(Optional.of(adocao));

        /* Ação e verificação */
        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class, () -> {
            adocaoService.buscarDetalhesAdocao(jwtUserDataInvasor, adocaoId);
        });

        // Validamos que ele foi barrado com a mensagem exata
        assertEquals("Você não tem permissão para acessar os detalhes desta adoção.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar os detalhes da adoção com sucesso quando o usuário for o ADOTANTE")
    void buscarDetalhesAdocao_DeveRetornarSucesso_QuandoUsuarioForAdotante() {

        /* Preparação */
        Long adotanteId = 1L;
        Long donoId = 2L;
        Long adocaoId = 100L;

        // O token JWT agora pertence ao Adotante
        JWTUserData jwtUserDataAdotante = new JWTUserData(adotanteId, "Lira", List.of("USER"));

        Usuario adotante = Usuario.builder().id(adotanteId).nome("Lira").build();
        Usuario donoOriginal = Usuario.builder().id(donoId).nome("Dono").build();
        Animal animal = Animal.builder().id(10L).usuario(donoOriginal).build();

        Adocao adocao = Adocao.builder()
                .id(adocaoId)
                .animal(animal)
                .adotante(adotante)
                .donoOrigem(donoOriginal)
                .status(StatusAdocao.PENDENTE)
                .build();

        when(adocaoRepository.findById(adocaoId)).thenReturn(Optional.of(adocao));

        /* Ação e verificação */
        AdocaoResponse response = adocaoService.buscarDetalhesAdocao(jwtUserDataAdotante, adocaoId);

        // O retorno não pode ser nulo e deve pertencer à adoção solicitada
        assertNotNull(response);
        assertEquals(adocaoId, response.id());
        assertEquals(adotanteId, response.adotanteId());
    }

    @Test
    @DisplayName("Deve retornar os detalhes da adoção com sucesso quando o usuário for o DONO do animal")
    void buscarDetalhesAdocao_DeveRetornarSucesso_QuandoUsuarioForDono() {

        /* Preparação */
        Long adotanteId = 1L;
        Long donoId = 2L;
        Long adocaoId = 100L;

        // O token JWT agora pertence ao Dono do Animal
        JWTUserData jwtUserDataDono = new JWTUserData(donoId, "Dono", List.of("USER"));

        Usuario adotante = Usuario.builder().id(adotanteId).nome("Lira").build();
        Usuario donoOriginal = Usuario.builder().id(donoId).nome("Dono").build();
        Animal animal = Animal.builder().id(10L).usuario(donoOriginal).build();

        Adocao adocao = Adocao.builder()
                .id(adocaoId)
                .animal(animal)
                .adotante(adotante)
                .donoOrigem(donoOriginal)
                .status(StatusAdocao.PENDENTE)
                .build();

        when(adocaoRepository.findById(adocaoId)).thenReturn(Optional.of(adocao));

        /* Ação e verificação */
        AdocaoResponse response = adocaoService.buscarDetalhesAdocao(jwtUserDataDono, adocaoId);

        assertNotNull(response);
        assertEquals(adocaoId, response.id());
        assertEquals(donoId, response.donoOrigemId());
    }

    @Test
    @DisplayName("Deve aprovar adoção, inativar animal e rejeitar outras solicitações com sucesso")
    void aprovarAdocao_DeveAprovar_QuandoUsuarioForDono() {

        /* Preparação */
        Long donoId = 2L;
        Long animalId = 10L;
        Long adocaoId = 100L;

        JWTUserData jwtUserDataDono = new JWTUserData(donoId, "Dono", List.of("USER"));

        Usuario donoOriginal = Usuario.builder().id(donoId).nome("Dono").build();
        Animal animal = Animal.builder().id(animalId).ativo(true).usuario(donoOriginal).build();
        Usuario adotante = Usuario.builder().id(3L).nome("Adotante").build();

        Adocao adocaoPendente = Adocao.builder()
                .id(adocaoId)
                .animal(animal)
                .donoOrigem(donoOriginal)
                .adotante(adotante)
                .status(StatusAdocao.PENDENTE)
                .build();

        when(adocaoRepository.findById(adocaoId)).thenReturn(Optional.of(adocaoPendente));

        /* Ação e verificação */
        AdocaoResponse response = adocaoService.aprovarAdocao(jwtUserDataDono, adocaoId);

        // Verifica mudanças de estado em memória
        assertEquals(StatusAdocao.APROVADA, response.status());
        assertFalse(animal.getAtivo(), "O animal deve ser inativado após aprovação");

        // Verifica se os métodos do banco foram chamados corretamente
        verify(adocaoRepository, times(1)).rejeitarOutrasAdocoesPendentesDoAnimal(animalId, adocaoId);
        verify(animalRepository, times(1)).save(animal);
        verify(adocaoRepository, times(1)).saveAndFlush(adocaoPendente);
    }

    @Test
    @DisplayName("Deve lançar ForbiddenOperationException ao tentar aprovar adoção sem ser o dono do animal")
    void aprovarAdocao_DeveFalhar_QuandoUsuarioNaoForDono() {

        /* Preparação */
        Long invasorId = 99L;
        Long donoId = 2L;
        Long adocaoId = 100L;
        Long animalId = 10L;

        JWTUserData jwtUserDataInvasor = new JWTUserData(invasorId, "Hacker", List.of("USER"));

        Usuario donoOriginal = Usuario.builder().id(donoId).nome("Dono").build();
        Animal animal = Animal.builder().id(animalId).usuario(donoOriginal).build();


        Adocao adocaoPendente = Adocao.builder()
                .id(adocaoId)
                .animal(animal)
                .donoOrigem(donoOriginal)
                .status(StatusAdocao.PENDENTE)
                .build();

        when(adocaoRepository.findById(adocaoId)).thenReturn(Optional.of(adocaoPendente));

        /* Ação e verificação */
        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class, () -> {
            adocaoService.aprovarAdocao(jwtUserDataInvasor, adocaoId);
        });

        assertEquals("Apenas o dono do animal pode aprovar a adoção.", exception.getMessage());
        verify(adocaoRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve lançar ForbiddenOperationException ao tentar cancelar adoção de outro usuário")
    void cancelarAdocao_DeveFalhar_QuandoUsuarioNaoForAdotante() {

        /* Preparação */
        Long invasorId = 99L;
        Long adotanteId = 1L;
        Long adocaoId = 100L;

        // Lira vai tentar cancelar a adoção de outra pessoa, ou o dono vai tentar cancelar
        JWTUserData jwtUserDataInvasor = new JWTUserData(invasorId, "Lira", List.of("USER"));

        Usuario adotante = Usuario.builder().id(adotanteId).nome("Adotante Real").build();

        Adocao adocaoPendente = Adocao.builder()
                .id(adocaoId)
                .adotante(adotante)
                .status(StatusAdocao.PENDENTE)
                .build();

        when(adocaoRepository.findById(adocaoId)).thenReturn(Optional.of(adocaoPendente));

        /* Ação e verificação */
        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class, () -> {
            adocaoService.cancelarAdocao(jwtUserDataInvasor, adocaoId);
        });

        assertEquals("Apenas o dono da solicitação pode cancelar a adoção.", exception.getMessage());
        verify(adocaoRepository, never()).saveAndFlush(any());
    }

}