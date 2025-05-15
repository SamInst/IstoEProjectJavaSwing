package tools;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PanelArredondado extends JPanel {
    private int arredondamentoSuperiorEsquerdo = 40;
    private int arredondamentoSuperiorDireito = 40;
    private int arredondamentoInferiorEsquerdo = 40;
    private int arredondamentoInferiorDireito = 40;

    public PanelArredondado() {
        setOpaque(false);
    }

    // Método para definir todos os cantos com o mesmo valor
    public void setArredondamento(int raio) {
        this.arredondamentoSuperiorEsquerdo = raio;
        this.arredondamentoSuperiorDireito = raio;
        this.arredondamentoInferiorEsquerdo = raio;
        this.arredondamentoInferiorDireito = raio;
        repaint();
    }

    // Método para definir cada canto individualmente
    public void setArredondamento(int superiorEsquerdo, int superiorDireito,
                                  int inferiorEsquerdo, int inferiorDireito) {
        this.arredondamentoSuperiorEsquerdo = superiorEsquerdo;
        this.arredondamentoSuperiorDireito = superiorDireito;
        this.arredondamentoInferiorEsquerdo = inferiorEsquerdo;
        this.arredondamentoInferiorDireito = inferiorDireito;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());

        // Cria um caminho com cantos arredondados personalizados
        int width = getWidth();
        int height = getHeight();

        RoundRectangle2D shape = new RoundRectangle2D.Double(
                0, 0,
                width, height,
                arredondamentoSuperiorEsquerdo, arredondamentoSuperiorDireito
        );

        g2.fill(shape);
        g2.dispose();
    }
}

