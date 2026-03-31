package com.adocao.Projeto_Adocao.Application.Animal;

import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Table(name = "animais")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private Boolean ativo = true;
    private String nome;
    private String raca;
    private String cor;

    @Enumerated(EnumType.STRING) // Identifica para o JPA, que é um ENUM de STRING
    private Porte porte;
    private LocalDate nascimento;
    private Boolean castracao = false;

    @Column(columnDefinition = "TEXT")
    private String descricao; // Descrição: vacina, vermifugação, peso, altura a critério de quem colocou para adoção


    // Relacionamento com Usuario (muitos animais para um usuário)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) // Coluna usuario_id na tabela animal
    private Usuario usuario;


    public void atualizarInformacoes(@Valid AnimalUpdate novosDados){
        this.nome = novosDados.nome();
        this.raca = novosDados.raca();
        this.cor = novosDados.cor();
        this.porte = novosDados.porte();
        this.nascimento = novosDados.nascimento();
        this.castracao = novosDados.castracao();
        this.descricao = novosDados.descricao();
    }

    public void alterarStatus(Boolean status){
        this.ativo = status;
    }


}
