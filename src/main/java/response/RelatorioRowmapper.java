package response;

import java.time.LocalDateTime;

public record RelatorioRowmapper(
        Long id,
        LocalDateTime data_hora,
        String tipo_pagamento_enum,
        String relatorio,
        Long pernoite_id,
        Long entrada_id,
        Float valor
){}
