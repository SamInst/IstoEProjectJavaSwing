package request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record BuscaReservasResponse(
            Long reserva_id,
            Long quarto,
            LocalDate data_entrada,
            LocalDate data_saida,
            LocalTime hora_prevista,
            List<Pessoas> pessoas,
            List<Pagamentos> pagamentos,
            Boolean hospedado
    ){
        public record Pessoas(
                Long pessoa_id,
                String nome,
                String telefone,
                boolean representante
        ){}
        public record Pagamentos(
                String descricao,
                Integer tipo_pagamento,
                Float valor_pagamento,
                LocalDateTime data_hora_pagamento
        ){}
}
