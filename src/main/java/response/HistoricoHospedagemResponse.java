package response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record HistoricoHospedagemResponse(
        Long quarto_id,
        PessoaResponse pessoa_principal,
        LocalDate checkin,
        LocalDate checkout,
        List<Diaria> diariaList
){
    public record Diaria(
            LocalDate checkin,
            LocalDate checkout,
            Float totalDiaria,
            List<PessoaResponse> acompanhantes,
            List<Pagamento> pagamentos
    ){
        public record Pagamento(
                LocalDateTime data_hora_pagamento,
                String tipo_pagamento,
                String status_pagamento,
                Float valor_pagamento
        ){}
    }
}
