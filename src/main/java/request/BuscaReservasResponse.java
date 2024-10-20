package request;

import java.time.LocalDate;
import java.util.List;

public record BuscaReservasResponse(
        LocalDate data,
        List<Reservas> reservas
) {
    public record Reservas(
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
                String tipo_pagamento,
                Float valor_pagamento,
                String data_hora_pagamento
        ){}
    }
}
