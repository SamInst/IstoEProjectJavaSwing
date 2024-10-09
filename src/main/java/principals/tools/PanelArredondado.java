package principals.tools;

import javax.swing.*;
import java.awt.*;

public class PanelArredondado extends JPanel {

    public PanelArredondado() {
        setOpaque(false);  // Torna o fundo transparente para que possamos desenhar o nosso próprio fundo
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Chamando super.paintComponent() para garantir que qualquer desenho de fundo padrão seja feito corretamente

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.dispose();
    }

    @Override
    public Insets getInsets() {
        return new Insets(10, 20, 10, 20);
    }

    @Override
    public Dimension getPreferredSize() {
        // Ajustar o tamanho preferido do painel, se necessário
        Dimension d = super.getPreferredSize();
        d.setSize(d.width + 40, d.height + 20);  // Adicionar espaço para as bordas arredondadas
        return d;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Painel Arredondado");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        PanelArredondado painel = new PanelArredondado();
        painel.setBackground(new Color(70, 130, 180));  // Definir a cor de fundo
        painel.setLayout(new FlowLayout());  // Definir um layout (opcional)
        painel.add(new JLabel("Painel com bordas arredondadas"));
        painel.add(new JButton("Botão dentro do painel"));

        frame.setLayout(new FlowLayout());
        frame.add(painel);

        frame.setVisible(true);
    }
}

