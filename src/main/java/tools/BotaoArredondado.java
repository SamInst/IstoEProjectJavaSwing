package tools;

import javax.swing.*;
import java.awt.*;

public class BotaoArredondado extends JButton {
    private boolean showBorder = false;
    private Color borderColor = Color.BLACK;

    public BotaoArredondado(String texto) {
        super(texto);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    public void setShowBorder(boolean showBorder, Color borderColor) {
        this.showBorder = showBorder;
        this.borderColor = borderColor;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

        if (showBorder) {
            g2.setStroke(new BasicStroke(1));
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}



