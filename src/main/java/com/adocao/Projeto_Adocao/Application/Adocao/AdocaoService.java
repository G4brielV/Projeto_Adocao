package com.adocao.Projeto_Adocao.Application.Adocao;

import com.adocao.Projeto_Adocao.Application.Animal.Animal;
import com.adocao.Projeto_Adocao.Application.Animal.AnimalRepository;
import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import com.adocao.Projeto_Adocao.Infra.Exception.BusinessRuleException;
import com.adocao.Projeto_Adocao.Infra.Exception.ForbiddenOperationException;
import com.adocao.Projeto_Adocao.Infra.Exception.ResourceNotFoundException;
import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AdocaoService {

    private final AnimalRepository animalRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdocaoRepository adocaoRepository;

    public AdocaoResponse solicitarAdocao(JWTUserData jwtUserData, AdocaoRequest adocaoRequest) {
        Usuario usuarioLogado = usuarioRepository.getReferenceById(jwtUserData.id());
        Animal animal = animalRepository.findById(adocaoRequest.animalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal com ID " + adocaoRequest.animalId() + " não encontrado."));

        // Disponibilidade do animal
        if (!animal.getAtivo()) throw new BusinessRuleException("Animal indisponível para adoção");
        // O animal ja é dele?
        if (animal.getUsuario().getId().equals(usuarioLogado.getId()))  throw new BusinessRuleException("Você não pode solicitar a adoção do seu próprio animal.");
        // O usuário já tem uma solicitação pendente para este mesmo animal?
        boolean jaPossuiSolicitacao = adocaoRepository.existsByAnimalIdAndAdotanteIdAndStatus(
                animal.getId(), usuarioLogado.getId(), StatusAdocao.PENDENTE);
        if (jaPossuiSolicitacao) throw new BusinessRuleException("Você já possui uma solicitação pendente para este animal.");

        Adocao novaSolicitacao = AdocaoMapper.toAdocao(adocaoRequest, usuarioLogado, animal);
        adocaoRepository.saveAndFlush(novaSolicitacao);
        return AdocaoMapper.toAdocaoResponse(novaSolicitacao);

    }


    @Transactional(readOnly = true)
    public Page<AdocaoResponse> listarMinhasSolicitacoes(JWTUserData jwtUserData, Pageable pageable) {
        return adocaoRepository.findByAdotanteId(jwtUserData.id(), pageable)
                .map(AdocaoMapper::toAdocaoResponse);
    }

    @Transactional(readOnly = true)
    public Page<AdocaoResponse> listarSolicitacoesRecebidas(JWTUserData jwtUserData, Pageable pageable) {
        return adocaoRepository.findByDonoOrigemId(jwtUserData.id(), pageable)
                .map(AdocaoMapper::toAdocaoResponse);
    }

    @Transactional(readOnly = true)
    public Page<AdocaoResponse> listarSolicitacoesPorAnimal(JWTUserData jwtUserData, Long animalId, Pageable pageable) {
        // Regra de segurança: O usuário logado é realmente o dono deste animal?
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal não encontrado."));

        if (!Objects.equals(animal.getUsuario().getId(), jwtUserData.id())) throw new ForbiddenOperationException("Você não tem permissão para ver as solicitações deste animal.");

        return adocaoRepository.findByAnimalId(animalId, pageable)
                .map(AdocaoMapper::toAdocaoResponse);
    }

    @Transactional(readOnly = true)
    public AdocaoResponse buscarDetalhesAdocao(JWTUserData jwtUserData, Long adocaoId) {
        Adocao adocao = adocaoRepository.findById(adocaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Adoção com ID " + adocaoId + " não encontrada."));

        // Regra de segurança: Apenas o dono do animal ou o adotante podem ver os detalhes dessa negociação
        boolean isDono = Objects.equals(adocao.getDonoOrigem().getId(), jwtUserData.id());
        boolean isAdotante = Objects.equals(adocao.getAdotante().getId(), jwtUserData.id());

        if (!isDono && !isAdotante) throw new ForbiddenOperationException("Você não tem permissão para acessar os detalhes desta adoção.");
        return AdocaoMapper.toAdocaoResponse(adocao);
    }

    @Transactional
    public AdocaoResponse aprovarAdocao(JWTUserData jwtUserData, Long adocaoId) {
        Adocao adocao = buscarAdocaoPendente(adocaoId);

        // É o dono do animal que está logado?
        if (!Objects.equals(jwtUserData.id(), adocao.getDonoOrigem().getId())) throw new ForbiddenOperationException("Apenas o dono do animal pode aprovar a adoção.");
        Animal animal = adocao.getAnimal();
        animal.alterarStatus(false);
        adocao.setStatus(StatusAdocao.APROVADA);
        adocaoRepository.rejeitarOutrasAdocoesPendentesDoAnimal(animal.getId(), adocao.getId());
        animalRepository.save(animal);
        adocaoRepository.saveAndFlush(adocao);

        return AdocaoMapper.toAdocaoResponse(adocao);
    }

    @Transactional
    public AdocaoResponse rejeitarAdocao(JWTUserData jwtUserData, Long adocaoId) {
        Adocao adocao = buscarAdocaoPendente(adocaoId);

        // É o dono do animal que está logado?
        if (!Objects.equals(jwtUserData.id(), adocao.getDonoOrigem().getId())) throw new ForbiddenOperationException("Apenas o dono do animal pode rejeitar a adoção.");
        adocao.setStatus(StatusAdocao.REJEITADA);
        adocaoRepository.saveAndFlush(adocao);
        return AdocaoMapper.toAdocaoResponse(adocao);
    }

    @Transactional
    public AdocaoResponse cancelarAdocao(JWTUserData jwtUserData, Long adocaoId) {
        Adocao adocao = buscarAdocaoPendente(adocaoId);

        // É o dono do pedido da solicitação? (Adotante)
        if (!Objects.equals(jwtUserData.id(), adocao.getAdotante().getId())) throw new ForbiddenOperationException("Apenas o dono da solicitação pode cancelar a adoção.");
        adocao.setStatus(StatusAdocao.CANCELADA);
        adocaoRepository.saveAndFlush(adocao);
        return AdocaoMapper.toAdocaoResponse(adocao);
    }

    private Adocao buscarAdocaoPendente(Long adocaoId) {
        Adocao adocao = adocaoRepository.findById(adocaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Adoção com ID " + adocaoId + " não encontrada."));

        if (!adocao.getStatus().equals(StatusAdocao.PENDENTE)) {
            throw new BusinessRuleException("Apenas adoções pendentes podem ser modificadas.");
        }
        return adocao;
    }
}
