package enums;

public enum GeneroEnum {
    MASCULINO(0),
    FEMININO(1),
    OUTRO(2);

    private final int valor;

    GeneroEnum(int valor) {
        this.valor = valor;
    }

    public static GeneroEnum fromCodigo(int codigo) {
        for (GeneroEnum status : GeneroEnum.values()) {
            if (status.valor == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }
}
