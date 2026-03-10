package com.adocao.Projeto_Adocao.Application.Usuario;

import com.adocao.Projeto_Adocao.Application.Auth.CadastroRequest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/*
Teste Unitário do findByID() do JPA
   * Usa do banco em memória: H2DATABASE
   *
 */

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor
class UsuarioRepositoryTest {

    private final UsuarioRepository usuarioRepository;
    private final EntityManager entityManager;

    @Test
    @DisplayName("Existe, deve retornar o usuario")
    void findByTelefoneSuccess() {

        String telefone = "987654321";
        CadastroRequest data = new CadastroRequest("JJ", "11111111111", "xxx@example.com", "senha" , telefone);
        this.createUser(data);

        Optional<Usuario> resultado = this.usuarioRepository.findByTelefone(telefone);

        assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    @DisplayName("NÃO existe, não deve retornar usuario")
    void findByTelefoneError() {

        String telefone = "987654321";

        Optional<Usuario> resultado = this.usuarioRepository.findByTelefone(telefone);

        assertThat(resultado.isEmpty()).isTrue();
    }

    private Usuario createUser(CadastroRequest data){
        Usuario newUser = new Usuario(data);
        System.out.println("AAAAAAAAAAAAA" + newUser);
        this.entityManager.persist(newUser);
        return newUser;
    }

}