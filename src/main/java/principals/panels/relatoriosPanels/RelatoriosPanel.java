package principals.panels.relatoriosPanels;

import buttons.BotaoComSombra;
import com.toedter.calendar.JCalendar;
import enums.TipoPagamentoEnum;
import principals.tools.CustomJCalendar;
import principals.tools.Icones;
import principals.tools.Refreshable;
import repository.RelatoriosRepository;
import response.RelatoriosResponse;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static buttons.Botoes.*;
import static java.time.format.DateTimeFormatter.ofPattern;
import static principals.tools.CorPersonalizada.*;
import static principals.tools.FormatarFloat.format;
import static principals.tools.Icones.*;
import static principals.tools.Resize.resizeIcon;

public class RelatoriosPanel extends JPanel implements Refreshable {

    private final RelatoriosRepository relatoriosRepository;
    private JPanel relatoriosPanel;
    private JScrollPane scrollPane;
    private JCalendar jCalendar;
    private JButton btnPesquisar;
    private JButton btnAdicionar;
    private JButton btnFiltros;
    private JPanel filterButtonsPanel;
    private LocalDate selectedDate;
    private BotaoComSombra saldoBtn;

    public RelatoriosPanel(RelatoriosRepository relatoriosRepository) {
        this.relatoriosRepository = relatoriosRepository;
        refreshPanel();
    }

