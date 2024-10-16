package request;

public record PagamentoRequest(
        String tipo_pagamento,
        Float valor_pagamento
){}
