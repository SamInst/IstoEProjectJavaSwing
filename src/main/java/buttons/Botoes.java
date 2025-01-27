package buttons;

import java.awt.*;

import static principals.tools.CorPersonalizada.*;

public class Botoes {
    private static BotaoComSombra botaoComSombra(Color color, String text){
        BotaoComSombra botao = new BotaoComSombra();
        botao.setBackground(color);
        botao.setForeground(WHITE);
        botao.setFocusPainted(false);
        botao.setRippleColor(WHITE);
        botao.setShadowColor(color);
        botao.setFocusPainted(false);
        botao.setText(text);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    public static BotaoComSombra btn_vermelho(String text){
        return botaoComSombra(RED, text);
    }

    public static BotaoComSombra btn_verde(String text){
        return botaoComSombra(GREEN, text);
    }

    public static BotaoComSombra btn_azul(String text){
        return botaoComSombra(BLUE, text);
    }
    public static BotaoComSombra btn_laranja(String text){
        return botaoComSombra(ORANGE, text);
    }
    public static BotaoComSombra btn_cinza(String text){
        return botaoComSombra(GRAY, text);
    }

    public static BotaoComSombra btn_branco(String text){
        var botao = botaoComSombra(WHITE, text);
        botao.setForeground(GRAY);
        return botao;
    }
}
