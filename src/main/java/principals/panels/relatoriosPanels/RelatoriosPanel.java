package principals.panels.relatoriosPanels;

import buttons.ShadowButton;
import calendar.DateChooser;
import calendar.EventDateChooser;
import calendar.SelectedAction;
import calendar.SelectedDate;
import enums.TipoPagamentoEnum;
import principals.tools.Refreshable;
import principals.tools.Resize;
import repository.RelatoriosRepository;
import response.RelatoriosResponse;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import static buttons.Botoes.*;
import static enums.TipoPagamentoEnum.*;
import static java.time.format.DateTimeFormatter.ofPattern;
import static javax.swing.BorderFactory.createEmptyBorder;
import static principals.tools.CorPersonalizada.*;
import static principals.tools.FormatarFloat.format;
import static principals.tools.Icones.*;
import static principals.tools.OptionPane.warning;
import static principals.tools.Resize.resizeIcon;

public class RelatoriosPanel extends JPanel implements Refreshable {
    private final JFrame menu;
    private final RelatoriosRepository relatoriosRepository;

    private JPanel relatoriosPanel;
    private JScrollPane scrollPane;
    private JButton btnAdicionar;
    private ShadowButton btnFiltros;
    private LocalDate selectedDate;
    private ShadowButton saldoBtn;

    private final DateChooser dateChooser = new DateChooser();
    private final ShadowButton searchDateField = new ShadowButton();

    public RelatoriosPanel(RelatoriosRepository relatoriosRepository, JFrame menu) {
        this.relatoriosRepository = relatoriosRepository;
        this.menu = menu;
        refreshPanel();
    }

    private void mostrarPorData(LocalDate data) {
        updatePanel(() -> {
            var relatorio = relatoriosRepository.buscaRelatorioPorData(data);
            relatoriosPanel.add(montarPainelDia(relatorio));
        });
    }

    private void mostrarPorDataETipo(LocalDate data, TipoPagamentoEnum tipo) {
        updatePanel(() -> {
            List<RelatoriosResponse.Relatorios.RelatorioDoDia> lista = relatoriosRepository.buscaPorDataETipo(data, tipo);
            relatoriosPanel.add(montarPainelDiaETipo(data, tipo, lista));
        });
    }

    private void mostrarTodasDatasPorTipo(TipoPagamentoEnum tipo) {
        updatePanel(() -> {
            List<LocalDate> datas = relatoriosRepository.datasPorTipo(tipo);
            datas.forEach(d -> {
                List<RelatoriosResponse.Relatorios.RelatorioDoDia> rows = relatoriosRepository.buscaPorDataETipo(d, tipo);
                if (!rows.isEmpty()) {
                    relatoriosPanel.add(montarPainelDiaETipo(d, tipo, rows));
                }
            });
        });
    }

    private void mostrarTodasDatasRetirada() {
        updatePanel(() -> {
            List<LocalDate> datas = relatoriosRepository.datasRetirada();
            datas.forEach(d -> {
                List<RelatoriosResponse.Relatorios.RelatorioDoDia> rows = relatoriosRepository.buscaRetiradaPorData(d);
                if (!rows.isEmpty()) {
                    relatoriosPanel.add(montarPainelDiaETipo(d, DINHEIRO, rows));
                }
            });
        });
    }

    private void updatePanel(Runnable updateAction) {
        relatoriosPanel.removeAll();
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
        updateAction.run();
        relatoriosPanel.revalidate();
        relatoriosPanel.repaint();
    }

    private JPanel montarPainelDia(RelatoriosResponse.Relatorios relatorio) {
        JPanel relatorioDiaPanel = new JPanel(new BorderLayout());
        relatorioDiaPanel.setBackground(BACKGROUND_GRAY);
        relatorioDiaPanel.setBorder(createEmptyBorder(5, 20, 5, 10));

        var dataBtn = btn_branco(relatorio.data());
        var totalBtn = btn_branco("Total do dia: R$ " + format(relatorio.total_do_dia()));

        JPanel headerPanel = createHeaderPanel(dataBtn, totalBtn);
        relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel relatoriosDoDiaPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        relatoriosDoDiaPanel.setBackground(LIGHT_GRAY);
        relatoriosDoDia(relatorio.relatorioDoDia(), relatoriosDoDiaPanel);
        relatorioDiaPanel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);

