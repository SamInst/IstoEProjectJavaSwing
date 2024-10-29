package principals.tools;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JComboBoxArredondado<E> extends JComboBox<E> {

    private float espessuraBorda = 0.5f;
    private Color corBorda = Color.GRAY;

    public JComboBoxArredondado() {
        setOpaque(false);
        setUI(new ComboBoxUIArredondada());
        setBackground(Color.WHITE);
        setForeground(Cor.CINZA_ESCURO.brighter());
        setPreferredSize(new Dimension(200, 40));
        setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 5));

        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(true);
                if (isSelected) {
                    label.setBackground(new Color(184, 207, 229));
                    label.setForeground(Cor.CINZA_ESCURO);
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.GRAY);
                }
                label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return label;
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arcWidth = 20;
        int arcHeight = 20;
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke(espessuraBorda));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);
        g2.dispose();
        super.paintComponent(g);
    }

    private class ComboBoxUIArredondada extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton() {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Cor.CINZA_ESCURO.brighter());

                    int w = getWidth();
                    int h = getHeight();
                    int size = Math.min(w, h) / 2;
                    int x = (w - size) / 2;
                    int y = (h - size) / 2;

                    g2.fillPolygon(new int[]{x, x + size, x + size / 2}, new int[]{y, y, y + size / 2}, 3);
                    g2.dispose();
                }
            };
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setPreferredSize(new Dimension(40, 20));

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(Color.BLUE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                  button.setBackground(Cor.CINZA_ESCURO.brighter());
                }
            });
            return button;
        }

        @Override
        public void configureArrowButton() {
            super.configureArrowButton();
            arrowButton.setBounds(getWidth() - 25, 5, 20, getHeight() - 10); // Ajuste manual da posição do botão de seta
        }
    }

    public void setEspessuraBorda(float espessuraBorda) {
        this.espessuraBorda = espessuraBorda;
        repaint();
    }

    public void setCorBorda(Color corBorda) {
        this.corBorda = corBorda;
        repaint();
    }
}
