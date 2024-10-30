package request;

import java.time.LocalDate;

public record PessoaRequest(
        String nome,
        LocalDate dataNascimento,
        String cpf,
        String rg,
        String email,
        String telefone,
        Long pais,
        Long estado,
        Long municipio,
        String endereco,
        String complemento,
        Boolean hospedado,
        Integer vezesHospedado,
        Boolean clienteNovo
){}
