package request;

import java.time.LocalDate;
import java.util.List;

public record PernoiteRequest(
        Long quarto_id,
        LocalDate dataEntrada,
        LocalDate dataSaida,
        Integer quantidade_de_pessoas,
        List<Long> pessoas
){}
