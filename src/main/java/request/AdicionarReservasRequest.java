package request;

import java.time.LocalDate;
import java.util.List;

public record AdicionarReservasRequest(
        Long quarto,
        LocalDate data_entrada,
        LocalDate data_saida,
        Integer quantidade_pessoas,
        List<Long> pessoas,
        List<PagamentoRequest> pagamentos
){}
