package response;

import java.time.LocalDate;
import java.time.LocalTime;

public record BuscaPernoiteResponse(
        Long pernoite_id,
        Boolean ativo,
        Long quarto,
        LocalTime hora_chegada,
        LocalDate data_entrada,
        LocalDate data_saida,
        Float valor_total,
        Integer quantidade_pessoas,
        Integer quantidade_diarias,
        Integer quantidade_consumo,
        String status_pernoite,
        Representante representante
) {
    public record Representante(
            Long id,
            String nome,
            String telefone
    ){}
}
