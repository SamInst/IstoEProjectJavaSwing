package principals.tools;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JTextFieldComTextoFixoArredondado extends JTextField {

    private final String textoFixo;
    private Color promptForegroundColor = Color.LIGHT_GRAY;

    public JTextFieldComTextoFixoArredondado(String textoFixo, int columns) {
        super(columns);
        this.textoFixo = (textoFixo != null) ? textoFixo : "";
        setText(this.textoFixo);
        setCaretPosition(this.textoFixo.length());
        setForeground(Color.DARK_GRAY);  // Texto digitado em DARK_GRAY
        setHorizontalAlignment(SwingConstants.LEFT);
        setMargin(new Insets(0, 10, 5, 10));
        setOpaque(false);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (getCaretPosition() < JTextFieldComTextoFixoArredondado.this.textoFixo.length()) {
                    setCaretPosition(JTextFieldComTextoFixoArredondado.this.textoFixo.length());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!getText().startsWith(JTextFieldComTextoFixoArredondado.this.textoFixo)) {
                    setText(JTextFieldComTextoFixoArredondado.this.textoFixo);
                }
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (getCaretPosition() < JTextFieldComTextoFixoArredondado.this.textoFixo.length()) {
                    setCaretPosition(JTextFieldComTextoFixoArredondado.this.textoFixo.length());
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

        // Desenho da borda arredondada
        g2.setColor(Cor.CINZA_ESCURO.brighter());
        float espessuraBorda = 1.0f;
        g2.setStroke(new BasicStroke(espessuraBorda));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        // Cor do texto dependendo se está vazio ou preenchido
        if (getText().equals(textoFixo)) {
            setForeground(promptForegroundColor);  // Cor do placeholder (texto fixo)
        } else {
            setForeground(Color.DARK_GRAY);  // Cor do texto digitado
        }

        g2.dispose();
        super.paintComponent(g);
    }

    public void setPromptForeground(Color color) {
        this.promptForegroundColor = color;
        repaint();
    }

    @Override
    public void paintBorder(Graphics g) {
        // Desabilitar a borda padrão
    }
}




