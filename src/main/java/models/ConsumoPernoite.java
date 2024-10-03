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
public class ConsumoPernoite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataHoraConsumo;
    @ManyToOne
    private Pernoite pernoite;
    @OneToOne
    private Item item;
    private Integer quantidade;

}

