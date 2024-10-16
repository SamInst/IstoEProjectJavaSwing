package principals.tools;

import javax.swing.*;
import java.awt.*;

public class PanelArredondado extends JPanel {

    public PanelArredondado() {
        setOpaque(false);  // Torna o fundo transparente para que possamos desenhar o nosso próprio fundo
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Chamando super.paintComponent() para garantir que qualquer desenho de fundo padrão seja feito corretamente

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
        g2.dispose();
    }
}

