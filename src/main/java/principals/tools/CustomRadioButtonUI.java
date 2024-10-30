package principals.tools;

import javax.swing.*;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import java.awt.*;

public class CustomRadioButtonUI extends BasicRadioButtonUI {
    private final Color selectedColor;
    private final Color unselectedColor;
    private final Color borderColor;

    public CustomRadioButtonUI(Color selectedColor, Color unselectedColor, Color borderColor) {
        this.selectedColor = selectedColor;
        this.unselectedColor = unselectedColor;
        this.borderColor = borderColor;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define cor de fundo do botão
        if (model.isSelected()) {
            g2.setColor(selectedColor);
        } else {
            g2.setColor(unselectedColor);
        }
        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);

        // Define a borda
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 15, 15);

        // Define o texto
        g2.setColor(model.isSelected() ? Color.WHITE : Color.DARK_GRAY);
        FontMetrics fm = g.getFontMetrics();
        Rectangle textRect = b.getBounds();
        String text = b.getText();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2.drawString(text, (textRect.width - textWidth) / 2, (textRect.height + textHeight) / 2 - 3);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(60, 30); // Ajusta o tamanho do botão
    }
}
