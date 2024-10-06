package principals.tools;

public class Converter {
    public static String converterTipoPagamento(String tipoPagamento) {
        switch (tipoPagamento) {
            case "0" ->  tipoPagamento = "PIX";
            case "1" -> tipoPagamento = "DINHEIRO";
            case "2" ->  tipoPagamento = "CARTAO DE CREDITO";
            case "3" ->  tipoPagamento = "CARTAO DE DEBITO";
            case "4" ->  tipoPagamento = "TRANSFERENCIA BANCARIA";
            case "5" ->  tipoPagamento = "CARTAO VIRTUAL";
            default ->  tipoPagamento = "DESCONHECIDO";
        }
        return tipoPagamento;
    }
}
