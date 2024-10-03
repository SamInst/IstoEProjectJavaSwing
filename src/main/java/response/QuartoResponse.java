package response;

import enums.StatusQuartoEnum;
import enums.TipoQuartoEnum;

public record QuartoResponse(
        Long quarto_id,
        String descricao,
        Integer quantidade_pessoas,
        TipoQuartoEnum tipo_quarto_enum,
        StatusQuartoEnum status_quarto_enum
) {
}
