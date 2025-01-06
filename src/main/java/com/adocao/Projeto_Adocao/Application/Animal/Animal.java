package com.adocao.Projeto_Adocao.Application.Animal;


import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@Table(name = "animal")
@Entity(name = "animais")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean ativo;

    private String nome;
    private String especie;
    private String cor;

    @Enumerated(EnumType.STRING) // Identifica para o JPA, que é um ENUM de STRING
    private Porte porte;

    private LocalDate nascimento;

    private Boolean castracao;

    private List<String> descricao; // Descrição: vacina, vermifugação, peso, altura a critério de quem colocou para adoção


//    private String observações; ou fica fora da tabela? devo receber isso?

    // Construtor da class --> está sendo usado pelo AnimalController
    public Animal(DTOCadastroAnimal dados) {
        this.ativo = true;
        this.nome = dados.nome();
        this.especie = dados.especie();
        this.cor = dados.cor();
        this.porte= dados.porte();
        this.nascimento = dados.nascimento();

        this.descricao = dados.vacinas();

    }

}
