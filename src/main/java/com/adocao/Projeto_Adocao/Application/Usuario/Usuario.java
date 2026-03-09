package com.adocao.Projeto_Adocao.Application.Usuario;

import com.adocao.Projeto_Adocao.Application.Animal.Animal;
import com.adocao.Projeto_Adocao.Application.Endereco.Endereco;
import com.adocao.Projeto_Adocao.Application.Usuario.Roles.Role;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@Table(name ="usuario")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of= "id")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*Para o builder identificar*/
    @Builder.Default
    private Boolean ativo = true;

    private String nome;
    private String cpf;
    private String email;

    private String senha;

    private String telefone;
    private BigInteger saldo;

    // Relacionamento com Endereco (um usuário so pode ter um endereço)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id", unique = true)
    private Endereco endereco;

    // Relacionamento com Animal (um usuário pode ter vários animais)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Animal> animais = new ArrayList<>();

    // Controle de acesso do perfil
    /*Para que Builder coloque uma lista vazia e não null*/
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_role",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNome()))
                .collect(Collectors.toList());
    }

    public void desativar() {
        this.ativo = false;
    }

    public void atualizarSenha(String senhaCriptografada) {
        this.senha = senhaCriptografada;
    }

    public void atualizarEndereco(Endereco endereco) {
        this.endereco = endereco;
    }


    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return cpf;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return this.ativo; }
}
