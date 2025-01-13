package models;

import enums.StatusPagamentoEnum;
import enums.TipoPagamentoEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Diaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Float valorDiaria;
    private Float total;
    private Integer numero_diaria;
    private TipoPagamentoEnum tipoPagamentoEnum;
    private StatusPagamentoEnum statusPagamentoEnum;

    @ManyToOne
    private Pernoite pernoite;

    @OneToMany
    private List<Pessoa> hospedes;

    @OneToMany
    private List<ConsumoPernoite> consumoPernoite;


}

