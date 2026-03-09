package com.adocao.Projeto_Adocao.Application.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
    UserDetails findByCpf(String cpf);

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByTelefone(String telefone);
}
