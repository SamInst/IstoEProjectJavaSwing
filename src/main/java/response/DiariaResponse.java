package response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DiariaResponse(
        Long diaria_id,
        LocalDate data_entrada,
        LocalDate data_saida,
        Float valor_diaria,
        List<Pagamento> pagamento,
        List<Consumo> consumo,
        List<Pessoa> pessoa
) {
    public record Pagamento(
            Long pagamento_id,
            LocalDateTime data_hora_pagamento,
            String tipo_pagamento,
            String status_pagamento,
            Float valor_pagamento
    ){}

    public record Consumo(
            Float total_consumo,
            List<Itens> itens
    ){
        public record Itens(
                LocalDateTime data_hora_consumo,
                Long item_id,
                String item,
                Float valor_item
        ){}
    }

    public record Pessoa(
            Long pessoa_id,
            String nome,
            String cpf,
            String telefone
    ){}
}
