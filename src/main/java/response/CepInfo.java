package response;

public record CepInfo(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String unidade,
        String estado,
        String ibge,
        String gia,
        String ddd,
        String siafi
) {}

