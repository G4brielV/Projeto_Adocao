package com.adocao.Projeto_Adocao.Application.Animal;


import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

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

    public void inativar(){
        this.ativo = false;
    }

    public void ativar(){
        this.ativo = true;
    }

}
