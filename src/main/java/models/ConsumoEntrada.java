package models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsumoEntrada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataHoraConsumo;
    @ManyToOne
    private Entrada entrada;
    @ManyToOne
    private Item item;
    private Integer quantidade;
}

