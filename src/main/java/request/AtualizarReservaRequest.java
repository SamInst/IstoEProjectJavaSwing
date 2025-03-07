package request;

import java.time.LocalDate;
import java.util.List;

public record AtualizarReservaRequest(
        Long reserva_id,
        Long quarto,
        LocalDate data_entrada,
        LocalDate data_saida,
        int quantidade_pessoas,
        List<Long> pessoas,
        List<BuscaReservasResponse.Pagamentos> pagamentos
) {}
