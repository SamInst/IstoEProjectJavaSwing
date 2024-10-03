package models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataHoraCadastro;
    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String rg;
    private String email;
    private String telefone;
    private String pais;
    private String estado;
    private String municipio;
    private String endereco;
    private String complemento;
    private Boolean hospedado;
    private Integer vezesHospedado;
    @OneToMany
    private List<Veiculo> veiculos;
}