        dataBtn.addActionListener(e -> relatoriosDoDiaPanel.setVisible(!relatoriosDoDiaPanel.isVisible()));
        return relatorioDiaPanel;
    }

    private JPanel montarPainelDiaETipo(LocalDate data, TipoPagamentoEnum tipo,
                                        List<RelatoriosResponse.Relatorios.RelatorioDoDia> lista) {
        JPanel relatorioDiaPanel = new JPanel(new BorderLayout());
        relatorioDiaPanel.setBackground(BACKGROUND_GRAY);
        relatorioDiaPanel.setBorder(createEmptyBorder(5, 20, 5, 10));

        var dataStr = data.format(ofPattern("dd/MM/yyyy"));
        var dataBtn = btn_branco(dataStr);
        var filtroBtn = btn_branco("Filtro: " + tipo.name());

        JPanel headerPanel = createHeaderPanel(dataBtn, filtroBtn);
        relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel relatoriosDoDiaPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        relatoriosDoDiaPanel.setBackground(LIGHT_GRAY);
        relatoriosDoDia(lista, relatoriosDoDiaPanel);
        relatorioDiaPanel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);

        dataBtn.addActionListener(e -> relatoriosDoDiaPanel.setVisible(!relatoriosDoDiaPanel.isVisible()));
        return relatorioDiaPanel;
    }

    private JPanel createHeaderPanel(JButton leftButton, JButton rightButton) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.add(leftButton, BorderLayout.WEST);
        headerPanel.add(rightButton, BorderLayout.EAST);
        headerPanel.setBorder(createEmptyBorder(5, 5, 0, 10));
        return headerPanel;
    }

    public void relatoriosDoDia(List<RelatoriosResponse.Relatorios.RelatorioDoDia> lista,
                                JPanel relatoriosDoDiaPanel) {
        lista.forEach(relatorioDoDia -> {
            JButton relatorioButton = createRelatorioButton(relatorioDoDia);
            relatoriosDoDiaPanel.add(relatorioButton);
        });
    }

    private JButton createRelatorioButton(RelatoriosResponse.Relatorios.RelatorioDoDia relatorioDoDia) {
        JButton relatorioButton = new JButton();
        var tipoPagamentoButton = btn_branco("DESCONHECIDO");
        JLabel tipoPagamentoText = new JLabel("DESCONHECIDO");

        switch (relatorioDoDia.tipo_pagamento()) {
            case "0" -> configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA PIX", BLUE);
            case "1" -> {
                Color cor = relatorioDoDia.valor() < 0 ? RED_2 : GREEN;
                configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO EM CÉDULAS", cor);
            }
            case "2" -> configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA CARTÃO DE CRÉDITO", BLUE);
            case "3" -> configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA CARTÃO DE DÉBITO", BLUE);
            case "4" -> configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA CARTÃO VIRTUAL", BLUE);
            case "5" -> configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA TRANSFERÊNCIA BANCÁRIA", BLUE);
        }

        relatorioButton.setBackground(tipoPagamentoButton.getForeground());
        relatorioButton.setBorder(createEmptyBorder(0, 5, 0, 0));
        relatorioButton.setBorderPainted(false);
        relatorioButton.setFocusPainted(false);
        relatorioButton.setContentAreaFilled(true);
        relatorioButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel buttonContent = createButtonContent(relatorioDoDia, tipoPagamentoText);
        relatorioButton.setLayout(new BorderLayout());
        relatorioButton.add(buttonContent);

        return relatorioButton;
    }

    private void configureTipoPagamento(JLabel label, JButton button, String text, Color color) {
        label.setText(text);
        label.setForeground(color);
        button.setForeground(color);
    }

    private JPanel createButtonContent(RelatoriosResponse.Relatorios.RelatorioDoDia relatorioDoDia, JLabel tipoPagamentoText) {
        JPanel buttonContent = new JPanel(new BorderLayout());
        buttonContent.setBackground(Color.WHITE);

        ShadowButton valorSomaButton =
                btn_branco(format(relatoriosRepository.somaValorRelatorioMaisAnteriores(relatorioDoDia.relatorio_id())));
        JPanel rightButtonContent = new JPanel();
        rightButtonContent.setBackground(BACKGROUND_GRAY);
        rightButtonContent.setPreferredSize(new Dimension(80, buttonContent.getPreferredSize().height));
        rightButtonContent.add(valorSomaButton);
        rightButtonContent.setVisible(false);

        var valor = "R$ " + format(relatorioDoDia.valor());
        var btn_hora = btn_branco(relatorioDoDia.horario().format(ofPattern("HH:mm")));
        var btn_quarto =
                relatorioDoDia.quarto_id() == 0
                        ? btn_branco(" 00 ")
                        : btn_cinza(
                        relatorioDoDia.quarto_id() < 10
                                ? " 0" + relatorioDoDia.quarto_id() + " "
                                : " " + relatorioDoDia.quarto_id() + " "
                );
        btn_quarto.setForeground(Color.WHITE);

        JPanel descricaoPanel = new JPanel();
        descricaoPanel.setLayout(new BoxLayout(descricaoPanel, BoxLayout.Y_AXIS));
        descricaoPanel.setBackground(Color.WHITE);
        descricaoPanel.setBorder(createEmptyBorder(3, 0, 0, 0));
        descricaoPanel.setPreferredSize(new Dimension(900, 40));

        ShadowButton valorPagamentoButton;
        if (relatorioDoDia.valor() < 0) {
            valorPagamentoButton = btn_vermelho(valor);
        } else if ("1".equals(relatorioDoDia.tipo_pagamento())) {
            valorPagamentoButton = btn_verde(valor);
        } else {
            valorPagamentoButton = btn_azul(valor);
        }

        JLabel descricao = new JLabel(relatorioDoDia.relatorio());
        descricao.setBorder(null);
        descricao.setForeground(DARK_GRAY);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(btn_quarto);
        leftPanel.add(btn_hora);

        descricaoPanel.add(descricao);
        descricaoPanel.add(tipoPagamentoText);

        leftPanel.add(descricaoPanel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.setBackground(BACKGROUND_GRAY);
        rightPanel.add(valorPagamentoButton);

        buttonContent.add(leftPanel, BorderLayout.WEST);
        buttonContent.add(rightPanel, BorderLayout.EAST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(buttonContent, BorderLayout.CENTER);
        contentPanel.add(rightButtonContent, BorderLayout.EAST);

        saldoBtn.addActionListener(e -> rightButtonContent.setVisible(!rightButtonContent.isVisible()));

        return contentPanel;
    }


    private void initializePanel() {
        setLayout(new BorderLayout());
        RelatoriosResponse response = relatoriosRepository.relatoriosResponse();

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setPreferredSize(new Dimension(300, 50));
        rightPanel.setBorder(createEmptyBorder());

        JPanel identificadorPanel = new JPanel(new BorderLayout());
        identificadorPanel.setOpaque(false);

        topPanel.add(identificadorPanel, BorderLayout.CENTER);

        searchDateField.setIcon(Resize.resizeIcon(search, 15,15));
        searchDateField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateChooser.setTextReference(searchDateField);

        dateChooser.addEventDateChooser(new EventDateChooser() {
            @Override
            public void dateSelected(SelectedAction action, SelectedDate date) {
                if (action.getAction() == SelectedAction.DAY_SELECTED) {
                    dateChooser.hidePopup();
                    LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
                    if (localDate != null) {
                        selectedDate = localDate;
                        mostrarPorData(localDate);
                    } else {
                        warning(menu, "Selecione uma data válida");
                    }
                }
            }
        });

        searchDateField.addActionListener(e -> dateChooser.showPopup());

        btnAdicionar = btn_verde(" Adicionar Relatório");
        btnAdicionar.setIcon(resizeIcon(plus, 15, 15));
        // Exemplo: btnAdicionar.addActionListener(e -> toggleFormPanel());

        btnFiltros = btn_branco(" Filtros");
        btnFiltros.setIcon(resizeIcon(filter_gray, 15, 15));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(searchDateField);
        buttonsPanel.add(btnAdicionar);
        buttonsPanel.add(btnFiltros);
        identificadorPanel.add(buttonsPanel, BorderLayout.NORTH);

        saldoBtn = btn_verde("Saldo R$ " + format(relatoriosRepository.totalPorTipo(DINHEIRO)));
        saldoBtn.setPreferredSize(new Dimension(300, 50));
        saldoBtn.setMaximumSize(new Dimension(300, 50));
        saldoBtn.setMinimumSize(new Dimension(300, 50));
        rightPanel.add(saldoBtn, BorderLayout.NORTH);

        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        ShadowButton pixButton = btn_azul(" PIX");
        pixButton.setIcon(resizeIcon(pix, 15,15));
        pixButton.addActionListener(e -> updateSaldoAndPanel(PIX));

        ShadowButton dinheiroButton = btn_verde(" CÉDULAS");
        dinheiroButton.setIcon(resizeIcon(cash, 15,15));
        dinheiroButton.addActionListener(e -> updateSaldoAndPanel(DINHEIRO));

        ShadowButton creditoButton = btn_azul(" CARTÃO DE CRÉDITO");
        creditoButton.setIcon(resizeIcon(card, 15,15));
        creditoButton.addActionListener(e -> updateSaldoAndPanel(CARTAO_CREDITO));

        ShadowButton debitoButton = btn_azul(" CARTÃO DE DÉBITO");
        debitoButton.setIcon(resizeIcon(card, 15,15));
        debitoButton.addActionListener(e -> updateSaldoAndPanel(CARTAO_DEBITO));

        ShadowButton virtualButton = btn_azul(" CARTÃO VIRTUAL");
        virtualButton.setIcon(resizeIcon(card, 15,15));
        virtualButton.addActionListener(e -> updateSaldoAndPanel(CARTAO_VIRTUAL));

        ShadowButton transfButton = btn_azul(" TRANSFERÊNCIA BANCÁRIA");
        transfButton.setIcon(resizeIcon(bank, 15,15));
        transfButton.addActionListener(e -> updateSaldoAndPanel(TRANSFERENCIA_BANCARIA));

        ShadowButton retiradaButton = btn_vermelho(" RETIRADA");
        retiradaButton.setIcon(resizeIcon(cash_out, 15,15));
        retiradaButton.addActionListener(e -> {
            if (selectedDate != null) {
                float totalNegativoNoDia = relatoriosRepository.buscaRetiradaPorData(selectedDate)
                        .stream()
                        .map(RelatoriosResponse.Relatorios.RelatorioDoDia::valor)
                        .reduce(0f, Float::sum);

                saldoBtn.setText("Saldo R$ " + format(totalNegativoNoDia));
                List<RelatoriosResponse.Relatorios.RelatorioDoDia> dia =
                        relatoriosRepository.buscaRetiradaPorData(selectedDate);
                updatePanel(() -> {
                    relatoriosPanel.add(montarPainelDiaETipo(selectedDate, DINHEIRO, dia));
                });
            } else {
                saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalNegativo()));
                mostrarTodasDatasRetirada();
            }
        });

        btnFiltros.addActionListener(e -> {
            btnFiltros.showPopupWithButtons(pixButton,
                    dinheiroButton,
                    creditoButton,
                    debitoButton,
                    virtualButton,
                    transfButton,
                    retiradaButton);
        });

        topPanel.setBorder(createEmptyBorder(10, 10, 10, 10));
        add(topPanel, BorderLayout.NORTH);

        relatoriosPanel = new JPanel();
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));

        var responseData = response.relatorios();
        responseData.forEach(r -> relatoriosPanel.add(montarPainelDia(r)));

        scrollPane = new JScrollPane(
                relatoriosPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateSaldoAndPanel(TipoPagamentoEnum tipo) {
        if (selectedDate != null) {
            saldoBtn.setText("Saldo R$ " + format(
                    relatoriosRepository.totalPorTipoEData(selectedDate, tipo)
            ));
            mostrarPorDataETipo(selectedDate, tipo);
        } else {
            saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(tipo)));
            mostrarTodasDatasPorTipo(tipo);
        }
    }

    @Override
    public void refreshPanel() {
        removeAll();
        initializePanel();
        revalidate();
        repaint();
    }
}