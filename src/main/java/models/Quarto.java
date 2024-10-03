package models;


import enums.StatusQuartoEnum;
import enums.TipoQuartoEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quarto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private Integer quantidadePessoas;
    private TipoQuartoEnum tipoQuartoEnum;
    private StatusQuartoEnum statusQuartoEnum;
}

