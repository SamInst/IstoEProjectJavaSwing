package buttons;

import java.awt.*;

import static tools.CorPersonalizada.*;

public class Botoes {
    private static ShadowButton botaoComSombra(Color color, String text){
        ShadowButton botao = new ShadowButton();
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

    public static ShadowButton btn_vermelho(String text){
        return botaoComSombra(RED, text);
    }

    public static ShadowButton btn_verde(String text){
        return botaoComSombra(GREEN, text);
    }

    public static ShadowButton btn_azul(String text){
        return botaoComSombra(BLUE, text);
    }
    public static ShadowButton btn_laranja(String text){
        return botaoComSombra(ORANGE, text);
    }
    public static ShadowButton btn_cinza(String text){
        return botaoComSombra(GRAY, text);
    }

    public static ShadowButton btn_branco(String text){
        var botao = botaoComSombra(WHITE, text);
        botao.setForeground(GRAY);
        return botao;
    }

    public static ShadowButton btn_backgroung(String text){
        var botao = botaoComSombra(BACKGROUND_GRAY, text);
        botao.setForeground(GRAY);
        return botao;
    }
}
