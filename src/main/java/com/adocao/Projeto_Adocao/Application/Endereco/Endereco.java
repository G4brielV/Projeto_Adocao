package com.adocao.Projeto_Adocao.Application.Endereco;


import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

@Table(name="endereco")
@Entity(name="enderecos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode (of="id")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean ativo = true;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;

    @OneToOne(mappedBy = "endereco")
    private Usuario usuario;

    // Construtor da class
    public Endereco(DTOCadastroEndereco dtoCadastroEndereco){
        this.rua = dtoCadastroEndereco.rua();
        this.numero = dtoCadastroEndereco.numero();
        this.bairro = dtoCadastroEndereco.bairro();
        this.cidade = dtoCadastroEndereco.cidade();
        this.estado = dtoCadastroEndereco.estado();
    }

    public void atualizarInformacoes(@Valid DTOEditarEndereco novosDados){
        this.rua = novosDados.rua();
        this.numero = novosDados.numero();
        this.bairro = novosDados.bairro();
        this.cidade = novosDados.cidade();
        this.estado = novosDados.estado();
    }

}
