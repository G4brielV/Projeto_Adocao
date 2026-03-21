package com.adocao.Projeto_Adocao.Application.Endereco;


import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

@Builder
@Table(name="enderecos")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode (of="id")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Boolean ativo = true;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;


    public void atualizarInformacoes(@Valid EnderecoUpdate novosDados){
        this.rua = novosDados.rua();
        this.numero = novosDados.numero();
        this.bairro = novosDados.bairro();
        this.cidade = novosDados.cidade();
        this.estado = novosDados.estado();
    }

    public void atualizarUsuario(Usuario usuario){
        this.usuario = usuario;
    }

}
