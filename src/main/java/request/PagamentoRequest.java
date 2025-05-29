package request;

public record PagamentoRequest(
        String descricao,
        Integer tipo_pagamento,
        Float valor_pagamento
){}
