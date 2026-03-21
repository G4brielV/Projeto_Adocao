package com.adocao.Projeto_Adocao.Application.Endereco;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository <Endereco, Long> {

    Optional<Endereco> findById (Long id);

    Optional<Endereco> findByUsuarioId (Long usuarioId);
}
