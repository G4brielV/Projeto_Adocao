package com.adocao.Projeto_Adocao.Application.Endereco;


import com.adocao.Projeto_Adocao.Application.Usuario.DTOCadastroUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @GeneratedValue
    private Long id;

    private Boolean ativo = true;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;

    // Construtor da class
    public Endereco(DTOCadastroEndereco dtoCadastroEndereco){
        this.rua = dtoCadastroEndereco.rua();
        this.numero = dtoCadastroEndereco.numero();
        this.bairro = dtoCadastroEndereco.bairro();
        this.cidade = dtoCadastroEndereco.cidade();
        this.estado = dtoCadastroEndereco.estado();
    }

}
