package request;

import java.time.LocalDate;

public record PessoaRequest(
        String nome,
        LocalDate dataNascimento,
        String cpf,
        String rg,
        String email,
        String telefone,
        String pais,
        String estado,
        String municipio,
        String endereco,
        String complemento,
        Boolean hospedado
){}
