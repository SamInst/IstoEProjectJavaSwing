package response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PernoiteResponse (
        Long pernoite_id,
        Boolean ativo,
        Long quarto,
        LocalTime hora_chegada,
        LocalDate data_entrada,
        LocalDate data_saida,
        Float valor_total,
        Integer quantidade_pessoas,
        String status_pernoite,
        List<DiariaResponse> diarias){}
