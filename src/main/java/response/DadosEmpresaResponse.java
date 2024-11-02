package response;

import request.BuscaPessoaRequest;

import java.util.List;

public record DadosEmpresaResponse(
        String nomeEmpresa,
        String cnpj,
        String telefone,
        String email,
        String endereco,
        String cep,
        String numero,
        String complemento,
        String pais,
        String estado,
        String municipio,
        List<BuscaPessoaRequest> pessoasVinculadas
) {}

