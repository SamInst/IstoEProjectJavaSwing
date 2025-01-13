package principals.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class LabelRedondo extends JLabel {

    public LabelRedondo(String texto) {
        super(texto);
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        int size = Math.min(getWidth(), getHeight());
        int xOffset = (getWidth() - size) / 2;
        int yOffset = (getHeight() - size) / 2;

        Shape circle = new Ellipse2D.Double(xOffset, yOffset, size, size);
        g2.setClip(circle);

        if (getIcon() != null) {
            Icon icon = getIcon();
            BufferedImage highResImage = new BufferedImage(size * 2, size * 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gHighRes = highResImage.createGraphics();
            gHighRes.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gHighRes.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            icon.paintIcon(this, gHighRes, 0, 0);

            BufferedImage scaledImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gScaled = scaledImage.createGraphics();
            gScaled.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gScaled.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            gScaled.drawImage(highResImage, 0, 0, size, size, null);

            g2.drawImage(scaledImage, xOffset, yOffset, size, size, null);

            gHighRes.dispose();
            gScaled.dispose();
        } else {
            g2.setColor(getBackground());
            g2.fill(circle);
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        int size = Math.max(d.width, d.height);
        return new Dimension(size, size);
    }

    @Override
    public Insets getInsets() {
        return new Insets(15, 15, 15, 15);
    }
}
