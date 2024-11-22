package request;

import java.time.LocalDate;

public record PessoaRequest(
        String path_foto,
        String nome,
        LocalDate dataNascimento,
        Integer idade,
        Integer sexo,
        String cpf,
        String rg,
        String email,
        String telefone,
        Long pais,
        Long estado,
        Long municipio,
        String cep,
        String endereco,
        String bairro,
        String complemento,
        Boolean hospedado,
        Integer vezesHospedado,
        Boolean clienteNovo,
        String numero
){}
