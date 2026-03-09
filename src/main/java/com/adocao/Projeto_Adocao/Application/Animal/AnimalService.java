package com.adocao.Projeto_Adocao.Application.Animal;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final UsuarioRepository usuarioRepository;

    public List<AnimalResponse> getAnimalsFromUser(Usuario usuario) {
        return animalRepository
                .findAllByAtivoTrueAndUsuarioId(usuario.getId())
                .stream()
                .map(animal -> AnimalMapper.toAnimalResponse(animal))
                .toList();
    }

    @Transactional
    public AnimalResponse cadastrarAnimal(AnimalRequest animalRequest, Usuario usuario){
       /* // Extrai o ID do usuário logado
        Long usuarioId = SecurityUtil.getIdUsuarioAutenticado();
         // Busca o usuário no banco
        Usuario usuarioLogado = usuarioRepository.getReferenceById(usuarioId);*/

        Animal novoAnimal = AnimalMapper.toAnimal(animalRequest, usuario);
        animalRepository.save(novoAnimal);

        return AnimalMapper.toAnimalResponse(novoAnimal);
    }

    @Transactional
    public AnimalResponse editarAnimal(Long animalId, Usuario usuario, AnimalUpdate animalUpdate){
        // Verfica se o Usuario logado tem permiição para editar o animal
        Animal animal = animalExiste(animalId);
        verificarPermicao(animal, usuario);
        animal.atualizarInformacoes(animalUpdate);

        // Salva o animal atualizado
        animalRepository.save(animal);
        return AnimalMapper.toAnimalResponse(animal);
    }

    @Transactional
    public void inativarAnimal(Long animalId, Usuario usuario) {
        Animal animal = animalExiste(animalId);
        verificarPermicao(animal, usuario);
        animal.inativar();
    }

    @Transactional
    public void ativarAnimal(Long animalId, Usuario usuario) {
        Animal animal = animalExiste(animalId);
        verificarPermicao(animal, usuario);
        animal.ativar();
    }

    /*TODO: Verifica se o animal pertence ao usuario*/
    private Boolean verificarPermicao(Animal animal, Usuario usuario){
        // Pega o id do usuario logado
        Long usuarioId = usuario.getId();
        // Verifica se o animal pertence ao usuário logado
        if (!animal.getUsuario().getId().equals(usuarioId)) {
            /*Isso é um erro? Runtime? HTTP?*/
            return false;
            /*throw new RuntimeException("Usuário não tem permissão para editar este animal");*/
        }
        return true;
    }

    /*TODO: Verifica se o animal existe pelo id*/
    private Animal animalExiste(Long animalId){
        return animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado."));
    }
}
