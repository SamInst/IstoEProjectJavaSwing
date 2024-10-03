package response;

import enums.TipoPagamentoEnum;

public record RelatorioResponse(
        Long relatorio_id,
        String relatorio,
        TipoPagamentoEnum tipo_pagamento_enum,
        Float valor,
        QuartoResponse quarto,
        PernoiteResponse pernoite,
        EntradaResponse entrada
){}
