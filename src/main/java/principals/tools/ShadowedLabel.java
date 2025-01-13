package principals.tools;

import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class ShadowedLabel extends JLabel {
    @Setter
    private Color shadowColor = Color.BLACK;
    private int shadowOffsetX = 1;
    private int shadowOffsetY = 1;

    public ShadowedLabel(String text) {
        super(text);
    }

    public void setShadowOffset(int offsetX, int offsetY) {
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(shadowColor);
        g2.drawString(getText(), getInsets().left + shadowOffsetX, getInsets().top + shadowOffsetY + g2.getFontMetrics().getAscent());

        g2.setColor(getForeground());
        g2.drawString(getText(), getInsets().left, getInsets().top + g2.getFontMetrics().getAscent());

        g2.dispose();
    }
}

