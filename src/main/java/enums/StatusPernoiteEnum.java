package enums;

import lombok.Getter;

@Getter
public enum StatusPernoiteEnum {
    ATIVO(0),
    DIARIA_ENCERRADA(1),
    FINALIZADOS(2),
    CANCELADOS(3);

    private final int value;

    StatusPernoiteEnum(int value) {
        this.value = value;
    }

}
