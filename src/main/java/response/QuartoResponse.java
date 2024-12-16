package response;

import enums.StatusQuartoEnum;

import java.util.List;

public record QuartoResponse(
        Long quarto_id,
        String descricao,
        Integer quantidade_pessoas,
        StatusQuartoEnum status_quarto_enum,
        Integer qtd_cama_casal,
        Integer qtd_cama_solteiro,
        Integer qtd_cama_beliche,
        Integer qtd_rede,
        Categoria categoria
) {
    public record Categoria(
            Long categoria_id,
            String categoria,
            List<ValorPessoa> valorPessoaList
    ){
        public record ValorPessoa(
                Integer qtd_pessoa,
                Float valor
        ){}
    }
}
