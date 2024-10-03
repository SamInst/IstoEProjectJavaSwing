package principals.tools;

import javax.swing.*;
import java.awt.*;

public class RelatorioBoxPanel extends JPanel {
    private int radius;
    private Color borderColor;

    public RelatorioBoxPanel(int radius, Color borderColor) {
        this.radius = radius;
        this.borderColor = borderColor;
        setOpaque(false);  // Faz o fundo ser transparente para que a borda seja desenhada
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Desenha o fundo branco arredondado
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Desenha a borda arredondada
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
    }
}
