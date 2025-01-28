package com.adocao.Projeto_Adocao.Application.Endereco;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import com.adocao.Projeto_Adocao.Security.SecurityUtil;
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


    public ResponseEntity<DTODetalhamentoEndereco> cadastrarEndereco(DTOCadastroEndereco dtoCadastroEndereco, UriComponentsBuilder uriComponentsBuilder){

        // Extrai o ID do usuário logado
        Long usuarioId = SecurityUtil.getIdUsuarioAutenticado();
        // Busca o usuário no banco
        Usuario usuarioLogado = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verifica se o usuario ja tem endereço cadastrado
        if (usuarioLogado.getEndereco() != null) {
            throw new RuntimeException("Usuário já possui um endereço cadastrado.");
        }

        // Cadastra o Endereço
        Endereco novoEndereco = new Endereco(dtoCadastroEndereco);
        novoEndereco.setUsuario(usuarioLogado);
        Endereco enderecoSalvo = enderecoRepository.save(novoEndereco);

        // Atualiza o usuário com o ID do endereço
        usuarioLogado.setEndereco(enderecoSalvo); // Define o endereço para o usuário
        usuarioRepository.save(usuarioLogado);   // Salva o usuário com o endereço atualizado

        // Pegando a URL para acesso desse item no banco
        var uri = uriComponentsBuilder
                .path("/endereco/{id}")  // Caminho do endpoint da class para a API
                .buildAndExpand(novoEndereco.getId()) // Pegar o ID do novo usuario
                .toUri();

        return ResponseEntity.created(uri).body(new DTODetalhamentoEndereco(novoEndereco));
    }


    public ResponseEntity<DTODetalhamentoEndereco> editarEndereco(Long enderecoId, DTOAlterarEndereco dtoAlterarEndereco, UriComponentsBuilder uriComponentsBuilder){

        // Pega o usuario logado
        Long usuarioId = SecurityUtil.getIdUsuarioAutenticado();

        // Verifica se o endereço existe e pertence ao usuário
        Endereco altEndereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado."));

        // Verifica se é o endereço do Usuario cadastrado
        if (!altEndereco.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não autorizado a editar este endereço.");
        }

        altEndereco.atualizarInformacoes(dtoAlterarEndereco);

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
