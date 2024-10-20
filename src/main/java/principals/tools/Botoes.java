package principals.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;

public class Botoes {

    public static JButton botaoEstilizado(String titulo, int tamanhoTitulo, String pathIcon, int larguraIcone, int alturaIcone, Color color,  int larguraBotao, int alturaBotao) {
        ImageIcon icone = new ImageIcon(pathIcon);

        JButton btnAdicionar = new JButton(titulo, Tool.resizeIcon(icone, larguraIcone, alturaIcone));
        btnAdicionar.setFont(new Font("Inter", Font.BOLD, tamanhoTitulo));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setBackground(color);
        btnAdicionar.setFocusPainted(false);
        btnAdicionar.setBorderPainted(false);
        btnAdicionar.setContentAreaFilled(false);
        btnAdicionar.setOpaque(false);
        btnAdicionar.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnAdicionar.setIconTextGap(7);
        btnAdicionar.setMargin(new Insets(4, 10, 5, 10));
        btnAdicionar.setPreferredSize(new Dimension(larguraBotao, alturaBotao));
        btnAdicionar.setMinimumSize(new Dimension(larguraBotao, alturaBotao));

        btnAdicionar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 15)
        ));

        btnAdicionar.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(btnAdicionar.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 40, 40);

                super.paint(g, c);
            }
        });

        Color hoverColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 178);
        btnAdicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdicionar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAdicionar.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAdicionar.setBackground(color);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                String originalText = btnAdicionar.getText();
                Icon originalIcon = btnAdicionar.getIcon();

                Icon loadingIcon = resizeGif(larguraIcone, alturaIcone);
                btnAdicionar.setIcon(loadingIcon);
                btnAdicionar.setEnabled(false);

                Executors.newSingleThreadExecutor().submit(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } finally {
                        SwingUtilities.invokeLater(() -> {
                            btnAdicionar.setText(originalText);
                            btnAdicionar.setIcon(originalIcon);
                            btnAdicionar.setEnabled(true);
                        });
                    }
                });
            }

        });

        return btnAdicionar;
    }

    private static ImageIcon resizeGif(int width, int height) {
        ImageIcon gifIcon = new ImageIcon("src/main/resources/icons/loading2.gif");
        Image gifImage = gifIcon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        return new ImageIcon(gifImage);
    }
}
