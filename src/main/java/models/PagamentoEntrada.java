package models;

import enums.StatusPagamentoEnum;
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
public class PagamentoEntrada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float valor;
    @ManyToOne
    private Entrada entrada;
    private LocalDateTime dataHoraPagamento;
    private TipoPagamentoEnum tipoPagamentoEnum;
    private StatusPagamentoEnum statusPagamentoEnum;
}

