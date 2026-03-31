package com.adocao.Projeto_Adocao.Application.Animal;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import com.adocao.Projeto_Adocao.Infra.Exception.ForbiddenOperationException;
import com.adocao.Projeto_Adocao.Infra.Exception.ResourceNotFoundException;
import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final UsuarioRepository usuarioRepository;

    public Page<AnimalResponse> buscarMeusAnimais(JWTUserData jwtUserData, Pageable pageable) {
        return animalRepository.findAllByUsuarioId(jwtUserData.id(), pageable)
                .map(AnimalMapper::toAnimalResponse);
    }

    public Page<AnimalResponse> buscarAnimaisDisponiveis(Porte porte, Boolean castracao, String raca, Pageable pageable) {
        return animalRepository.findAnimaisDisponiveisComFiltros(porte, castracao, raca, pageable)
                .map(AnimalMapper::toAnimalResponse);
    }

    @Transactional
    public AnimalResponse cadastrarAnimal(JWTUserData jwtUserData, AnimalRequest animalRequest){
        Usuario usuarioLogado = usuarioRepository.getReferenceById(jwtUserData.id());
        Animal novoAnimal = AnimalMapper.toAnimal(animalRequest, usuarioLogado);
        animalRepository.save(novoAnimal);
        return AnimalMapper.toAnimalResponse(novoAnimal);
    }

    @Transactional
    public AnimalResponse editarAnimal(JWTUserData jwtUserData, Long animalId, AnimalUpdate animalUpdate){
        // Verfica se o Usuario logado tem permiição para editar o animal
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal com ID " + animalId + " não encontrado."));
        verificarPermicao(animal, jwtUserData.id());
        animal.atualizarInformacoes(animalUpdate);

        // Salva o animal atualizado
        animalRepository.save(animal);
        return AnimalMapper.toAnimalResponse(animal);
    }


    public AnimalResponse alterarStatus(JWTUserData jwtUserData, Long animalId, Boolean status) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal com ID " + animalId + " não encontrado."));
        verificarPermicao(animal, jwtUserData.id());
        animal.alterarStatus(status);
        animalRepository.save(animal);
        return AnimalMapper.toAnimalResponse(animal);

    }

    /*TODO: Verifica se o animal pertence ao usuario*/
    private void verificarPermicao(Animal animal, Long usuarioId){
        if (!animal.getUsuario().getId().equals(usuarioId)) {
            throw new ForbiddenOperationException("Você não tem permissão para modificar os dados deste animal.");
        }
    }


}
