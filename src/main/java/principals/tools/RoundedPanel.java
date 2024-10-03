package principals.tools;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int radius;
    private Color backgroundColor;

    public RoundedPanel(int radius, Color backgroundColor) {
        this.radius = radius;
        this.backgroundColor = backgroundColor;
        setOpaque(false);  // Faz com que o fundo seja transparente
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Desenha o fundo arredondado
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Desenha a borda arredondada (opcional)
        g2.setColor(getForeground());
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
    }
}


