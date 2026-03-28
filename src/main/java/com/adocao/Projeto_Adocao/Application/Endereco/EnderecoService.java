package com.adocao.Projeto_Adocao.Application.Endereco;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import com.adocao.Projeto_Adocao.Infra.Exception.BusinessRuleException;
import com.adocao.Projeto_Adocao.Infra.Exception.ResourceNotFoundException;
import com.adocao.Projeto_Adocao.Infra.Security.JWTUserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final UsuarioRepository usuarioRepository;

    public EnderecoResponse getEndereco(JWTUserData jwtUserData) {
        Endereco endereco = enderecoRepository.findByUsuarioId(jwtUserData.id())
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado para o usuário."));
        return EnderecoMapper.toEnderecoResponse(endereco);
    }

    @Transactional
    public EnderecoResponse cadastrarEndereco(EnderecoRequest enderecoRequest, JWTUserData jwtUserData) {
        // Busca o usuário autenticado
        Usuario usuario = usuarioRepository.findById(jwtUserData.id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado no sistema."));

        // Verifica se o usuário já possui endereço
        if (usuario.getEndereco() != null) {
            throw new BusinessRuleException("O usuário já possui um endereço cadastrado. Utilize a rota de edição.");
        }

        // Cria e vincula o endereço ao usuário
        Endereco novoEndereco = EnderecoMapper.toEndereco(enderecoRequest);
        novoEndereco.atualizarUsuario(usuario);
        Endereco enderecoSalvo = enderecoRepository.save(novoEndereco);

        // Atualiza o usuário com o endereço
        usuario.atualizarEndereco(enderecoSalvo);
        usuarioRepository.save(usuario);

        return EnderecoMapper.toEnderecoResponse(enderecoSalvo);
    }

    @Transactional
    public EnderecoResponse editarEndereco(JWTUserData jwtUserData, EnderecoUpdate enderecoUpdate) {
        Endereco endereco = enderecoRepository.findByUsuarioId(jwtUserData.id())
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado para o usuário."));

        endereco.atualizarInformacoes(enderecoUpdate);
        enderecoRepository.save(endereco);
        return EnderecoMapper.toEnderecoResponse(endereco);
    }

    @Transactional
    public EnderecoResponse alterarStatus(JWTUserData jwtUserData, Boolean status) {
        Endereco endereco = enderecoRepository.findByUsuarioId(jwtUserData.id())
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado para o usuário."));
        endereco.alterarStatus(status);
        enderecoRepository.save(endereco);
        return EnderecoMapper.toEnderecoResponse(endereco);
    }
}
