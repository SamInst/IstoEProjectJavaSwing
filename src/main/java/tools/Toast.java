package tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.BorderFactory.createEmptyBorder;
import static tools.CorPersonalizada.*;

public class Toast extends JDialog {
    private static final long DURATION = 3500;
    private static final int SLIDE_DELAY = 10;
    private static final int SLIDE_STEP = 5;

    public Toast(JFrame owner, String message, Color color, ImageIcon icon) {
        super(owner, false);
        initToast(message, color, icon);
        int x = owner.getX() + (owner.getWidth() - getWidth()) / 2;
        int y = owner.getY() + 100;
        setLocation(x, y - getHeight() - 20);
        iniciarAnimacao(x, y);
    }

    public Toast(JLayeredPane layeredPane, String message, Color color, ImageIcon icon) {
        super(SwingUtilities.getWindowAncestor(layeredPane), ModalityType.MODELESS);
        if (color == YELLOW) setForeground(DARK_GRAY);
        initToast(message, color, icon);
        Point p = layeredPane.getLocationOnScreen();
        int x = p.x + (layeredPane.getWidth() - getWidth()) / 2;
        int y = p.y + 100;
        setLocation(x, y - getHeight() - 20);
        iniciarAnimacao(x, y);
    }

    private void initToast(String message, Color color, ImageIcon icon) {
        setUndecorated(true);
        setOpacity(1.0f);
        LabelArredondado label = new LabelArredondado(" "+message);
        label.setOpaque(false);
        label.setForeground(WHITE);
        label.setBackground(color);
        if (color == YELLOW) label.setForeground(DARK_GRAY);
        label.setFont(new Font("sansserif", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(createEmptyBorder(5, 10, 5, 15));
        label.setIcon(icon);
        add(label);
        pack();
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

    public static void showToast(JLayeredPane layeredPane, String message, Color color, ImageIcon icon) {
        new Toast(layeredPane, message, color, icon);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Teste Toast");
            frame.setSize(new Dimension(800, 600));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            JButton showToastButton = new JButton("Mostrar Toast");
            showToastButton.setFont(new Font("Dialog", Font.BOLD, 16));
            showToastButton.addActionListener(e -> {
                new Toast(frame, "Toast via JFrame", new Color(7, 164, 121), Icones.search);
            });
            frame.add(showToastButton, BorderLayout.SOUTH);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
