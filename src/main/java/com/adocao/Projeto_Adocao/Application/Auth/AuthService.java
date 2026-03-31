package com.adocao.Projeto_Adocao.Application.Auth;

import com.adocao.Projeto_Adocao.Application.Usuario.Roles.Role;
import com.adocao.Projeto_Adocao.Application.Usuario.Roles.RoleRepository;
import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioMapper;
import com.adocao.Projeto_Adocao.Application.Usuario.UsuarioRepository;
import com.adocao.Projeto_Adocao.Infra.Exception.BusinessRuleException;
import com.adocao.Projeto_Adocao.Infra.Exception.ForbiddenOperationException;
import com.adocao.Projeto_Adocao.Infra.Exception.ResourceNotFoundException;
import com.adocao.Projeto_Adocao.Infra.Security.PasswordEncryptService;
import com.adocao.Projeto_Adocao.Infra.Security.TokenJWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenJWTService tokenJWTService;
    private final PasswordEncryptService passwordEncryptService;


    public LoginResponse login(LoginRequest loginRequest) {
        // Erro capturado no GlobalExceptionHandler
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.senha());
        Authentication autenticacao = authenticationManager.authenticate(token);
        Usuario usuario = (Usuario) autenticacao.getPrincipal();
        String tokenJWT = tokenJWTService.gerarToken(usuario);
        return new LoginResponse(tokenJWT);
    }

    @Transactional
    public CadastroResponse cadastro(CadastroRequest cadastroRequest){
        if (usuarioRepository.findByCpf(cadastroRequest.cpf()).isPresent()) {
            throw new BusinessRuleException("Já existe um usuário cadastrado com este cpf.");
        }

        Usuario usuario = UsuarioMapper.toUsuario(cadastroRequest);

        Role roleUser = roleRepository.findByNome("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role USER não encontrada no banco!"));
        usuario.adicionarRoles(roleUser);

        String senhaCriptografada = passwordEncryptService.encryptPassword(usuario.getSenha());
        usuario.atualizarSenha(senhaCriptografada);
        usuarioRepository.save(usuario);

        return UsuarioMapper.toLoginResponse(usuario);
    }

    public CadastroResponse reativarConta(LoginRequest loginRequest) {
        Usuario usuario = usuarioRepository.findByCpf(loginRequest.login())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));;

        // 2. Verifica se a conta realmente está desativada (opcional, mas recomendado)
        if (usuario.getAtivo()) throw new BusinessRuleException("Esta conta já está ativa.");

        // 3. Valida a senha manualmente
        String senhaCriptografada = passwordEncryptService.encryptPassword(loginRequest.senha());
        if (senhaCriptografada.matches(usuario.getSenha())) throw new ForbiddenOperationException("Credenciais inválidas.");

        // 4. Reativa a conta e salva no banco
        usuario.alterarStatus(true);
        usuarioRepository.save(usuario);

        return UsuarioMapper.toLoginResponse(usuario);
    }
}
