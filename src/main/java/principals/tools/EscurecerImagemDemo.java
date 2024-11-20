package principals.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EscurecerImagemDemo {

    public static ImageIcon escurecerImagem(ImageIcon originalIcon, float darkeningFactor) {
        Image originalImage = originalIcon.getImage();
        BufferedImage bufferedImage = new BufferedImage(
                originalIcon.getIconWidth(),
                originalIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);

        g2d.setComposite(AlphaComposite.SrcOver.derive(darkeningFactor));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        g2d.dispose();

        return new ImageIcon(bufferedImage);
    }

    public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
}

