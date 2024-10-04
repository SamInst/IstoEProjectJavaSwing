package principals.tools;

import javax.swing.*;
import java.awt.*;

public class BotaoArredondado extends JButton {

    public BotaoArredondado(String texto) {
        super(texto);
        setContentAreaFilled(false);  // Remove o preenchimento padrão do JButton
        setFocusPainted(false);       // Remove a borda de foco
        setBorderPainted(false);      // Remove a borda padrão
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  // Suaviza as bordas

        // Desenhar o fundo arredondado
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);  // 30, 30 são os raios de arredondamento

        // Não desenhar o texto manualmente. Permitir que o `JButton` faça isso automaticamente
        g2.dispose();

        // Chamar super.paintComponent() ANTES de qualquer desenho personalizado do texto
        super.paintComponent(g);
    }
}

