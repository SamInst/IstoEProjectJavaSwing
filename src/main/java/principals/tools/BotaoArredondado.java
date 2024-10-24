package principals.tools;

import javax.swing.*;
import java.awt.*;

public class BotaoArredondado extends JButton {
    private boolean showBorder = false;  // Campo para controlar a exibição da borda
    private Color borderColor = Color.BLACK;  // Campo para armazenar a cor da borda

    public BotaoArredondado(String texto) {
        super(texto);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    public void setShowBorder(boolean showBorder, Color borderColor) {
        this.showBorder = showBorder;
        this.borderColor = borderColor;  // Atualiza a cor da borda
        this.repaint();  // Repinta o componente quando o estado da borda mudar
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);  // Pinta o fundo arredondado

        if (showBorder) {  // Condicionalmente pinta a borda
            g2.setStroke(new BasicStroke(1));
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}



