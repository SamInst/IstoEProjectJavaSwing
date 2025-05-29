package request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AdicionarReservasRequest(
        Long quarto,
        LocalDate data_entrada,
        LocalDate data_saida,
        Integer quantidade_pessoas,
        LocalTime horario_previsto,
        List<PessoaRepresentante> pessoas,
        List<PagamentoRequest> pagamentos
){
    public record PessoaRepresentante(
            Long id,
            Boolean representante
    ) {}
}
