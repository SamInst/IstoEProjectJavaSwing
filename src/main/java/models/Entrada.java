package models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Entrada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Quarto quarto;

    private LocalDate data;
    private LocalTime horaEntrada;
    private LocalTime horaSaida;

    @ManyToMany
    private List<Pessoa> pessoas;

    @OneToMany
    private List<PagamentoPernoite> pagamentoPernoites;

    @OneToMany
    private List<ConsumoEntrada> consumos;
}

