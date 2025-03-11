package buttons;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import lombok.Getter;
import shadow.ShadowRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;

public class ShadowButton extends JButton {

    private JDialog dialog;

    public void setRound(int round) {
        this.round = round;
        createImageShadow();
        repaint();
    }

    public void closeJDialog() {
        dialog.dispose();
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
        createImageShadow();
        repaint();
    }

    public void setRippleColor(Color color) {
        rippleEffect.setRippleColor(color);
    }

    @Getter
    private int round = 10;
    @Getter
    private Color shadowColor = new Color(170, 170, 170);
    private BufferedImage imageShadow;
    private final Insets shadowSize = new Insets(2, 5, 8, 5);
    private final RippleEffect rippleEffect = new RippleEffect(this);

    public ShadowButton() {
        setBorder(new EmptyBorder(10, 12, 15, 12));
        setContentAreaFilled(false);
        setBackground(new Color(255, 255, 255));
        setForeground(new Color(80, 80, 80));
        rippleEffect.setRippleColor(new Color(220, 220, 220));
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double width = getWidth() - (shadowSize.left + shadowSize.right);
        double height = getHeight() - (shadowSize.top + shadowSize.bottom);
        double x = shadowSize.left;
        double y = shadowSize.top;
        g2.drawImage(imageShadow, 0, 0, null);
        g2.setColor(getBackground());
        Area area = new Area(new RoundRectangle2D.Double(x, y, width, height, round, round));
        g2.fill(area);
        rippleEffect.reder(grphcs, area);
        g2.dispose();
        super.paintComponent(grphcs);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        createImageShadow();
    }

    private void createImageShadow() {
        int height = getHeight();
        int width = getWidth();
        if (width > 0 && height > 0) {
            imageShadow = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = imageShadow.createGraphics();
            BufferedImage img = createShadow();
            if (img != null) {
                g2.drawImage(createShadow(), 0, 0, null);
            }
            g2.dispose();
        }
    }

    private BufferedImage createShadow() {
        int width = getWidth() - (shadowSize.left + shadowSize.right);
        int height = getHeight() - (shadowSize.top + shadowSize.bottom);
        if (width > 0 && height > 0) {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fill(new RoundRectangle2D.Double(0, 0, width, height, round, round));
            g2.dispose();
            return new ShadowRenderer(5, 0.3f, shadowColor).createShadow(img);
        } else {
            return null;
        }
    }

    public void showPopupWithButtons(ShadowButton... buttons) {
        dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), Dialog.ModalityType.MODELESS);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0)); // Transparent background

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setBackground(Color.WHITE);

        for (ShadowButton button : buttons) {
            panel.add(button);
        }

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setLocation(dialog.getX(), dialog.getY() + this.getHeight());
        dialog.setVisible(true);
    }

    public void enableHoverEffect() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(getBackground().brighter());
            }
        });
        setCursor(getPredefinedCursor(HAND_CURSOR));
    }
}
