package models;


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
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Quarto quarto;

    private LocalDate dataEntrada;
    private LocalDate dataSaida;

    @ManyToMany
    private List<Pessoa> pessoas;

    @OneToMany
    private List<PagamentoPernoite> pagamentoPernoites;

    // Getters and Setters
}

