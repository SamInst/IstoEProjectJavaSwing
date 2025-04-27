package request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AtualizarReservaRequest(
        Long reserva_id,
        Long quarto,
        LocalDate data_entrada,
        LocalDate data_saida,
        LocalTime horarioPrevistoChegada,
        int quantidade_pessoas
) {}
