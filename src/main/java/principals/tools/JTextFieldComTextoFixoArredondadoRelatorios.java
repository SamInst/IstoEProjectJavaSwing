package principals.tools;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JTextFieldComTextoFixoArredondadoRelatorios extends JTextField {

    private final String textoFixo;

    public JTextFieldComTextoFixoArredondadoRelatorios(String textoFixo, int columns) {
        super(columns);
        this.textoFixo = (textoFixo != null) ? textoFixo : "";
        setText(this.textoFixo);
        setCaretPosition(this.textoFixo.length());
        setForeground(Color.GRAY);
        setHorizontalAlignment(SwingConstants.LEFT);
        setMargin(new Insets(0, 10, 5, 10));
        setOpaque(false);
//        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//        setBackground(Color.GRAY.brighter());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (getCaretPosition() < JTextFieldComTextoFixoArredondadoRelatorios.this.textoFixo.length()) {
                    setCaretPosition(JTextFieldComTextoFixoArredondadoRelatorios.this.textoFixo.length());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!getText().startsWith(JTextFieldComTextoFixoArredondadoRelatorios.this.textoFixo)) {
                    setText(JTextFieldComTextoFixoArredondadoRelatorios.this.textoFixo);
                }
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (getCaretPosition() < JTextFieldComTextoFixoArredondadoRelatorios.this.textoFixo.length()) {
                    setCaretPosition(JTextFieldComTextoFixoArredondadoRelatorios.this.textoFixo.length());
                }
            }
        });
    }

    @Override
    public void setText(String t) {
        if (t != null && !t.startsWith(textoFixo)) {
            t = textoFixo;
        }
        super.setText(t);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.setColor(Cor.CINZA_ESCURO.brighter());

        float espessuraBorda = 1.0f;
        g2.setStroke(new BasicStroke(espessuraBorda));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        g2.dispose();
        super.paintComponent(g);
    }


    @Override
    public void paintBorder(Graphics g) {
    }
}




