package models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataHoraCadastro;
    private String nomeRazaoSocial;
    private String ramo;
    private String cnpj;
    private String email;
    private String telefone;
    private String pais;
    private String estado;
    private String municipio;
    private String endereco;
    private String complemento;
    @OneToMany
    private List<Veiculo> veiculos;
    @OneToMany
    private List<Pessoa> funcionarios;
}

