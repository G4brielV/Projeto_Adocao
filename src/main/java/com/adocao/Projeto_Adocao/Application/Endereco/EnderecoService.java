package com.adocao.Projeto_Adocao.Application.Endereco;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public EnderecoResponse cadastrarEndereco(EnderecoRequest enderecoRequest,
                                                              Usuario usuario){
        /*Verifica se o usuario ja tem endereço cadastrado
        Qual retorno HTTP? ou retornar so esse runtime*/
        if (usuario.getEndereco() != null) {
            throw new RuntimeException("Usuário já possui um endereço cadastrado.");
        }

        // Cadastra o Endereço
        Endereco novoEndereco = EnderecoMapper.toEndereco(enderecoRequest);
        novoEndereco.atualizarUsuario(usuario);
        Endereco enderecoSalvo = enderecoRepository.save(novoEndereco);

        // Atualiza o usuário com o ID do endereço
        usuario.atualizarEndereco(enderecoSalvo); // Define o endereço para o usuário
        usuarioRepository.save(usuario);   // Salva o usuário com o endereço atualizado

        return EnderecoMapper.toEnderecoResponse(novoEndereco);
    }

    @Transactional
    public EnderecoResponse editarEndereco(Long enderecoId,
                                                           Usuario usuario,
                                                           EnderecoUpdate enderecoUpdate){
        // Pega o Id do usuario logado
        Long usuarioId = usuario.getId();

        /*Verifica se o endereço existe e pertence ao usuário
        Qual retorno HTTP? ou retornar so esse runtime*/
        Endereco altEndereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado."));

        // Verifica se é o endereço do Usuario cadastrado
        if (!altEndereco.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Usuário não autorizado a editar este endereço.");
        }

        altEndereco.atualizarInformacoes(enderecoUpdate);

        // Salva o endereço atualizado
        enderecoRepository.save(altEndereco);

        return EnderecoMapper.toEnderecoResponse(altEndereco);
    }


}
