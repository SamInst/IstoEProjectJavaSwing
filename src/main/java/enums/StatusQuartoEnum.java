package enums;

import lombok.Getter;

@Getter
public enum StatusQuartoEnum {
    OCUPADO(1),
    DISPONIVEL(2),
    RESERVADO(3),
    LIMPEZA(4),
    DIARIA_ENCERRADA(5),
    MANUTENCAO(6);

    private final int codigo;

    StatusQuartoEnum(int codigo) {
        this.codigo = codigo;
    }

    public static StatusQuartoEnum fromCodigo(int codigo) {
        for (StatusQuartoEnum status : StatusQuartoEnum.values()) {
            if (status.getCodigo() == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }


}
