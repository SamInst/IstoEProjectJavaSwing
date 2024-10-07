package enums;

import lombok.Getter;

@Getter
public enum StatusPernoiteEnum {
    ATIVO(0),
    DIARIA_ENCERRADA(1),
    FINALIZADO(2);

    private final int value;

    StatusPernoiteEnum(int value) {
        this.value = value;
    }

}
