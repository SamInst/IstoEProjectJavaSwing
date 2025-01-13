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
public class PagamentoPernoite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float valor;
    @ManyToOne
    private Pernoite pernoite;
    private LocalDateTime dataHoraPagamento;
    private String tipoPagamentoEnum;
    private String statusPagamentoEnum;
}

