package models;

import enums.TipoPagamentoEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Relatorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataHora;
    private TipoPagamentoEnum tipoPagamentoEnum;
    private String relatorio;
    @ManyToOne
    private Pernoite pernoite;
    @ManyToOne
    private Entrada entrada;
    private Float valor;
}

