package request;

import enums.TipoPagamentoEnum;

public record RelatorioRequest(
   String relatorio,
   TipoPagamentoEnum tipo_pagamento_enum,
   Long quarto_id,
   Float valor
) {}
