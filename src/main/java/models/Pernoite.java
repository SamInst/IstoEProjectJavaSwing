package models;

import enums.StatusPernoiteEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pernoite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Quarto quarto;
    private LocalTime horaChegada;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private StatusPernoiteEnum statusPernoiteEnum;
}

