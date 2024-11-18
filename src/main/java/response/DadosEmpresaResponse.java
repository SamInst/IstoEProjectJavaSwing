package response;

import request.BuscaPessoaRequest;

import java.util.List;

public record DadosEmpresaResponse(
        Long id,
        String nomeEmpresa,
        String cnpj,
        String telefone,
        String email,
        String endereco,
        String bairro,
        String cep,
        String numero,
        String complemento,
        Objeto pais,
        Objeto estado,
        Objeto municipio,
        List<BuscaPessoaRequest> pessoasVinculadas
) {}

