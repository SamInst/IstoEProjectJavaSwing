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
    private JButton btnFiltros;
    private JPanel filterButtonsPanel;
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
        relatoriosPanel.removeAll();
        var relatorio = relatoriosRepository.buscaRelatorioPorData(data);
        JPanel painel = montarPainelDia(relatorio);
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
        relatoriosPanel.add(painel);
        relatoriosPanel.revalidate();
        relatoriosPanel.repaint();
    }

    private void mostrarPorDataETipo(LocalDate data, TipoPagamentoEnum tipo) {
        relatoriosPanel.removeAll();
        List<RelatoriosResponse.Relatorios.RelatorioDoDia> lista = relatoriosRepository.buscaPorDataETipo(data, tipo);
        JPanel painel = montarPainelDiaETipo(data, tipo, lista);
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
        relatoriosPanel.add(painel);
        relatoriosPanel.revalidate();
        relatoriosPanel.repaint();
    }

    private void mostrarTodasDatasPorTipo(TipoPagamentoEnum tipo) {
        relatoriosPanel.removeAll();
        List<LocalDate> datas = relatoriosRepository.datasPorTipo(tipo);
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
        for (LocalDate d : datas) {
            List<RelatoriosResponse.Relatorios.RelatorioDoDia> rows = relatoriosRepository.buscaPorDataETipo(d, tipo);
            if (!rows.isEmpty()) {
                JPanel p = montarPainelDiaETipo(d, tipo, rows);
                relatoriosPanel.add(p);
            }
        }
        relatoriosPanel.revalidate();
        relatoriosPanel.repaint();
    }

    private void mostrarTodasDatasRetirada() {
        relatoriosPanel.removeAll();
        List<LocalDate> datas = relatoriosRepository.datasRetirada();
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
        for (LocalDate d : datas) {
            List<RelatoriosResponse.Relatorios.RelatorioDoDia> rows = relatoriosRepository.buscaRetiradaPorData(d);
            if (!rows.isEmpty()) {
                JPanel p = montarPainelDiaETipo(d, DINHEIRO, rows);
                relatoriosPanel.add(p);
            }
        }
        relatoriosPanel.revalidate();
        relatoriosPanel.repaint();
    }

    private JPanel montarPainelDia(RelatoriosResponse.Relatorios relatorio) {
        JPanel relatorioDiaPanel = new JPanel(new BorderLayout());
        relatorioDiaPanel.setBackground(BACKGROUND_GRAY);
        relatorioDiaPanel.setBorder(createEmptyBorder(5, 20, 5, 10));

        var dataBtn = btn_branco(relatorio.data());
        var totalBtn = btn_branco("Total do dia: R$ " + format(relatorio.total_do_dia()));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.add(dataBtn, BorderLayout.WEST);
        headerPanel.add(totalBtn, BorderLayout.EAST);
        headerPanel.setBorder(createEmptyBorder(5, 5, 0, 10));
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

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.add(dataBtn, BorderLayout.WEST);
        headerPanel.add(filtroBtn, BorderLayout.EAST);
        headerPanel.setBorder(createEmptyBorder(5, 5, 0, 10));

        relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel relatoriosDoDiaPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        relatoriosDoDiaPanel.setBackground(LIGHT_GRAY);
        relatoriosDoDia(lista, relatoriosDoDiaPanel);
        relatorioDiaPanel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);

        dataBtn.addActionListener(e -> relatoriosDoDiaPanel.setVisible(!relatoriosDoDiaPanel.isVisible()));
        return relatorioDiaPanel;
    }

    public void relatoriosDoDia(List<RelatoriosResponse.Relatorios.RelatorioDoDia> lista,
                                JPanel relatoriosDoDiaPanel) {
        for (var relatorioDoDia : lista) {
            JButton relatorioButton = new JButton();
            var tipoPagamentoButton = btn_branco("DESCONHECIDO");
            JLabel tipoPagamentoText = new JLabel("DESCONHECIDO");

            switch (relatorioDoDia.tipo_pagamento()) {
                case "0" -> {
                    tipoPagamentoText.setText("PAGAMENTO VIA PIX");
                    tipoPagamentoText.setForeground(BLUE);
                    tipoPagamentoButton.setForeground(BLUE);
                }
                case "1" -> {
                    Color cor = relatorioDoDia.valor() < 0 ? RED_2 : GREEN;
                    tipoPagamentoText.setText("PAGAMENTO EM CÉDULAS");
                    tipoPagamentoText.setForeground(cor);
                    tipoPagamentoButton.setForeground(cor);
                }
                case "2" -> {
                    tipoPagamentoText.setText("PAGAMENTO VIA CARTÃO DE CRÉDITO");
                    tipoPagamentoText.setForeground(BLUE);
                    tipoPagamentoButton.setForeground(BLUE);
                }
                case "3" -> {
                    tipoPagamentoText.setText("PAGAMENTO VIA CARTÃO DE DÉBITO");
                    tipoPagamentoText.setForeground(BLUE);
                    tipoPagamentoButton.setForeground(BLUE);
                }
                case "4" -> {
                    tipoPagamentoText.setText("PAGAMENTO VIA CARTÃO VIRTUAL");
                    tipoPagamentoText.setForeground(BLUE);
                    tipoPagamentoButton.setForeground(BLUE);
                }
                case "5" -> {
                    tipoPagamentoText.setText("PAGAMENTO VIA TRANSFERÊNCIA BANCÁRIA");
                    tipoPagamentoText.setForeground(BLUE);
                    tipoPagamentoButton.setForeground(BLUE);
                }
            }

            relatorioButton.setBackground(tipoPagamentoButton.getForeground());
            relatorioButton.setBorder(createEmptyBorder(0, 5, 0, 0));
            relatorioButton.setBorderPainted(false);
            relatorioButton.setFocusPainted(false);
            relatorioButton.setContentAreaFilled(true);
            relatorioButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel buttonContent = new JPanel(new BorderLayout());
            buttonContent.setBackground(Color.WHITE);

            ShadowButton valorSomaButton =
                    btn_verde(format(relatoriosRepository.somaValorRelatorioMaisAnteriores(relatorioDoDia.relatorio_id())));
            JPanel rightButtonContent = new JPanel();
            rightButtonContent.setBackground(Color.LIGHT_GRAY);
            rightButtonContent.setPreferredSize(new Dimension(80, buttonContent.getPreferredSize().height));
            rightButtonContent.add(valorSomaButton);

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

            relatorioButton.setLayout(new BorderLayout());
            relatorioButton.add(contentPanel);

            relatoriosDoDiaPanel.add(relatorioButton);
        }
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
        btnFiltros.addActionListener(e -> {
            filterButtonsPanel.setVisible(!filterButtonsPanel.isVisible());
            revalidate();
            repaint();
        });

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

        filterButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterButtonsPanel.setVisible(false);

        ShadowButton pixButton = btn_azul(" PIX");
        pixButton.setIcon(resizeIcon(pix, 15,15));
        pixButton.addActionListener(e -> {
            if (selectedDate != null) {
                saldoBtn.setText("Saldo R$ " + format(
                        relatoriosRepository.totalPorTipoEData(selectedDate, PIX)
                ));
                mostrarPorDataETipo(selectedDate, PIX);
            } else {
                saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(PIX)));
                mostrarTodasDatasPorTipo(PIX);
            }
        });

        ShadowButton dinheiroButton = btn_verde(" CÉDULAS");
        dinheiroButton.setIcon(resizeIcon(cash, 15,15));
        dinheiroButton.addActionListener(e -> {
            if (selectedDate != null) {
                saldoBtn.setText("Saldo R$ " + format(
                        relatoriosRepository.totalPorTipoEData(selectedDate, DINHEIRO)
                ));
                List<RelatoriosResponse.Relatorios.RelatorioDoDia> dia =
                        relatoriosRepository.buscaPorDataETipo(selectedDate, DINHEIRO)
                                .stream()
                                .filter(r -> r.valor() > 0)
                                .toList();
                relatoriosPanel.removeAll();
                JPanel p = montarPainelDiaETipo(selectedDate, DINHEIRO, dia);
                relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
                relatoriosPanel.add(p);
            } else {
                saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(DINHEIRO)));
                relatoriosPanel.removeAll();
                List<LocalDate> datas = relatoriosRepository.datasPorTipo(DINHEIRO);
                relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
                for (LocalDate d : datas) {
                    List<RelatoriosResponse.Relatorios.RelatorioDoDia> rows =
                            relatoriosRepository.buscaPorDataETipo(d, DINHEIRO)
                                    .stream()
                                    .filter(r -> r.valor() > 0)
                                    .toList();
                    if (!rows.isEmpty()) {
                        JPanel p = montarPainelDiaETipo(d, DINHEIRO, rows);
                        relatoriosPanel.add(p);
                    }
                }
            }
            relatoriosPanel.revalidate();
            relatoriosPanel.repaint();
        });

        ShadowButton creditoButton = btn_azul(" CARTÃO DE CRÉDITO");
        creditoButton.setIcon(resizeIcon(card, 15,15));
        creditoButton.addActionListener(e -> {
            if (selectedDate != null) {
                saldoBtn.setText("Saldo R$ " + format(
                        relatoriosRepository.totalPorTipoEData(selectedDate, CARTAO_CREDITO)
                ));
                mostrarPorDataETipo(selectedDate, CARTAO_CREDITO);
            } else {
                saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(CARTAO_CREDITO)));
                mostrarTodasDatasPorTipo(CARTAO_CREDITO);
            }
        });

        ShadowButton debitoButton = btn_azul(" CARTÃO DE DÉBITO");
        debitoButton.setIcon(resizeIcon(card, 15,15));
        debitoButton.addActionListener(e -> {
            if (selectedDate != null) {
                saldoBtn.setText("Saldo R$ " + format(
                        relatoriosRepository.totalPorTipoEData(selectedDate, CARTAO_DEBITO)
                ));
                mostrarPorDataETipo(selectedDate, CARTAO_DEBITO);
            } else {
                saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(CARTAO_DEBITO)));
                mostrarTodasDatasPorTipo(CARTAO_DEBITO);
            }
        });

        ShadowButton virtualButton = btn_azul(" CARTÃO VIRTUAL");
        virtualButton.setIcon(resizeIcon(card, 15,15));
        virtualButton.addActionListener(e -> {
            if (selectedDate != null) {
                saldoBtn.setText("Saldo R$ " + format(
                        relatoriosRepository.totalPorTipoEData(selectedDate, CARTAO_VIRTUAL)
                ));
                mostrarPorDataETipo(selectedDate, CARTAO_VIRTUAL);
            } else {
                saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(CARTAO_VIRTUAL)));
                mostrarTodasDatasPorTipo(CARTAO_VIRTUAL);
            }
        });

        ShadowButton transfButton = btn_azul(" TRANSFERÊNCIA BANCÁRIA");
        transfButton.setIcon(resizeIcon(bank, 15,15));
        transfButton.addActionListener(e -> {
            if (selectedDate != null) {
                saldoBtn.setText("Saldo R$ " + format(
                        relatoriosRepository.totalPorTipoEData(selectedDate, TRANSFERENCIA_BANCARIA)
                ));
                mostrarPorDataETipo(selectedDate, TRANSFERENCIA_BANCARIA);
            } else {
                saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(TRANSFERENCIA_BANCARIA)));
                mostrarTodasDatasPorTipo(TRANSFERENCIA_BANCARIA);
            }
        });

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
                relatoriosPanel.removeAll();
                JPanel p = montarPainelDiaETipo(selectedDate, DINHEIRO, dia);
                relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
                relatoriosPanel.add(p);
                relatoriosPanel.revalidate();
                relatoriosPanel.repaint();
            } else {
                saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalNegativo()));
                mostrarTodasDatasRetirada();
            }
        });

        filterButtonsPanel.add(pixButton);
        filterButtonsPanel.add(dinheiroButton);
        filterButtonsPanel.add(creditoButton);
        filterButtonsPanel.add(debitoButton);
        filterButtonsPanel.add(virtualButton);
        filterButtonsPanel.add(transfButton);
        filterButtonsPanel.add(retiradaButton);

        identificadorPanel.add(filterButtonsPanel, BorderLayout.SOUTH);

        topPanel.setBorder(createEmptyBorder(10, 10, 10, 10));
        add(topPanel, BorderLayout.NORTH);

        relatoriosPanel = new JPanel();
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));

        var responseData = response.relatorios();
        for (RelatoriosResponse.Relatorios r : responseData) {
            JPanel relatorioDiaPanel = new JPanel(new BorderLayout());
            relatorioDiaPanel.setBackground(BACKGROUND_GRAY);
            relatorioDiaPanel.setBorder(createEmptyBorder(5, 20, 5, 10));

            var dataBtn = btn_branco(r.data());
            var totalDoDia = btn_branco("Total do dia: R$ " + format(r.total_do_dia()));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(BACKGROUND_GRAY);
            headerPanel.add(dataBtn, BorderLayout.WEST);
            headerPanel.add(totalDoDia, BorderLayout.EAST);
            headerPanel.setBorder(createEmptyBorder(5, 5, 0, 10));

            JPanel relatoriosDoDiaPanelInner = new JPanel(new GridLayout(0, 1, 0, 5));
            relatoriosDoDiaPanelInner.setBackground(LIGHT_GRAY);
            relatoriosDoDia(r.relatorioDoDia(), relatoriosDoDiaPanelInner);

            dataBtn.addActionListener(e -> relatoriosDoDiaPanelInner.setVisible(!relatoriosDoDiaPanelInner.isVisible()));

            relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);
            relatorioDiaPanel.add(relatoriosDoDiaPanelInner, BorderLayout.CENTER);

            relatoriosPanel.add(relatorioDiaPanel);
        }

        scrollPane = new JScrollPane(
                relatoriosPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void refreshPanel() {
        removeAll();
        initializePanel();
        revalidate();
        repaint();
    }
}
