package enums;

import lombok.Getter;

@Getter
public enum TipoPagamentoEnum {
    SELECIONE(-1),
    PIX(0),
    DINHEIRO(1),
    CARTAO_CREDITO(2),
    CARTAO_DEBITO(3),
    CARTAO_VIRTUAL(4),
    TRANSFERENCIA_BANCARIA(5);

    private final int codigo;

    TipoPagamentoEnum(int codigo) {
        this.codigo = codigo;
    }
}
