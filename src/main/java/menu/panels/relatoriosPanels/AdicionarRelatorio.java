package menu.panels.relatoriosPanels;

import buttons.Botoes;
import buttons.ShadowButton;
import enums.TipoPagamentoEnum;
import repository.RelatoriosRepository;
import request.RelatorioRequest;
import tools.CorPersonalizada;
import tools.JComboBoxArredondado;
import tools.JTextFieldComTextoFixoArredondadoRelatorios;
import tools.PanelArredondado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import static notifications.Notification.notification;
import static notifications.Notifications.Location.TOP_CENTER;
import static notifications.Notifications.Type;
import static notifications.Notifications.Type.SUCCESS;
import static notifications.Notifications.Type.WARNING;
import static tools.CorPersonalizada.DARK_GRAY;
import static tools.CorPersonalizada.DARK_GREEN;
import static tools.Icones.plus;
import static tools.Icones.remove;
import static tools.Resize.resizeIcon;

public class AdicionarRelatorio extends PanelArredondado {
    private final JTextFieldComTextoFixoArredondadoRelatorios relatorioField;
    private final JComboBoxArredondado<TipoPagamentoEnum> tipoPagamentoComboBox;
    private final JTextFieldComTextoFixoArredondadoRelatorios campoQuarto;
    private final JTextFieldComTextoFixoArredondadoRelatorios campoValor;
    ShadowButton x = Botoes.btn_branco("");

    public AdicionarRelatorio(RelatoriosRepository relatoriosRepository, RelatoriosPanel relatoriosPanel, JFrame menu) {
        setSize(800, 250);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(CorPersonalizada.BACKGROUND_GRAY);
        topPanel.setPreferredSize(new Dimension(400, 20));
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        x.setBackground(CorPersonalizada.BACKGROUND_GRAY);
        x.setIcon(resizeIcon(remove, 25,25));

//        topPanel.add(x);
        x.addActionListener(ev -> {
            Window window = SwingUtilities.getWindowAncestor(x);
            if (window != null) {
                window.dispose();
            }
        });

        add(topPanel, BorderLayout.NORTH);

        JPanel laranjaPanel = new JPanel();
        laranjaPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
        laranjaPanel.setBackground(Color.WHITE);
        laranjaPanel.setPreferredSize(new Dimension(500, 100));

        relatorioField = new JTextFieldComTextoFixoArredondadoRelatorios("relatório: ", 35);
        relatorioField.setFont(new Font("Roboto", Font.PLAIN, 16));

        tipoPagamentoComboBox = new JComboBoxArredondado<>();
        tipoPagamentoComboBox.setEditable(true);
        tipoPagamentoComboBox.setPreferredSize(new Dimension(185, 30));
        Arrays.stream(TipoPagamentoEnum.values()).forEach(tipoPagamentoComboBox::addItem);

        tipoPagamentoComboBox.setEspessuraBorda(1.0F);
        tipoPagamentoComboBox.setCorBorda(DARK_GRAY.brighter());
        tipoPagamentoComboBox.setLightWeightPopupEnabled(false);
        tipoPagamentoComboBox.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.FALSE);

        campoQuarto = new JTextFieldComTextoFixoArredondadoRelatorios("quarto: ", 7);
        campoQuarto.setFont(new Font("Roboto", Font.PLAIN, 16));

        campoValor = new JTextFieldComTextoFixoArredondadoRelatorios("valor: ", 11);
        campoValor.setFont(new Font("Roboto", Font.PLAIN, 16));

        laranjaPanel.add(tipoPagamentoComboBox);
        laranjaPanel.add(campoQuarto);
        laranjaPanel.add(campoValor);
        laranjaPanel.add(relatorioField);
        add(laranjaPanel, BorderLayout.CENTER);

        JPanel pretoPanel = new JPanel();
        pretoPanel.setPreferredSize(new Dimension(400, 50));
        pretoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton btnAdicionar = Botoes.btn_verde("Adicionar Relatorio");
        btnAdicionar.setIcon(resizeIcon(plus, 20,20));
        pretoPanel.add(btnAdicionar);

        btnAdicionar.addActionListener(e -> adicionarRelatorio(relatoriosRepository, relatoriosPanel, menu));

        add(pretoPanel, BorderLayout.SOUTH);

        setupComboBoxFiltering(tipoPagamentoComboBox);
        configurarValidacaoCampos(campoQuarto, campoValor, relatorioField);
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
                    campoQuarto.setForeground(DARK_GRAY.brighter());
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
                        campoValor.setForeground(DARK_GRAY.brighter());
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
                            campoValor.setForeground(DARK_GREEN);
                        }
                    }
                } else if (texto.equals("-")) {
                    campoValor.setText("valor: -");
                    campoValor.setForeground(Color.RED);
                    campoValor.setCaretPosition(campoValor.getText().length());
                } else {
                    campoValor.setText("valor: ");
                    campoValor.setForeground(DARK_GRAY.brighter());
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
                    valorField.setForeground(DARK_GRAY.brighter());
                } else {
                    valorField.setForeground(Color.BLACK);
                }
            }
        });
    }

    private void adicionarRelatorio(RelatoriosRepository relatoriosRepository, RelatoriosPanel relatoriosPanel, JFrame menu) {
        try {
            String relatorio = relatorioField.getText().replace("relatório: ", "").trim().toUpperCase();
            RelatorioRequest relatorioRequest = getRelatorioRequest(relatorio);

            if (relatorioField.getText().isEmpty()) {
                notification(menu, WARNING, TOP_CENTER,"Relatório não pode estar vazio!");
                return;
            }

            if (campoValor.getText().isEmpty()) {
                notification(menu, Type.ERROR, TOP_CENTER,"Valor inválido");
                return;
            }

            relatoriosRepository.adicionarRelatorio(relatorioRequest);
            notification(menu, SUCCESS, TOP_CENTER, "Relatório Adicionado com sucesso");

            resetFields();
            relatoriosPanel.refreshPanel();

        } catch (Exception ex) {
            notification(menu, Type.ERROR, TOP_CENTER, "Relatório ou valor inválido");
        }
    }

    private RelatorioRequest getRelatorioRequest(String relatorio) {
        TipoPagamentoEnum tipoPagamento = (TipoPagamentoEnum) tipoPagamentoComboBox.getSelectedItem();

        Long quartoId = null;

        String quartoTexto = campoQuarto.getText().replace("quarto: ", "").trim();

        if (!quartoTexto.isEmpty()) {
            quartoId = Long.parseLong(quartoTexto);
            if (quartoId == 0) quartoId = null;
        }

        String valorTexto = campoValor.getText()
                .replace("valor: ", "")
                .replace("R$ ", "")
                .replace(".", "")
                .replace(",", ".").trim();
        Float valor = Float.parseFloat(valorTexto);

        return new RelatorioRequest(relatorio, tipoPagamento, quartoId, valor);
    }

    private void resetFields(){
        campoQuarto.setText("quarto: ");
        campoValor.setText("valor: ");
        relatorioField.setText("relatório: ");
    }
}
