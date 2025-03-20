package com.adocao.Projeto_Adocao.Application.Animal;


import com.adocao.Projeto_Adocao.Application.Endereco.DTOEditarEndereco;
import com.adocao.Projeto_Adocao.Application.Usuario.Usuario;
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



    // Construtor da class --> está sendo usado pelo AnimalService
    public Animal(DTOCadastroAnimal dtoCadastroAnimal) {
        this.ativo = true;
        this.nome = dtoCadastroAnimal.nome();
        this.raca = dtoCadastroAnimal.raca();
        this.cor = dtoCadastroAnimal.cor();
        this.porte= dtoCadastroAnimal.porte();
        this.nascimento = dtoCadastroAnimal.nascimento();
        this.castracao = dtoCadastroAnimal.castracao();

        this.descricao = dtoCadastroAnimal.descricao();

    }

    public void atualizarInformacoes(@Valid DTOEditarAnimal novosDados){
        this.nome = novosDados.nome();
        this.raca = novosDados.raca();
        this.cor = novosDados.cor();
        this.porte = novosDados.porte();
        this.nascimento = novosDados.nascimento();
        this.castracao = novosDados.castracao();
        this.descricao = novosDados.descricao();
    }

}
