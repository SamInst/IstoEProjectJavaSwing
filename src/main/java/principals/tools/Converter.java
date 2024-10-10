package principals.tools;

import javax.swing.*;

import static principals.tools.Tool.resizeIcon;

public class Converter {
    public static String converterTipoPagamento(String tipoPagamento) {
       return switch (tipoPagamento) {
            case "0" -> "PIX";
            case "1" -> "DINHEIRO";
            case "2" -> "CARTAO DE CREDITO";
            case "3" -> "CARTAO DE DEBITO";
            case "4" -> "TRANSFERENCIA BANCARIA";
            case "5" -> "CARTAO VIRTUAL";
            default ->  "DESCONHECIDO";
        };
    }

    public static String converterStatusPernoite(String statusPernoite) {
       return switch (statusPernoite) {
            case "0" -> "ATIVO";
            case "1" -> "DIARIA ENCERRADA";
            case "2" -> "FINALIZADO";
            default -> "DESCONHECIDO";
        };
    }

    public static String converterStatusPagamento(String statusPagamento) {
        return switch (statusPagamento){
            case "0" -> "PENDENTE";
            case "1" -> "PAGO";
            default -> "DESCONHECIDO";
        };
    }

    public static ImageIcon converterIconePagamento(String tipoPagamento) {
        return switch (tipoPagamento){
            case "PIX" -> resizeIcon(Icones.pix, 25, 25);
            case "DINHEIRO" -> resizeIcon(Icones.dinheiro, 25, 25);
            case "CARTAO DE CREDITO" -> resizeIcon(Icones.cartao, 25, 25);
            case "CARTAO DE DEBITO" -> resizeIcon(Icones.cartao, 25, 25);
            case "TRANSFERENCIA BANCARIA" -> resizeIcon(Icones.bancario, 25, 25);
            case "CARTAO VIRTUAL" -> resizeIcon(Icones.preco, 25, 25);
            default -> new ImageIcon();
        };
    }
}