    private final PropertyChangeListener calendarioListener = evt -> {
        if (!"day".equals(evt.getPropertyName())) return;
        if (jCalendar.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma data válida.");
            return;
        }
        selectedDate = new Date(jCalendar.getDate().getTime()).toLocalDate();
        mostrarPorData(selectedDate);
    };

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
                JPanel p = montarPainelDiaETipo(d, TipoPagamentoEnum.DINHEIRO, rows);
                relatoriosPanel.add(p);
            }
        }
        relatoriosPanel.revalidate();
        relatoriosPanel.repaint();
    }

    private JPanel montarPainelDia(RelatoriosResponse.Relatorios relatorio) {
        JPanel relatorioDiaPanel = new JPanel(new BorderLayout());
        relatorioDiaPanel.setBackground(BACKGROUND_GRAY);
        relatorioDiaPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 10));
        var dataBtn = btn_branco(relatorio.data());
        var totalBtn = btn_branco("Total do dia: R$ " + format(relatorio.total_do_dia()));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.add(dataBtn, BorderLayout.WEST);
        headerPanel.add(totalBtn, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 10));
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
        relatorioDiaPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 10));
        var dataStr = data.format(ofPattern("dd/MM/yyyy"));
        var dataBtn = btn_branco(dataStr);
        var filtroBtn = btn_branco("Filtro: " + tipo.name());
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.add(dataBtn, BorderLayout.WEST);
        headerPanel.add(filtroBtn, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 10));
        relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);
        JPanel relatoriosDoDiaPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        relatoriosDoDiaPanel.setBackground(LIGHT_GRAY);
        relatoriosDoDia(lista, relatoriosDoDiaPanel);
        relatorioDiaPanel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);
        dataBtn.addActionListener(e -> relatoriosDoDiaPanel.setVisible(!relatoriosDoDiaPanel.isVisible()));
        return relatorioDiaPanel;
    }

    private JPanel montarPainelTipo(TipoPagamentoEnum tipo,
                                    List<RelatoriosResponse.Relatorios.RelatorioDoDia> lista) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(BACKGROUND_GRAY);
        painel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 10));
        var filtroBtn = btn_branco("Filtro: " + tipo.name());
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.add(filtroBtn, BorderLayout.WEST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 10));
        painel.add(headerPanel, BorderLayout.NORTH);
        JPanel relatoriosDoDiaPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        relatoriosDoDiaPanel.setBackground(LIGHT_GRAY);
        relatoriosDoDia(lista, relatoriosDoDiaPanel);
        painel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);
        return painel;
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
            relatorioButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            relatorioButton.setBorderPainted(false);
            relatorioButton.setFocusPainted(false);
            relatorioButton.setContentAreaFilled(true);
            relatorioButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JPanel buttonContent = new JPanel(new BorderLayout());
            buttonContent.setBackground(Color.WHITE);
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
            descricaoPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
            descricaoPanel.setPreferredSize(new Dimension(900, 40));
            var valorPagamentoButton = new BotaoComSombra();
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
            rightPanel.setBackground(Color.WHITE);
            rightPanel.add(valorPagamentoButton);
            buttonContent.add(leftPanel, BorderLayout.WEST);
            buttonContent.add(rightPanel, BorderLayout.EAST);
            relatorioButton.add(buttonContent, BorderLayout.CENTER);
            relatoriosDoDiaPanel.add(relatorioButton);
        }
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        RelatoriosResponse response = relatoriosRepository.relatoriosResponse();

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel identificadorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
//        identificadorPanel.setBorder(BorderFactory.createEmptyBorder(7, 5, 5, 5));
        topPanel.add(identificadorPanel, BorderLayout.CENTER);

        btnPesquisar = btn_branco("Pesquisar");
        btnPesquisar.setIcon(resizeIcon(search, 15, 15));
        btnPesquisar.addActionListener(e -> {
            jCalendar.setVisible(!jCalendar.isVisible());
            btnPesquisar.setVisible(false);
            btnAdicionar.setVisible(false);
            filterButtonsPanel.setVisible(true);
            revalidate();
            repaint();
        });

        btnAdicionar = btn_verde("Adicionar Relatório");
        btnAdicionar.setIcon(resizeIcon(plus, 15, 15));
        btnAdicionar.addActionListener(e -> new AdicionarRelatorioFrame(relatoriosRepository, RelatoriosPanel.this));

        btnFiltros = btn_branco("Filtros");
        btnFiltros.addActionListener(e -> {
            filterButtonsPanel.setVisible(!filterButtonsPanel.isVisible());
            revalidate();
            repaint();
        });

        identificadorPanel.add(btnPesquisar);
        identificadorPanel.add(btnAdicionar);
        identificadorPanel.add(btnFiltros);

        saldoBtn = btn_verde("Saldo R$ " + format(relatoriosRepository.totalPorTipo(TipoPagamentoEnum.DINHEIRO)));
        saldoBtn.setPreferredSize(new Dimension(300, 50));
        saldoBtn.setMaximumSize(new Dimension(300, 50));
        saldoBtn.setMinimumSize(new Dimension(300, 50));

        topPanel.add(saldoBtn, BorderLayout.EAST);

        CustomJCalendar customJCalendar = new CustomJCalendar();
        jCalendar = customJCalendar.createCustomCalendar();
        jCalendar.setVisible(false);
        jCalendar.getDayChooser().addPropertyChangeListener(calendarioListener);
        identificadorPanel.add(jCalendar);

        filterButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterButtonsPanel.setVisible(false);

        BotaoComSombra pixButton = btn_azul("PIX");
        pixButton.addActionListener(e -> {
            saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(TipoPagamentoEnum.PIX)));
            if (selectedDate != null) {
                mostrarPorDataETipo(selectedDate, TipoPagamentoEnum.PIX);
            } else {
                mostrarTodasDatasPorTipo(TipoPagamentoEnum.PIX);
            }
        });

        BotaoComSombra dinheiroButton = btn_verde("CÉDULAS");
        dinheiroButton.addActionListener(e -> {
            saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(TipoPagamentoEnum.DINHEIRO)));
            if (selectedDate != null) {
                relatoriosPanel.removeAll();
                List<RelatoriosResponse.Relatorios.RelatorioDoDia> dia =
                        relatoriosRepository.buscaPorDataETipo(selectedDate, TipoPagamentoEnum.DINHEIRO)
                                .stream()
                                .filter(r -> r.valor() > 0)
                                .toList();
                JPanel p = montarPainelDiaETipo(selectedDate, TipoPagamentoEnum.DINHEIRO, dia);
                relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
                relatoriosPanel.add(p);
                relatoriosPanel.revalidate();
                relatoriosPanel.repaint();
            } else {
                relatoriosPanel.removeAll();
                List<LocalDate> datas = relatoriosRepository.datasPorTipo(TipoPagamentoEnum.DINHEIRO);
                relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
                for (LocalDate d : datas) {
                    List<RelatoriosResponse.Relatorios.RelatorioDoDia> rows =
                            relatoriosRepository.buscaPorDataETipo(d, TipoPagamentoEnum.DINHEIRO)
                                    .stream()
                                    .filter(r -> r.valor() > 0)
                                    .toList();
                    if (!rows.isEmpty()) {
                        JPanel p = montarPainelDiaETipo(d, TipoPagamentoEnum.DINHEIRO, rows);
                        relatoriosPanel.add(p);
                    }
                }
                relatoriosPanel.revalidate();
                relatoriosPanel.repaint();
            }
        });

        BotaoComSombra creditoButton = btn_azul("CARTÃO DE CRÉDITO:");
        creditoButton.addActionListener(e -> {
            saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(TipoPagamentoEnum.CARTAO_CREDITO)));
            if (selectedDate != null) {
                mostrarPorDataETipo(selectedDate, TipoPagamentoEnum.CARTAO_CREDITO);
            } else {
                mostrarTodasDatasPorTipo(TipoPagamentoEnum.CARTAO_CREDITO);
            }
        });

        BotaoComSombra debitoButton = btn_azul("CARTÃO DE DÉBITO");
        debitoButton.addActionListener(e -> {
            saldoBtn.setText("Saldo: R$ " + format(relatoriosRepository.totalPorTipo(TipoPagamentoEnum.CARTAO_DEBITO)));
            if (selectedDate != null) {
                mostrarPorDataETipo(selectedDate, TipoPagamentoEnum.CARTAO_DEBITO);
            } else {
                mostrarTodasDatasPorTipo(TipoPagamentoEnum.CARTAO_DEBITO);
            }
        });

        BotaoComSombra virtualButton = btn_azul("CARTÃO VIRTUAL");
        virtualButton.addActionListener(e -> {
            saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(TipoPagamentoEnum.CARTAO_VIRTUAL)));
            if (selectedDate != null) {
                mostrarPorDataETipo(selectedDate, TipoPagamentoEnum.CARTAO_VIRTUAL);
            } else {
                mostrarTodasDatasPorTipo(TipoPagamentoEnum.CARTAO_VIRTUAL);
            }
        });

        BotaoComSombra transfButton = btn_azul("TRANSFERÊNCIA BANCÁRIA");
        transfButton.addActionListener(e -> {
            saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalPorTipo(TipoPagamentoEnum.TRANSFERENCIA_BANCARIA)));
            if (selectedDate != null) {
                mostrarPorDataETipo(selectedDate, TipoPagamentoEnum.TRANSFERENCIA_BANCARIA);
            } else {
                mostrarTodasDatasPorTipo(TipoPagamentoEnum.TRANSFERENCIA_BANCARIA);
            }
        });

        BotaoComSombra retiradaButton = btn_vermelho("RETIRADA");
        retiradaButton.addActionListener(e -> {
            saldoBtn.setText("Saldo R$ " + format(relatoriosRepository.totalNegativo()));
            if (selectedDate != null) {
                List<RelatoriosResponse.Relatorios.RelatorioDoDia> dia =
                        relatoriosRepository.buscaPorDataETipo(selectedDate, TipoPagamentoEnum.DINHEIRO);
                List<RelatoriosResponse.Relatorios.RelatorioDoDia> retiradas =
                        dia.stream().filter(r -> r.valor() < 0).toList();
                relatoriosPanel.removeAll();
                JPanel p = montarPainelDiaETipo(selectedDate, TipoPagamentoEnum.DINHEIRO, retiradas);
                relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
                relatoriosPanel.add(p);
                relatoriosPanel.revalidate();
                relatoriosPanel.repaint();
            } else {
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
        identificadorPanel.add(filterButtonsPanel);

        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topPanel, BorderLayout.NORTH);

        relatoriosPanel = new JPanel();
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));
        var responseData = response.relatorios();
        for (RelatoriosResponse.Relatorios r : responseData) {
            JPanel relatorioDiaPanel = new JPanel(new BorderLayout());
            relatorioDiaPanel.setBackground(BACKGROUND_GRAY);
            relatorioDiaPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 10));

            var dataBtn = btn_branco(r.data());
            var totalDoDia = btn_branco("Total do dia: R$ " + format(r.total_do_dia()));
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(BACKGROUND_GRAY);
            headerPanel.add(dataBtn, BorderLayout.WEST);
            headerPanel.add(totalDoDia, BorderLayout.EAST);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 10));

            JPanel relatoriosDoDiaPanel = new JPanel(new GridLayout(0, 1, 0, 5));
            relatoriosDoDiaPanel.setBackground(LIGHT_GRAY);
            relatoriosDoDia(r.relatorioDoDia(), relatoriosDoDiaPanel);

            dataBtn.addActionListener(e -> relatoriosDoDiaPanel.setVisible(!relatoriosDoDiaPanel.isVisible()));

            relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);
            relatorioDiaPanel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);
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
