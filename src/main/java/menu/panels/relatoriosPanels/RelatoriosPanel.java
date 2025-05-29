package menu.panels.relatoriosPanels;

import buttons.ShadowButton;
import calendar.DateChooser;
import calendar.EventDateChooser;
import calendar.SelectedAction;
import calendar.SelectedDate;
import enums.TipoPagamentoEnum;
import lateralMenu.tabbed.TabbedForm;
import panels.PanelPopup;
import repository.RelatoriosRepository;
import response.RelatoriosResponse;
import tools.FormatarFloat;
import tools.Refreshable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static buttons.Botoes.*;
import static enums.TipoPagamentoEnum.*;
import static java.lang.Integer.parseInt;
import static java.time.format.DateTimeFormatter.ofPattern;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static notifications.Notification.notification;
import static notifications.Notifications.Location.TOP_CENTER;
import static notifications.Notifications.Type.INFO;
import static response.RelatoriosResponse.Relatorios.RelatorioDoDia;
import static tools.CorPersonalizada.*;
import static tools.FormatarFloat.format;
import static tools.Icones.*;
import static tools.Resize.resizeIcon;

public class RelatoriosPanel extends TabbedForm implements Refreshable {
    private final RelatoriosRepository relatoriosRepository;
    private JPanel relatoriosPanel;
    private JScrollPane scrollPane;
    private JButton btnAdicionar;
    private ShadowButton btnFiltros;
    private LocalDate selectedDate;
    private ShadowButton saldoBtn;
    private final DateChooser dateChooser = new DateChooser();
    private final ShadowButton searchDateField = new ShadowButton();
    Font font = new Font("Roboto", Font.PLAIN, 16);

    public RelatoriosPanel(RelatoriosRepository relatoriosRepository) {
        this.relatoriosRepository = relatoriosRepository;
        refreshPanel();
    }

    private void mostrarPorData(LocalDate data) {
        updatePanel(() -> {
            var relatorio = relatoriosRepository.buscaRelatorioPorData(data);
            relatoriosPanel.add(montarPainelDataEDia(relatorio));
        });
    }

    private void mostrarPorDataETipo(LocalDate data, TipoPagamentoEnum tipo) {
        updatePanel(() -> {
            List<RelatorioDoDia> lista = relatoriosRepository.buscaPorDataETipo(data, tipo);
            relatoriosPanel.add(montarPainelDiaETipo(data, tipo, lista));
        });
    }

