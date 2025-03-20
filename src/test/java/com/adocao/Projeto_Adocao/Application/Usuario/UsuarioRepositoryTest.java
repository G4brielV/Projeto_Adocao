package com.adocao.Projeto_Adocao.Application.Usuario;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

/*
Teste Unitário do findByID() do JPA
   * Usa do banco em memória: H2DATABASE
   *
 */

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Existe, deve retornar o usuario")
    void findByTelefoneSuccess() {

        String telefone = "987654321";
        DTOCadastroUsuario data = new DTOCadastroUsuario("JJ", "11111111111", "xxx@example.com", "senha" , telefone);
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

    private Usuario createUser(DTOCadastroUsuario data){
        Usuario newUser = new Usuario(data);
        newUser.setTipo('P');
        System.out.println("AAAAAAAAAAAAA" + newUser);
        this.entityManager.persist(newUser);
        return newUser;
    }

}