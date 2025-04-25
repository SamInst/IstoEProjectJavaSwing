package response;

import java.time.LocalDate;

public record PessoaResponse(
        Long id,
        String data_hora_cadastro,
        String nome,
        LocalDate data_nascimento,
        String cpf,
        String rg,
        String email,
        String telefone,
        Objeto pais,
        Objeto estado,
        Objeto municipio,
        String endereco,
        String complemento,
        Boolean hospedado,
        Boolean clienteNovo,
        Integer vezes_hospedado,
        String cep,
        String bairro,
        Integer idade,
        String numero,
        Integer sexo,
        Boolean representante
) {}

