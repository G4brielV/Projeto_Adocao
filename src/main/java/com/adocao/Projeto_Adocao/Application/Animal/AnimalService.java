package com.adocao.Projeto_Adocao.Application.Animal;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    public ResponseEntity<DTODetalhamentoAnimal> cadastrarAnimal(DTOCadastroAnimal dtoCadastroAnimal, Usuario usuario, UriComponentsBuilder uriComponentsBuilder){

//        // Extrai o ID do usuário logado
//        Long usuarioId = SecurityUtil.getIdUsuarioAutenticado();
//        // Busca o usuário no banco
//        Usuario usuarioLogado = usuarioRepository.getReferenceById(usuarioId);

        Animal novoAnimal = new Animal(dtoCadastroAnimal);

        novoAnimal.setUsuario(usuario);

        animalRepository.save(novoAnimal);

        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/endereco/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(novoAnimal.getId()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(new DTODetalhamentoAnimal(novoAnimal));
    }


    public ResponseEntity<DTODetalhamentoAnimal> editarAnimal(Long animalId, Usuario usuario, DTOEditarAnimal dtoEditarAnimal, UriComponentsBuilder uriComponentsBuilder){

        // Verfica se o Usuario logado tem permiição para editar o animal
        Animal altAnimal = verificarPermicao(animalId, usuario);
        altAnimal.atualizarInformacoes(dtoEditarAnimal);

        // Salva o animal atualizado
        animalRepository.save(altAnimal);


        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/usuario/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(altAnimal.getId()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(new DTODetalhamentoAnimal(altAnimal));
    }

    public ResponseEntity<?> inativarAnimal(Long animalId, Usuario usuario, UriComponentsBuilder uriComponentsBuilder) {

        Animal animalIntv = verificarPermicao(animalId, usuario);

        animalIntv.setAtivo(false);

        animalRepository.save(animalIntv);

        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/animal/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(animalIntv.getId()) // Pegar o ID do novo usuario
                .toUri();

       return ResponseEntity.ok(uri);
    }

    public ResponseEntity<?> ativarAnimal(Long animalId, Usuario usuario, UriComponentsBuilder uriComponentsBuilder) {

        Animal animalIntv = verificarPermicao(animalId, usuario);

        animalIntv.setAtivo(true);

        animalRepository.save(animalIntv);

        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/animal/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(animalIntv.getId()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.ok(uri);
    }


    public Animal verificarPermicao(Long animalId, Usuario usuario){
        // Pega o id do usuario logado
        Long usuarioId = usuario.getId();

        // Verifica se o animal existe e pertence ao usuário
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado."));

        // Verifica se o animal pertence ao usuário logado
        if (!animal.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não tem permissão para editar este animal");
        }

        return animal;
    }
}
