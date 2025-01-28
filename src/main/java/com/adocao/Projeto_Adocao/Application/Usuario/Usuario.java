package com.adocao.Projeto_Adocao.Application.Usuario;

import com.adocao.Projeto_Adocao.Application.Endereco.Endereco;
import com.adocao.Projeto_Adocao.Security.Roles.Role;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "usuario")
@Entity(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode( of= "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean ativo = true;
    private String nome;
    private String identificador;
    private Character tipo;
    private String email;

    private String senha;

    private String telefone;
    private BigInteger saldo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id", unique = true)
    private Endereco endereco;

    // Controle de acesso do perfil
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_role",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Construtor da class
    public Usuario(DTOCadastroUsuario dados){
        this.nome = dados.nome();
        this.identificador = dados.identificador();
        this.email = dados.email();
        this.senha = dados.senha();
        this.telefone = dados.telefone();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return identificador;
    }
}
