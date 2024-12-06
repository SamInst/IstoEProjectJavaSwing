package request;

import enums.StatusQuartoEnum;

public record AtualizarDadosQuartoRequest(
        Long id,
        String descricao,
        Integer quantidadePessoas,
        Integer qtdCamaCasal,
        Integer qtdCamaSolteiro,
        Integer qtdCamaBeliche,
        Integer qtdRede,
        Long categoriaId,
        StatusQuartoEnum statusQuarto
) {}