    private void mostrarTodasDatasPorTipo(TipoPagamentoEnum tipo) {
        updatePanel(() -> {
            List<LocalDate> datas = relatoriosRepository.datasPorTipo(tipo);
            datas.forEach(d -> {
                List<RelatorioDoDia> rows = relatoriosRepository.buscaPorDataETipo(d, tipo);
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
                List<RelatorioDoDia> rows = relatoriosRepository.buscaRetiradaPorData(d);
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

    private JPanel montarPainelDataEDia(RelatoriosResponse.Relatorios relatorio) {

        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(BACKGROUND_GRAY);
        background.setBorder(createEmptyBorder(15, 20, 5, 20));

        var dataBtn = btn_branco(relatorio.data());
        dataBtn.setBackground(BACKGROUND_GRAY);
        dataBtn.setFont(font);

        var totalBtn = btn_branco("Total do dia: R$ " + format(relatorio.total_do_dia()));
        totalBtn.setBackground(BACKGROUND_GRAY);
        totalBtn.setFont(font);

        JPanel headersContainer = new JPanel(new GridLayout(2, 1));
        headersContainer.setBackground(WHITE);

        JPanel headerPanel = createHeaderPanel(dataBtn, totalBtn);
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel sumarioPanel = new JPanel(new BorderLayout());
        sumarioPanel.setBackground(WHITE);
        sumarioPanel.setBorder(createEmptyBorder(5, 15, 5, 30));

        JLabel aptLabel = new JLabel("Apt");
        aptLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        aptLabel.setFont(font);

        JLabel horaLabel = new JLabel("Hora");
        horaLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 35));
        horaLabel.setFont(font);

        JLabel relatorioLabel = new JLabel("Relatorio");
        relatorioLabel.setFont(font);

        JLabel valorLabel = new JLabel("Valor");
        valorLabel.setFont(font);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(WHITE);
        leftPanel.add(aptLabel);
        leftPanel.add(horaLabel);
        leftPanel.add(relatorioLabel);

        sumarioPanel.add(leftPanel, BorderLayout.WEST);
        sumarioPanel.add(valorLabel, BorderLayout.EAST);

        headersContainer.add(headerPanel);
        headersContainer.add(sumarioPanel);

        background.add(headersContainer, BorderLayout.NORTH);

        JPanel relatoriosDoDiaPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        relatoriosDoDiaPanel.setBackground(LIGHT_GRAY_2);
        relatoriosDoDia(relatorio.relatorioDoDia(), relatoriosDoDiaPanel);
        background.add(relatoriosDoDiaPanel, BorderLayout.CENTER);

        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                headerPanel.setBackground(getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                headerPanel.setBackground(BACKGROUND_GRAY);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                relatoriosDoDiaPanel.setVisible(!relatoriosDoDiaPanel.isVisible());
            }
        });
        return background;
    }

    private JPanel montarPainelDiaETipo(LocalDate data, TipoPagamentoEnum tipo, List<RelatorioDoDia> lista) {
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

    public void relatoriosDoDia(List<RelatorioDoDia> lista,
                                JPanel relatoriosDoDiaPanel) {
        lista.forEach(relatorioDoDia -> {
            JButton relatorioButton = createRelatorioButton(relatorioDoDia);
            relatoriosDoDiaPanel.add(relatorioButton);
        });
    }

    private JButton createRelatorioButton(RelatorioDoDia relatorioDoDia) {
        JButton fitaLateralRelatorio = new JButton();
        var tipoPagamentoButton = btn_branco("DESCONHECIDO");
        JLabel tipoPagamentoText = new JLabel("DESCONHECIDO");

        switch (relatorioDoDia.tipo_pagamento()) {
            case "0" -> configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA PIX", BLUE);
            case "1" -> {
                if (relatorioDoDia.valor() < 0) {
                    configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "RETIRADA VIA DINHEIRO", RED_2);
                } else {
                    configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA DINHEIRO", GREEN);
                }
            }
            case "2" ->
                    configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA CARTÃO DE CRÉDITO", BLUE);
            case "3" ->
                    configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA CARTÃO DE DÉBITO", BLUE);
            case "4" ->
                    configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA CARTÃO VIRTUAL", BLUE);
            case "5" ->
                    configureTipoPagamento(tipoPagamentoText, tipoPagamentoButton, "PAGAMENTO VIA TRANSFERÊNCIA BANCÁRIA", BLUE);
        }

        fitaLateralRelatorio.setBackground(tipoPagamentoButton.getForeground());
        fitaLateralRelatorio.setBorder(createEmptyBorder(0, 5, 0, 0));
        fitaLateralRelatorio.setBorderPainted(false);
        fitaLateralRelatorio.setFocusPainted(false);
        fitaLateralRelatorio.setContentAreaFilled(true);
        fitaLateralRelatorio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel buttonContent = createButtonContent(relatorioDoDia, tipoPagamentoText);
        fitaLateralRelatorio.setLayout(new BorderLayout());
        fitaLateralRelatorio.add(buttonContent);

        return fitaLateralRelatorio;
    }

    private void configureTipoPagamento(JLabel label, JButton button, String text, Color color) {
        label.setText(text);
        label.setForeground(color);
        button.setForeground(color);
    }

    private JPanel createButtonContent(RelatorioDoDia relatorioDoDia, JLabel tipoPagamentoText) {
        JPanel buttonContent = new JPanel(new BorderLayout());
        Dimension dimension = new Dimension(100, 80);
        buttonContent.setPreferredSize(dimension);
        buttonContent.setBackground(WHITE);

        ShadowButton valorSomaButton =
                btn_backgroung(format(relatoriosRepository.somaValorRelatorioMaisAnteriores(relatorioDoDia.relatorio_id())));

        var relatorio = relatoriosRepository.compararRelatorioAnterior(relatorioDoDia.relatorio_id(), parseInt(relatorioDoDia.tipo_pagamento()));

        if (relatorio == 0) {
            valorSomaButton.setIcon(resizeIcon(equal, 15, 15));
        }

        if (relatorio == 1) {
            valorSomaButton.setForeground(RED_2);
            valorSomaButton.setIcon(resizeIcon(down_single_arrow, 15, 15));
        }

        if (relatorio == 2) {
            valorSomaButton.setForeground(GREEN);
            valorSomaButton.setIcon(resizeIcon(up_single_arrow, 15, 15));
        }

        JPanel rightButtonContent = new JPanel();
        rightButtonContent.setBackground(WHITE);
        rightButtonContent.setPreferredSize(new Dimension(100, buttonContent.getPreferredSize().height));
        rightButtonContent.add(valorSomaButton);
        rightButtonContent.setVisible(false);

        var valor = "R$ " + format(relatorioDoDia.valor());
        var btn_hora = btn_branco(relatorioDoDia.horario().format(ofPattern("HH:mm")));

        var btnVazio = btn_branco(" 00 ");
        btnVazio.setBackground(BACKGROUND_GRAY);
        var btn_quarto =
                relatorioDoDia.quarto_id() == 0
                        ? btnVazio
                        : btn_cinza(
                        relatorioDoDia.quarto_id() < 10
                                ? " 0" + relatorioDoDia.quarto_id() + " "
                                : " " + relatorioDoDia.quarto_id() + " "
                );
        btn_quarto.setForeground(BACKGROUND_GRAY);

        JPanel descricaoPanel = new JPanel();
        descricaoPanel.setLayout(new BoxLayout(descricaoPanel, BoxLayout.Y_AXIS));
        descricaoPanel.setBackground(WHITE);
        descricaoPanel.setBorder(createEmptyBorder(3, 20, 0, 0));

        ShadowButton valorPagamentoButton = btn_branco(valor);
        valorPagamentoButton.setBackground(WHITE);
        valorPagamentoButton.setFont(font);

        if (relatorioDoDia.valor() < 0) {
            valorPagamentoButton.setText("- R$ "+ FormatarFloat.format(relatorioDoDia.valor()).replace("-", ""));
            valorPagamentoButton.setForeground(RED_2);
        } else if ("1".equals(relatorioDoDia.tipo_pagamento())) {
            valorPagamentoButton.setText("+ R$ "+ FormatarFloat.format(relatorioDoDia.valor()).replace("-", ""));
            valorPagamentoButton.setForeground(GREEN);
        } else {
            valorPagamentoButton.setForeground(BLUE);
        }

        JLabel id_relatorio = new JLabel("#" +relatorioDoDia.relatorio_id());
        id_relatorio.setBorder(null);
        id_relatorio.setForeground(RED_4);

        JLabel descricao = new JLabel(relatorioDoDia.relatorio());
        descricao.setFont(font);
        descricao.setBorder(null);
        descricao.setForeground(DARK_GRAY.brighter());
        descricao.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(btn_quarto);
        leftPanel.add(btn_hora);

        descricaoPanel.add(descricao);
        descricaoPanel.add(tipoPagamentoText);
        descricaoPanel.add(id_relatorio);

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

        searchDateField.setIcon(resizeIcon(search, 15, 15));
        dateChooser.setTextReference(searchDateField, true);

        dateChooser.addEventDateChooser(new EventDateChooser() {
            @Override
            public void dateSelected(SelectedAction action, SelectedDate date) {
                if (action.getAction() == SelectedAction.DAY_SELECTED) {
                    dateChooser.hidePopup();
                    LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
                    String monthName = Month.of(date.getMonth())
                            .getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

                    selectedDate = localDate;
                    mostrarPorData(localDate);
                    notification(INFO, TOP_CENTER, "Buscando por data\n"
                            + date.getDay() + " de " + monthName + " de " + date.getYear());
                }
            }
        });

        searchDateField.addActionListener(e -> dateChooser.showPopup());

        btnAdicionar = btn_verde(" Adicionar Relatório");
        btnAdicionar.setIcon(resizeIcon(plus, 15, 15));

        btnFiltros = btn_branco(" Filtros");
        btnFiltros.setIcon(resizeIcon(filter_gray, 15, 15));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBorder(createEmptyBorder(7, 0, 0, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(searchDateField);
        buttonsPanel.add(btnAdicionar);
        buttonsPanel.add(btnFiltros);
        identificadorPanel.add(buttonsPanel, BorderLayout.NORTH);

        btnAdicionar.addActionListener(e -> {
            PanelPopup panelPopup = new PanelPopup();
            panelPopup.showPersistentPopup(
                    btnAdicionar,
                    new AdicionarRelatorio(
                            relatoriosRepository,
                            RelatoriosPanel.this
                    )
            );
        });

        saldoBtn = btn_verde("Saldo R$ " + format(relatoriosRepository.totalPorTipo(DINHEIRO)));
        saldoBtn.setPreferredSize(new Dimension(300, 50));
        saldoBtn.setMaximumSize(new Dimension(300, 50));
        saldoBtn.setMinimumSize(new Dimension(300, 50));
        rightPanel.add(saldoBtn, BorderLayout.NORTH);

        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        ShadowButton pixButton = btn_azul(" PIX");
        pixButton.setIcon(resizeIcon(pix, 15, 15));
        pixButton.addActionListener(e -> updateSaldoAndPanel(PIX));

        ShadowButton dinheiroButton = btn_verde(" DiNHEIRO");
        dinheiroButton.setIcon(resizeIcon(cash, 15, 15));
        dinheiroButton.addActionListener(e -> updateSaldoAndPanel(DINHEIRO));

        ShadowButton creditoButton = btn_azul(" CARTÃO DE CRÉDITO");
        creditoButton.setIcon(resizeIcon(card, 15, 15));
        creditoButton.addActionListener(e -> updateSaldoAndPanel(CARTAO_CREDITO));

        ShadowButton debitoButton = btn_azul(" CARTÃO DE DÉBITO");
        debitoButton.setIcon(resizeIcon(card, 15, 15));
        debitoButton.addActionListener(e -> updateSaldoAndPanel(CARTAO_DEBITO));

        ShadowButton virtualButton = btn_azul(" CARTÃO VIRTUAL");
        virtualButton.setIcon(resizeIcon(card, 15, 15));
        virtualButton.addActionListener(e -> updateSaldoAndPanel(CARTAO_VIRTUAL));

        ShadowButton transfButton = btn_azul(" TRANSFERÊNCIA BANCÁRIA");
        transfButton.setIcon(resizeIcon(bank, 15, 15));
        transfButton.addActionListener(e -> updateSaldoAndPanel(TRANSFERENCIA_BANCARIA));

        ShadowButton retiradaButton = btn_vermelho(" RETIRADA");
        retiradaButton.setIcon(resizeIcon(cash_out, 15, 15));
        retiradaButton.addActionListener(e -> {
            if (selectedDate != null) {
                float totalNegativoNoDia = relatoriosRepository.buscaRetiradaPorData(selectedDate)
                        .stream()
                        .map(RelatorioDoDia::valor)
                        .reduce(0f, Float::sum);

                saldoBtn.setText("Saldo R$ " + format(totalNegativoNoDia));
                List<RelatorioDoDia> dia =
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

        response.relatorios().forEach(r -> relatoriosPanel.add(montarPainelDataEDia(r)));

        scrollPane = new JScrollPane(
                relatoriosPanel,
                VERTICAL_SCROLLBAR_AS_NEEDED,
                HORIZONTAL_SCROLLBAR_NEVER
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