package response;

import java.time.LocalTime;
import java.util.List;

public record RelatoriosResponse(
        Float total,
        List<Relatorios> relatorios

) {
    public record Relatorios (
            String data,
            Float total_do_dia,
            List<RelatorioDoDia> relatorioDoDia

    ){
        public record RelatorioDoDia(
                Long relatorio_id,
                Long quarto_id,
                LocalTime horario,
                String relatorio,
                String tipo_pagamento,
                Float valor
        ) {}
    }
}
