package principals.tools;

import javax.swing.*;
import java.awt.*;

public class LabelArredondado extends JLabel {

    public LabelArredondado(String texto) {
        super(texto);
        setOpaque(true);  // Permitir que a gente desenhe o fundo
        setHorizontalAlignment(SwingConstants.CENTER);  // Centralizar o texto
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  // Suaviza as bordas

        // Desenhar o fundo arredondado
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);  // 30, 30 são os raios de arredondamento

        // Desenhar o texto da label
        super.paintComponent(g);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.setSize(d.width + 20, d.height + 10);
        return d;
    }

    @Override
    public Insets getInsets() {

        return new Insets(15, 25, 15, 25);
    }
}

