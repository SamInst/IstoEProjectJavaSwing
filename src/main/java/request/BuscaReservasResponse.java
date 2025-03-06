package request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BuscaReservasResponse(
            Long reserva_id,
            Long quarto,
            LocalDate data_entrada,
            LocalDate data_saida,
            List<Pessoas> pessoas,
            List<Pagamentos> pagamentos
    ){
        public record Pessoas(
                Long pessoa_id,
                String nome,
                String telefone
        ){}
        public record Pagamentos(
                String descricao,
                String tipo_pagamento,
                Float valor_pagamento,
                LocalDateTime data_hora_pagamento
        ){}
}
