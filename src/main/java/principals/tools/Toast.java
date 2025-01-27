package principals.tools;

import buttons.BotaoComSombra;

import javax.swing.*;
import java.awt.*;

import static javax.swing.BorderFactory.createEmptyBorder;
import static principals.tools.CorPersonalizada.WHITE;

public class Toast extends JDialog {
    private static final long DURATION = 1200;
    private static final int FADE_STEP = 5;
    private static final int FADE_DELAY = 40;

    public Toast(JFrame owner, String message, Color color, ImageIcon icon) {
        super(owner, false);
        setUndecorated(true);
        setOpacity(50);

        BotaoComSombra label = new BotaoComSombra();
        label.setText(" " + message);
        label.setOpaque(true);
        label.setFocusPainted(false);
        label.setForeground(WHITE);
        label.setBackground(color);
        label.setFont(new Font("Dialog", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(createEmptyBorder(5, 10, 5, 10));
        label.setIcon(icon);

        add(label);
        pack();

        int x = (owner.getX() + (owner.getWidth() - getWidth()) / 2);
        int y = (owner.getY() + owner.getHeight() - getHeight() - 50);
        setLocation(x, y);

        iniciarAnimacao();
    }

    private void iniciarAnimacao() {
        setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(DURATION);
                for (int alpha = 180; alpha >= 0; alpha -= FADE_STEP) {
                    SwingUtilities.invokeLater(this::repaint);
                    Thread.sleep(FADE_DELAY);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dispose();
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Teste Toast");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            new Toast(frame, "Relatório Adicionado!", WHITE, Icones.saved);
        });
    }

}

