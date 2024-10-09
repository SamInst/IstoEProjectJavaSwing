package principals.tools;

import javax.swing.*;
import java.awt.*;

public class BlocosExemplo {
    public static void main(String[] args) {
        JFrame janelaAdicionar = new JFrame("Pernoite");
        janelaAdicionar.setLayout(new BorderLayout());
        janelaAdicionar.setSize(580, 800);
        janelaAdicionar.setMinimumSize(new Dimension(580, 600));
        janelaAdicionar.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        janelaAdicionar.setBackground(Color.RED);
        janelaAdicionar.setLocationRelativeTo(null);
        janelaAdicionar.setVisible(true);

        JPanel background = new JPanel();
        background.setBackground(Color.RED);
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));

        JPanel blocoBranco = blocoBranco(new JPanel());
        JPanel blocoAmarelo = blocoAmarelo(new JPanel());
        JPanel blocoLaranja = blocoLaranja(new JPanel());
        JPanel blocoVerde = blocoVerde(new JPanel());
        JPanel blocoAzul = blocoAzul(new JPanel());

        background.add(blocoBranco);
        background.add(blocoAmarelo);
        background.add(blocoLaranja);
        background.add(blocoVerde);
        background.add(blocoAzul);

        janelaAdicionar.add(background, BorderLayout.CENTER);

        janelaAdicionar.revalidate();
        janelaAdicionar.repaint();
    }

    public static JPanel blocoBranco(JPanel blocoBranco){
        blocoBranco.setPreferredSize(new Dimension(500, 70));
        blocoBranco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        blocoBranco.setBackground(Color.WHITE);
        blocoBranco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return blocoBranco;
    }

    public static JPanel blocoAmarelo(JPanel blocoAmarelo){
        blocoAmarelo.setPreferredSize(new Dimension(500, 140));
        blocoAmarelo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        blocoAmarelo.setBackground(Color.YELLOW);
        blocoAmarelo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return blocoAmarelo;
    }

    public static JPanel blocoLaranja(JPanel blocoLaranja){
        blocoLaranja.setPreferredSize(new Dimension(500, 70));
        blocoLaranja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        blocoLaranja.setBackground(Color.ORANGE);
        blocoLaranja.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return blocoLaranja;
    }

    public static JPanel blocoVerde(JPanel blocoVerde){
        blocoVerde.setPreferredSize(new Dimension(500, 70));
        blocoVerde.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        blocoVerde.setBackground(Color.GREEN);
        blocoVerde.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return blocoVerde;
    }

    public static JPanel blocoAzul(JPanel blocoAzul){
        blocoAzul.setPreferredSize(new Dimension(500, 70));
        blocoAzul.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        blocoAzul.setBackground(Color.BLUE);
        blocoAzul.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return blocoAzul;
    }
}
