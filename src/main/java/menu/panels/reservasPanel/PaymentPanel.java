package menu.panels.reservasPanel;

import buttons.ShadowButton;
import enums.TipoPagamentoEnum;
import notifications.Notifications;
import request.BuscaReservasResponse;
import tools.Converter;
import tools.FormatarFloat;
import tools.Mascaras;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import static buttons.Botoes.btn_backgroung;
import static buttons.Botoes.btn_laranja;
import static notifications.Notification.notification;
import static notifications.Notifications.Location.TOP_CENTER;
import static tools.CorPersonalizada.*;
import static tools.Icones.close;
import static tools.Resize.resizeIcon;

class PaymentPanel {
    private final ReservasPanel mainPanel;
    private JPanel pagamentosListPanel;

    public PaymentPanel(ReservasPanel mainPanel) {
        this.mainPanel = mainPanel;
        this.pagamentosListPanel = new JPanel();
    }

    public JPanel getPagamentosListPanel() {
        return pagamentosListPanel;
    }

    public JPanel createPaymentsTab(BuscaReservasResponse reserva) {
        Font textFont = new Font("Roboto", Font.PLAIN, 14);

        JPanel pagamentosTab = new JPanel();
        pagamentosTab.setLayout(new BoxLayout(pagamentosTab, BoxLayout.Y_AXIS));
        pagamentosTab.setOpaque(false);
        pagamentosTab.setAlignmentX(Component.LEFT_ALIGNMENT);
        pagamentosTab.add(Box.createVerticalStrut(10));

        JPanel descricaoPagamentoPanel = new JPanel(new BorderLayout());
        descricaoPagamentoPanel.setBackground(BACKGROUND_GRAY);
        descricaoPagamentoPanel.setPreferredSize(new Dimension(620, 30));
        descricaoPagamentoPanel.setMaximumSize(new Dimension(620, 30));

        JLabel descricaoPagamentoLabel = new JLabel("Descrição: ");
        descricaoPagamentoLabel.setFont(textFont);
        descricaoPagamentoLabel.setForeground(GRAY);
        descricaoPagamentoPanel.add(descricaoPagamentoLabel, BorderLayout.WEST);

        JFormattedTextField descricaoPagamentoField = new JFormattedTextField();
        descricaoPagamentoField.setPreferredSize(new Dimension(530, 25));
        descricaoPagamentoField.setForeground(DARK_GRAY);
        descricaoPagamentoField.setFont(new Font("Roboto", Font.PLAIN, 15));
        descricaoPagamentoPanel.add(descricaoPagamentoField, BorderLayout.CENTER);

        JPanel adicionarPagamento = new JPanel(new BorderLayout());
        adicionarPagamento.setPreferredSize(new Dimension(620, 30));
        adicionarPagamento.setMaximumSize(new Dimension(620, 30));
        adicionarPagamento.setBackground(BACKGROUND_GRAY);

        JLabel tipoPagamento = new JLabel("Tipo de pagamento: ");
        tipoPagamento.setFont(textFont);
        tipoPagamento.setForeground(GRAY);
        adicionarPagamento.add(tipoPagamento, BorderLayout.WEST);

        Vector<String> tipoPagamentoItems = new Vector<>();
        for (TipoPagamentoEnum tipo : TipoPagamentoEnum.values()) {
            tipoPagamentoItems.add(Converter.converterTipoPagamento(String.valueOf(tipo.getCodigo())));
        }

        JComboBox<String> tipoPagamentoComboBox = new JComboBox<>(tipoPagamentoItems);
        tipoPagamentoComboBox.setPreferredSize(new Dimension(193, 25));
        adicionarPagamento.add(tipoPagamentoComboBox, BorderLayout.CENTER);

        JFormattedTextField valorPagamentoField = new JFormattedTextField();
        valorPagamentoField.setPreferredSize(new Dimension(200, 25));
        valorPagamentoField.setForeground(DARK_GRAY);
        valorPagamentoField.setFont(new Font("Roboto", Font.BOLD, 14));
        valorPagamentoField.setText("R$ ");
        Mascaras.mascaraValor(valorPagamentoField);
        adicionarPagamento.add(valorPagamentoField, BorderLayout.LINE_END);

        pagamentosTab.add(descricaoPagamentoPanel);
        pagamentosTab.add(Box.createVerticalStrut(10));
        pagamentosTab.add(adicionarPagamento);

        pagamentosListPanel = new JPanel();
        pagamentosListPanel.setLayout(new BoxLayout(pagamentosListPanel, BoxLayout.Y_AXIS));
        pagamentosListPanel.setOpaque(false);

        JPanel adicionarPagamentoButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        adicionarPagamentoButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 10));
        adicionarPagamentoButtonPanel.setPreferredSize(new Dimension(720, 40));
        adicionarPagamentoButtonPanel.setMaximumSize(new Dimension(720, 40));
        adicionarPagamentoButtonPanel.setBackground(BACKGROUND_GRAY);

        ShadowButton adicionarPagamentoButton = btn_laranja("Adicionar Pagamento");
        adicionarPagamentoButton.setPreferredSize(new Dimension(150, 40));
        adicionarPagamentoButton.enableHoverEffect();
        adicionarPagamentoButtonPanel.add(adicionarPagamentoButton);

        pagamentosTab.add(adicionarPagamentoButtonPanel);

        adicionarPagamentoButton.addActionListener(e -> {
            String descricao = descricaoPagamentoField.getText();
            String valor = valorPagamentoField.getText()
                    .replace("R$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .replaceAll("[^0-9.]", "");
            String tipoPagamentoSelecionado = (String) tipoPagamentoComboBox.getSelectedItem();

            JPanel pagamentoPanel = createPagamentoPanel(
                    descricao,
                    valor,
                    Converter.converterTipoPagamento(Objects.requireNonNull(tipoPagamentoSelecionado)),
                    LocalDateTime.now().format(mainPanel.dtf),
                    reserva);

            pagamentosListPanel.add(pagamentoPanel);

            mainPanel.getReservasRepository().adicionarPagamentoReserva(
                    reserva.reserva_id(),
                    new BuscaReservasResponse.Pagamentos(
                            descricao,
                            Converter.converterTipoPagamentoParaInt(Objects.requireNonNull(tipoPagamentoSelecionado)),
                            Float.parseFloat(valor),
                            LocalDateTime.now())
            );

            notification(Notifications.Type.SUCCESS, TOP_CENTER, "Pagamento Adicionado: \n" + descricao + "\n R$ " + FormatarFloat.format(Float.parseFloat(valor)));

            descricaoPagamentoField.setText("");
            valorPagamentoField.setText("R$ ");

            pagamentosListPanel.revalidate();
            pagamentosListPanel.repaint();
        });

        JScrollPane pagamentosScrollPane = new JScrollPane(pagamentosListPanel);
        pagamentosScrollPane.setPreferredSize(new Dimension(900, 200));
        pagamentosScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        pagamentosTab.add(pagamentosScrollPane);

        return pagamentosTab;
    }

    public void carregarPagamentosExistentes(BuscaReservasResponse reserva) {
        pagamentosListPanel.removeAll();
        List<BuscaReservasResponse.Pagamentos> pagamentos =
                mainPanel.getReservasRepository().buscarPagamentosPorReserva(reserva.reserva_id());

        for (BuscaReservasResponse.Pagamentos pagamento : pagamentos) {
            JPanel pagamentoPanel = createPagamentoPanel(
                    pagamento.descricao(),
                    String.valueOf(pagamento.valor_pagamento()),
                    Converter.converterTipoPagamento(pagamento.tipo_pagamento()),
                    pagamento.data_hora_pagamento().format(mainPanel.dtf),
                    reserva
            );
            pagamentosListPanel.add(pagamentoPanel);
        }

        pagamentosListPanel.revalidate();
        pagamentosListPanel.repaint();
    }

    private JPanel createPagamentoPanel(String descricao, String valor, String tipoPagamento, String dataHora, BuscaReservasResponse reserva) {
        JPanel pagamentoPanel = new JPanel();
        pagamentoPanel.setLayout(new BoxLayout(pagamentoPanel, BoxLayout.Y_AXIS));
        pagamentoPanel.setOpaque(false);
        pagamentoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel linha1 = new JPanel(new BorderLayout());
        linha1.setOpaque(false);
        linha1.setMaximumSize(new Dimension(800, 30));

        JLabel descricaoLabel = new JLabel(descricao);
        descricaoLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        descricaoLabel.setForeground(DARK_GRAY);
        linha1.add(descricaoLabel, BorderLayout.WEST);

        JPanel valorButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        valorButtonPanel.setOpaque(false);

        JLabel valorLabel = new JLabel("R$ " + FormatarFloat.format(Float.valueOf(valor)));
        valorLabel.setFont(new Font("Roboto", Font.PLAIN, 15));
        valorLabel.setForeground(DARK_GRAY);
        valorButtonPanel.add(valorLabel);

        var x = btn_backgroung("");
        x.setIcon(resizeIcon(close, 10, 10));
        x.enableHoverEffect();
        x.setToolTipText("Remover");
        x.addActionListener(a -> {
            mainPanel.getReservasRepository().removerPagamentoReserva(reserva.reserva_id(), descricao);
            pagamentosListPanel.remove(pagamentoPanel);
            pagamentosListPanel.revalidate();
            pagamentosListPanel.repaint();

            notification(Notifications.Type.WARNING, TOP_CENTER, "Pagamento removido: \n" + descricao + "\n R$ " + FormatarFloat.format(Float.parseFloat(valor)));
        });

        Dimension buttonSize = new Dimension(30, 30);
        x.setPreferredSize(buttonSize);
        x.setMinimumSize(buttonSize);
        x.setMaximumSize(buttonSize);
        valorButtonPanel.add(x);

        linha1.add(valorButtonPanel, BorderLayout.EAST);

        JPanel dataTipoPagamento = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dataTipoPagamento.setOpaque(false);
        JLabel tipoPagamentoLabel = new JLabel(" " + tipoPagamento);
        tipoPagamentoLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        tipoPagamentoLabel.setForeground(GRAY);
        JLabel dataHoraLabel = new JLabel(dataHora);
        dataHoraLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        dataHoraLabel.setForeground(GRAY);
        dataTipoPagamento.add(dataHoraLabel);
        dataTipoPagamento.add(tipoPagamentoLabel);

        JPanel linha2 = new JPanel(new BorderLayout());
        linha2.setOpaque(false);
        linha2.setMaximumSize(new Dimension(800, 10));
        linha2.add(dataTipoPagamento, BorderLayout.WEST);

        pagamentoPanel.add(linha1);
        pagamentoPanel.add(linha2);

        return pagamentoPanel;
    }
}
