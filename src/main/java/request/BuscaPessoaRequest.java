package request;

public record BuscaPessoaRequest(
        Long id,
        String nome,
        String cpf
) {}

