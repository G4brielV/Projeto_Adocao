package com.adocao.Projeto_Adocao.Application.Endereco;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    public ResponseEntity<DTODetalhamentoEndereco> cadastrarEndereco(DTOCadastroEndereco dtoCadastroEndereco,
                                                                     Usuario usuario,
                                                                     UriComponentsBuilder uriComponentsBuilder){

        // Verifica se o usuario ja tem endereço cadastrado
        if (usuario.getEndereco() != null) {
            throw new RuntimeException("Usuário já possui um endereço cadastrado.");
        }

        // Cadastra o Endereço
        Endereco novoEndereco = new Endereco(dtoCadastroEndereco);
        novoEndereco.setUsuario(usuario);
        Endereco enderecoSalvo = enderecoRepository.save(novoEndereco);

        // Atualiza o usuário com o ID do endereço
        usuario.setEndereco(enderecoSalvo); // Define o endereço para o usuário
        usuarioRepository.save(usuario);   // Salva o usuário com o endereço atualizado

        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/endereco/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(novoEndereco.getId()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(new DTODetalhamentoEndereco(novoEndereco));
    }


    public ResponseEntity<DTODetalhamentoEndereco> editarEndereco(Long enderecoId,
                                                                  Usuario usuario,
                                                                  DTOEditarEndereco dtoEditarEndereco,
                                                                  UriComponentsBuilder uriComponentsBuilder){

        // Pega o Id do usuario logado
        Long usuarioId = usuario.getId();

        // Verifica se o endereço existe e pertence ao usuário
        Endereco altEndereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado."));

        // Verifica se é o endereço do Usuario cadastrado
        if (!altEndereco.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não autorizado a editar este endereço.");
        }

        altEndereco.atualizarInformacoes(dtoEditarEndereco);

        // Salva o endereço atualizado
        enderecoRepository.save(altEndereco);


        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/usuario/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(altEndereco.getId()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(new DTODetalhamentoEndereco(altEndereco));
    }


}
