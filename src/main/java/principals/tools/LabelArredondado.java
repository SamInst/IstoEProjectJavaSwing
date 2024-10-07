package principals.tools;

import javax.swing.*;
import java.awt.*;

public class LabelArredondado extends JLabel {

    public LabelArredondado(String texto) {
        super(texto);
        setOpaque(false);  // Permitir que a gente desenhe o fundo
        setHorizontalAlignment(SwingConstants.CENTER);  // Centralizar o texto
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  // Suaviza as bordas

        // Desenhar o fundo arredondado
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);  // 30, 30 são os raios de arredondamento

        // Desenhar o texto da label
        super.paintComponent(g);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        // Ajustar o tamanho preferido da label, se necessário
        Dimension d = super.getPreferredSize();
        d.setSize(d.width + 20, d.height + 10);  // Adicionar espaço para as bordas arredondadas
        return d;
    }

    @Override
    public Insets getInsets() {
        // Ajustar as margens internas da label para o conteúdo
        return new Insets(10, 20, 10, 20);  // Margens personalizadas
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Label Arredondado");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        LabelArredondado label = new LabelArredondado("Texto da Label Arredondada");
        label.setBackground(new Color(70, 130, 180));  // Cor de fundo
        label.setForeground(Color.WHITE);  // Cor do texto
        label.setFont(new Font("Inter", Font.BOLD, 18));  // Fonte do texto

        frame.setLayout(new FlowLayout());
        frame.add(label);

        frame.setVisible(true);
    }
}

