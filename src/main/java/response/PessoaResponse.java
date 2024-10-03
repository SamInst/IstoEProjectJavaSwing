package response;

public record PessoaResponse(
        Long id,
        String data_hora_cadastro,
        String nome,
        String data_nascimento,
        String cpf,
        String rg,
        String email,
        String telefone,
        String pais,
        String estado,
        String municipio,
        String endereco,
        String complemento,
        Boolean hospedado,
        Integer vezes_hospedado
) {}

