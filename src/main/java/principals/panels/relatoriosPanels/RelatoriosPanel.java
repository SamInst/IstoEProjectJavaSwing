package principals.panels.relatoriosPanels;

import buttons.BotaoComSombra;
import com.toedter.calendar.JCalendar;
import principals.tools.*;
import repository.RelatoriosRepository;
import response.RelatoriosResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static buttons.Botoes.*;
import static principals.tools.CorPersonalizada.CINZA_CLARO;
import static principals.tools.CorPersonalizada.CINZA_ESCURO;
import static principals.tools.Icones.*;
import static principals.tools.Resize.resizeIcon;

public class RelatoriosPanel extends JPanel implements Refreshable {
    private final RelatoriosRepository relatoriosRepository;

    public RelatoriosPanel(RelatoriosRepository relatoriosRepository) {
        this.relatoriosRepository = relatoriosRepository;
        refreshPanel();
    }

    public void sumarioPanel(JPanel sumarioPanel) {
        BotaoComSombra cartao = btn_azul("Cartão/PIX");
        cartao.setIcon(resizeIcon(card, 15, 15));

        BotaoComSombra dinheiro = btn_verde("Dinheiro");
        dinheiro.setIcon(resizeIcon(Icones.dinheiro, 15, 15));

        BotaoComSombra retirada = btn_vermelho("Retirada");
        retirada.setIcon(resizeIcon(cash_out, 15, 15));

        sumarioPanel.add(cartao);
        sumarioPanel.add(dinheiro, FlowLayout.LEFT);
        sumarioPanel.add(retirada);
    }

    public void buscarRelatorioPorData(RelatoriosRepository relatoriosRepository) {
        JFrame janelaPesquisar = new JFrame("Pesquisar Relatórios por Data");
        janelaPesquisar.setBackground(Color.BLUE);

        CustomJCalendar customJCalendar = new CustomJCalendar();
        JCalendar jCalendar = customJCalendar.createCustomCalendar();

        JPanel painelPesquisa = new JPanel();
        painelPesquisa.add(jCalendar);

        JPanel painelResultado = new JPanel();
        painelResultado.setLayout(new BoxLayout(painelResultado, BoxLayout.Y_AXIS));

        janelaPesquisar.add(painelPesquisa, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(painelResultado);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        janelaPesquisar.add(scrollPane, BorderLayout.CENTER);

        jCalendar.getDayChooser().addPropertyChangeListener("day", evt -> {
            painelResultado.removeAll();

            java.util.Date selectedDate = jCalendar.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(janelaPesquisar, "Por favor, selecione uma data válida.");
                return;
            }

            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
            LocalDate data = sqlDate.toLocalDate();

            var relatorio = relatoriosRepository.buscaRelatorioPorData(data);

            JPanel relatorioDiaPanel = new JPanel();
            relatorioDiaPanel.setBackground(Color.white);
            relatorioDiaPanel.setLayout(new BorderLayout());
            relatorioDiaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel dataLabel = new JLabel(relatorio.data());
            dataLabel.setForeground(Color.white);
            dataLabel.setFont(new Font("Roboto", Font.BOLD, 19));
            JLabel totalDoDiaLabel = new JLabel("Total do dia: R$ " + FormatarFloat.format(relatorio.total_do_dia()));
            totalDoDiaLabel.setForeground(Color.white);
            totalDoDiaLabel.setFont(new Font("Roboto", Font.BOLD, 20));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(66, 75, 152));
            headerPanel.add(dataLabel, BorderLayout.WEST);
            headerPanel.add(totalDoDiaLabel, BorderLayout.EAST);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 45));

            relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);

            JPanel relatoriosDoDiaPanel = new JPanel();
            relatoriosDoDiaPanel.setLayout(new GridLayout(0, 1, 0, 1));
            relatoriosDoDiaPanel.setBackground(CINZA_CLARO);
            relatoriosDoDiaPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

            relatoriosDoDia(relatorio.relatorioDoDia(), relatoriosDoDiaPanel);

            relatorioDiaPanel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);

            painelResultado.add(relatorioDiaPanel);
            painelResultado.revalidate();
            painelResultado.repaint();
        });

        janelaPesquisar.setSize(1900, 1000);
        janelaPesquisar.setBackground(Color.BLUE);
        janelaPesquisar.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        janelaPesquisar.setLocationRelativeTo(null);
        janelaPesquisar.setVisible(true);
    }


    public void relatoriosDoDia(List<RelatoriosResponse.Relatorios.RelatorioDoDia> relatorioDoDiaList, JPanel relatoriosDoDiaPanel) {
        for (RelatoriosResponse.Relatorios.RelatorioDoDia relatorioDoDia : relatorioDoDiaList) {
            JButton relatorioButton;
            relatorioButton = new JButton();
            relatorioButton.setBackground(Color.WHITE);
            relatorioButton.setBorderPainted(false);
            relatorioButton.setFocusPainted(false);
            relatorioButton.setContentAreaFilled(true);
            relatorioButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel buttonContent = new JPanel(new BorderLayout());
            buttonContent.setBackground(Color.WHITE);

            var tipoPagamentoButton = new BotaoComSombra();
            var valorPagamentoButton = new BotaoComSombra();

            switch (relatorioDoDia.tipo_pagamento()) {
                case "0" -> tipoPagamentoButton = btn_azul("PIX");
                case "1" -> tipoPagamentoButton = relatorioDoDia.valor() < 0 ? btn_vermelho("DINHEIRO") : btn_verde("DINHEIRO");
                case "2" -> tipoPagamentoButton = btn_azul("CARTÃO DE CRÉDITO");
                case "3" -> tipoPagamentoButton = btn_azul("CARTÃO DE DÉBITO");
                case "4" -> tipoPagamentoButton = btn_azul("CARTÃO VIRTUAL");
                case "5" -> tipoPagamentoButton = btn_azul("TRANSFERÊNCIA BANCÁRIA");
                default -> tipoPagamentoButton = btn_cinza("DESCONHECIDO");
            }

            var valor = "R$ " + FormatarFloat.format(relatorioDoDia.valor());
            var btn_hora = btn_branco(relatorioDoDia.horario().format(DateTimeFormatter.ofPattern("HH:mm")));
            var btn_quarto = relatorioDoDia.quarto_id() == 0 ? btn_branco(" 00 ") : btn_cinza(relatorioDoDia.quarto_id() < 10 ?
                    " 0" + relatorioDoDia.quarto_id() + " " : " " + relatorioDoDia.quarto_id() + " ");
            btn_quarto.setForeground(Color.WHITE);

            if (relatorioDoDia.valor() < 0) valorPagamentoButton = btn_vermelho(valor);
            else if (Integer.parseInt(relatorioDoDia.tipo_pagamento()) == 1) valorPagamentoButton = btn_verde(valor);
            else valorPagamentoButton = btn_azul(valor);

            JLabel descricao = new JLabel(relatorioDoDia.relatorio());
            descricao.setBorder(BorderFactory.createEmptyBorder(4, 20, 10, 10));
            descricao.setForeground(CINZA_ESCURO);

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.setOpaque(false);
            leftPanel.setBackground(Color.WHITE);
            leftPanel.add(btn_quarto);
            leftPanel.add(btn_hora);
            leftPanel.add(tipoPagamentoButton);
            leftPanel.add(descricao);

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

        JPanel identificadorPanel = new JPanel();
        identificadorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        identificadorPanel.setBorder(BorderFactory.createEmptyBorder(7, 5, 5, 5));

        topPanel.add(identificadorPanel);

        var btnPesquisar = btn_branco("Pesquisar");
        btnPesquisar.setIcon(resizeIcon(search, 15, 15));
        btnPesquisar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarRelatorioPorData(relatoriosRepository);
            }
        });

        var btnAdicionar = btn_verde("Adicionar Relatório");
        btnAdicionar.setIcon(resizeIcon(plus, 15, 15));
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdicionarRelatorioFrame(relatoriosRepository, RelatoriosPanel.this);
            }
        });

        JPanel sumarioPanel = new JPanel();
//        sumarioPanel(sumarioPanel);
        identificadorPanel.add(btnPesquisar);
        identificadorPanel.add(btnAdicionar);
        identificadorPanel.add(sumarioPanel);
        var total = btn_verde("Total: R$ " + FormatarFloat.format(response.total()));
        total.setFont(new Font("Arial", Font.PLAIN, 18));
        total.setPreferredSize(new Dimension(200, 50));

        topPanel.add(total, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(topPanel, BorderLayout.NORTH);

        JPanel relatoriosPanel = new JPanel();
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));

        for (RelatoriosResponse.Relatorios relatorio : response.relatorios()) {
            JPanel relatorioDiaPanel = new JPanel();
            relatorioDiaPanel.setBackground(new Color(0xF2F2F2));
            relatorioDiaPanel.setLayout(new BorderLayout());
            relatorioDiaPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            var data = btn_branco(relatorio.data());
            JLabel dataLabel = new JLabel(relatorio.data());
            dataLabel.setForeground(Color.white);
            dataLabel.setFont(new Font("Roboto", Font.PLAIN, 20));

            var totalDoDia = btn_branco("Total do dia: R$ " + FormatarFloat.format(relatorio.total_do_dia()));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(0xD2D2D2));
            headerPanel.add(data, BorderLayout.WEST);
            headerPanel.add(totalDoDia, BorderLayout.EAST);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 10));

            relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);

            JPanel relatoriosDoDiaPanel = new JPanel();
            relatoriosDoDiaPanel.setLayout(new GridLayout(0, 1, 0, 1));
            relatoriosDoDiaPanel.setBackground(CorPersonalizada.CINZA_CLARO);
            relatoriosDoDiaPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            relatoriosDoDiaPanel.setPreferredSize(null);
            relatoriosDoDiaPanel.setMinimumSize(null);
            relatoriosDoDiaPanel.setMaximumSize(null);

            relatoriosDoDia(relatorio.relatorioDoDia(), relatoriosDoDiaPanel);

            relatorioDiaPanel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);
            relatoriosPanel.add(relatorioDiaPanel);
        }

        JScrollPane scrollPane = new JScrollPane(relatoriosPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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
