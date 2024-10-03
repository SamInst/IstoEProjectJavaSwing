package response;

import enums.StatusPernoiteEnum;

import java.time.LocalDate;

public record PernoiteResponse (
        Long pernoite_id,
        QuartoResponse quarto,
        LocalDate dataEntrada,
        LocalDate dataSaida,
        StatusPernoiteEnum statusPernoiteEnum){}
