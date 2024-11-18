package request;

import java.util.List;

public record AdicionarEmpresaRequest (
        String nomeEmpresa,
        String cnpj,
        String telefone,
        String email,
        String endereco,
        String cep,
        String numero,
        String complemento,
        Long pais,
        Long estado,
        Long municipio,
        String bairro,
        List<Long> pessoasVinculadas
) {}

