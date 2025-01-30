package principals.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.BorderFactory.createEmptyBorder;
import static principals.tools.CorPersonalizada.WHITE;

public class Toast extends JDialog {
    private static final long DURATION = 1700;
    private static final int SLIDE_DELAY = 10;
    private static final int SLIDE_STEP = 5;

    public Toast(JFrame owner, String message, Color color, ImageIcon icon) {
        super(owner, false);
        setUndecorated(true);
        setOpacity(1.0f);

        LabelArredondado label = new LabelArredondado(message);
        label.setText(" " + message);
        label.setOpaque(false);
        label.setForeground(WHITE);
        label.setBackground(color);
        label.setFont(new Font("Dialog", Font.BOLD, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(createEmptyBorder(5, 10, 5, 15));
        label.setIcon(icon);

        add(label);
        pack();

        int x = (owner.getX() + (owner.getWidth() - getWidth()) / 2);
        int y = owner.getY() + 100;
        setLocation(x, y - getHeight() - 20);

        iniciarAnimacao(x, y);
    }

    private void iniciarAnimacao(int targetX, int targetY) {
        setVisible(true);
        Timer slideIn = new Timer(SLIDE_DELAY, null);
        slideIn.addActionListener(new ActionListener() {
            int currentY = getY();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentY < targetY) {
                    currentY += SLIDE_STEP;
                    setLocation(targetX, currentY);
                } else {
                    setLocation(targetX, targetY);
                    slideIn.stop();
                    iniciarEspera();
                }
            }
        });
        slideIn.start();
    }

    private void iniciarEspera() {
        Timer waitTimer = new Timer((int) DURATION, e -> iniciarAnimacaoOut());
        waitTimer.setRepeats(false);
        waitTimer.start();
    }

    private void iniciarAnimacaoOut() {
        Timer slideOut = new Timer(SLIDE_DELAY, null);
        slideOut.addActionListener(new ActionListener() {
            int currentY = getY();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentY > getOwner().getY() + 50 - getHeight() - 20) {
                    currentY -= SLIDE_STEP;
                    setLocation(getX(), currentY);
                } else {
                    slideOut.stop();
                    dispose();
                }
            }
        });
        slideOut.start();
    }

    private static class LabelArredondado extends JLabel {
        private static final int ARC_WIDTH = 20;
        private static final int ARC_HEIGHT = 20;

        public LabelArredondado(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_WIDTH, ARC_HEIGHT);
            super.paintComponent(g);
        }

        @Override
        public void setOpaque(boolean isOpaque) {
            super.setOpaque(false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Teste Toast");
            frame.setSize(new Dimension(800, 600));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            JButton showToastButton = new JButton("Mostrar Toast");
            showToastButton.setFont(new Font("Dialog", Font.BOLD, 16));
            showToastButton.addActionListener(e -> OptionPane.ok(frame, showToastButton.getText()));

            frame.add(showToastButton, BorderLayout.SOUTH);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
