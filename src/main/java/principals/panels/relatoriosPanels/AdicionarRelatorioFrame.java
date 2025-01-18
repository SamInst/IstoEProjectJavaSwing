package principals.panels.relatoriosPanels;

import buttons.Botoes;
import enums.TipoPagamentoEnum;
import principals.tools.*;
import repository.RelatoriosRepository;
import request.RelatorioRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

public class AdicionarRelatorioFrame extends JFrame {
    private final JTextFieldComTextoFixoArredondadoRelatorios valorField;
    private final JComboBoxArredondado<TipoPagamentoEnum> tipoPagamentoComboBox;
    private final JTextFieldComTextoFixoArredondadoRelatorios campoQuarto;
    private final JTextFieldComTextoFixoArredondadoRelatorios campoValor;

    public AdicionarRelatorioFrame(RelatoriosRepository relatoriosRepository, RelatoriosPanel relatoriosPanel) {
        setTitle("Adicionar Relatorio");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 200);
        setPreferredSize(new Dimension(600, 250));
        setMinimumSize(new Dimension(600, 250));
        setMaximumSize(new Dimension(600, 250));
        setResizable(false);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel azulPanel = new JPanel();
        azulPanel.setBackground(new Color(0x424B98));
        azulPanel.setPreferredSize(new Dimension(400, 50));
        azulPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        azulPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 0));
        JLabel titulo = new JLabel("Adicionar Relatório");
        titulo.setFont(new Font("Roboto", Font.PLAIN, 20));
        titulo.setForeground(Color.WHITE);
        azulPanel.add(titulo);
        add(azulPanel, BorderLayout.NORTH);

        JPanel laranjaPanel = new JPanel();
        laranjaPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
        laranjaPanel.setBackground(Color.WHITE);
        laranjaPanel.setPreferredSize(new Dimension(500, 200));

        valorField = new JTextFieldComTextoFixoArredondadoRelatorios("relatório: ", 35);
        valorField.setFont(new Font("Roboto", Font.PLAIN, 16));

        tipoPagamentoComboBox = new JComboBoxArredondado<>();
        tipoPagamentoComboBox.setEditable(true);
        tipoPagamentoComboBox.setPreferredSize(new Dimension(200, 30));
        Arrays.stream(TipoPagamentoEnum.values()).forEach(tipoPagamentoComboBox::addItem);

        tipoPagamentoComboBox.setEspessuraBorda(1.0F);
        tipoPagamentoComboBox.setCorBorda(CorPersonalizada.CINZA_ESCURO.brighter());

        campoQuarto = new JTextFieldComTextoFixoArredondadoRelatorios("quarto: ", 7);
        campoQuarto.setFont(new Font("Roboto", Font.PLAIN, 16));

        campoValor = new JTextFieldComTextoFixoArredondadoRelatorios("valor: ", 12);
        campoValor.setFont(new Font("Roboto", Font.PLAIN, 16));

        laranjaPanel.add(tipoPagamentoComboBox);
        laranjaPanel.add(campoQuarto);
        laranjaPanel.add(campoValor);
        laranjaPanel.add(valorField);
        add(laranjaPanel, BorderLayout.CENTER);

        JPanel pretoPanel = new JPanel();
        pretoPanel.setPreferredSize(new Dimension(400, 50));
        pretoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton btnAdicionar = Botoes.btn_verde("Adicionar Relatorio");
        btnAdicionar.setIcon(Resize.resizeIcon(Icones.plus, 20,20));
        pretoPanel.add(btnAdicionar);

        btnAdicionar.addActionListener(e -> adicionarRelatorio(relatoriosRepository, relatoriosPanel));

        add(pretoPanel, BorderLayout.SOUTH);

        setupComboBoxFiltering(tipoPagamentoComboBox);
        configurarValidacaoCampos(campoQuarto, campoValor, valorField);
        setVisible(true);
    }

    private void setupComboBoxFiltering(JComboBox<TipoPagamentoEnum> comboBox) {
        JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String filter = textField.getText();
                comboBox.removeAllItems();
                for (TipoPagamentoEnum item : TipoPagamentoEnum.values()) {
                    if (item.name().toLowerCase().contains(filter.toLowerCase())) {
                        comboBox.addItem(item);
                    }
                }
                textField.setText(filter);
                comboBox.showPopup();
            }
        });
    }

    private void configurarValidacaoCampos(JTextFieldComTextoFixoArredondadoRelatorios campoQuarto, JTextFieldComTextoFixoArredondadoRelatorios campoValor, JTextFieldComTextoFixoArredondadoRelatorios valorField) {
        campoQuarto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campoQuarto.getText().replaceAll("[^0-9]", "");
                if (!texto.isEmpty()) {
                    int numero = Integer.parseInt(texto);
                    String formattedText = (numero < 10) ? "0" + numero : String.valueOf(numero);
                    campoQuarto.setText("quarto: " + formattedText.substring(0, Math.min(2, formattedText.length())));
                    campoQuarto.setCaretPosition(campoQuarto.getText().length());
                    campoQuarto.setForeground(Color.BLACK);
                } else {
                    campoQuarto.setText("quarto: ");
                    campoQuarto.setForeground(CorPersonalizada.CINZA_ESCURO.brighter());
                    campoQuarto.setCaretPosition(campoQuarto.getText().length());
                }
            }
        });

        campoValor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campoValor.getText().replaceAll("[^-0-9]", "");
                boolean isNegative = texto.startsWith("-");

                if (!texto.isEmpty() && !(texto.equals("-"))) {
                    double valor = Double.parseDouble(texto.replace("-", "")) / 100;

                    if (valor == 0) {
                        campoValor.setText("valor: ");
                        campoValor.setForeground(CorPersonalizada.CINZA_ESCURO.brighter());
                        campoValor.setCaretPosition(campoValor.getText().length());
                    } else {
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
                        symbols.setDecimalSeparator(',');
                        symbols.setGroupingSeparator('.');
                        DecimalFormat formato = new DecimalFormat("#,##0.00", symbols);
                        String formattedValue = (isNegative ? "-" : "") + "R$ " + formato.format(valor);

                        campoValor.setText("valor: " + formattedValue);
                        campoValor.setCaretPosition(campoValor.getText().length());

                        if (isNegative) {
                            campoValor.setForeground(Color.RED);
                        } else {
                            campoValor.setForeground(CorPersonalizada.VERDE_ESCURO);
                        }
                    }
                } else if (texto.equals("-")) {
                    campoValor.setText("valor: -");
                    campoValor.setForeground(Color.RED);
                    campoValor.setCaretPosition(campoValor.getText().length());
                } else {
                    campoValor.setText("valor: ");
                    campoValor.setForeground(CorPersonalizada.CINZA_ESCURO.brighter());
                    campoValor.setCaretPosition(campoValor.getText().length());
                }
            }
        });


        valorField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = valorField.getText().replaceFirst("relatório: ", "");
                valorField.setText("relatório: " + texto);
                valorField.setCaretPosition(valorField.getText().length());

                if (texto.isEmpty()) {
                    valorField.setForeground(CorPersonalizada.CINZA_ESCURO.brighter());
                } else {
                    valorField.setForeground(Color.BLACK);
                }
            }
        });
    }

    private void adicionarRelatorio(RelatoriosRepository relatoriosRepository, RelatoriosPanel relatoriosPanel) {
        try {
            String relatorio = valorField.getText().replace("relatório: ", "").trim().toUpperCase();
            TipoPagamentoEnum tipoPagamento = (TipoPagamentoEnum) tipoPagamentoComboBox.getSelectedItem();

            Long quartoId = null;

            String quartoTexto = campoQuarto.getText().replace("quarto: ", "").trim();
            if (!quartoTexto.isEmpty()) {
                quartoId = Long.parseLong(quartoTexto);
                if (quartoId == 0) quartoId = null;
            }

            String valorTexto = campoValor.getText().replace("valor: ", "").replace("R$ ", "").replace(".", "").replace(",", ".").trim();
            Float valor = Float.parseFloat(valorTexto);

            RelatorioRequest relatorioRequest = new RelatorioRequest(relatorio, tipoPagamento, quartoId, valor);

            relatoriosRepository.adicionarRelatorio(relatorioRequest);

            JOptionPane.showMessageDialog(this, "Relatório adicionado com sucesso!");
            dispose();
            relatoriosPanel.refreshPanel();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar o relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
