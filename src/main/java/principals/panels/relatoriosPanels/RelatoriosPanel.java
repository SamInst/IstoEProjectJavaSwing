package principals.panels.relatoriosPanels;

import com.toedter.calendar.JCalendar;
import principals.tools.*;
import repository.RelatoriosRepository;
import response.RelatoriosResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static principals.tools.Cor.*;

public class RelatoriosPanel extends JPanel implements Refreshable{

    private final RelatoriosRepository relatoriosRepository;

    JButton relatorioButton;
    JButton btnPesquisar = new JButton("Pesquisar");
    JButton btnAdicionar = new JButton("Adicionar");

    public RelatoriosPanel(RelatoriosRepository relatoriosRepository) {
        this.relatoriosRepository = relatoriosRepository;
        refreshPanel();
    }

    public void sumarioPanel(JPanel sumarioPanel){
        sumarioPanel.setLayout(new BoxLayout(sumarioPanel, BoxLayout.Y_AXIS));

        JButton btnSumarioAzul = new JButton();
        btnSumarioAzul.setPreferredSize(new Dimension(20, 20));
        btnSumarioAzul.setBackground(AZUL_ESCURO);

        JLabel sumario = new JLabel("Cartão/PIX/Transferências");
        JPanel cartaoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cartaoPanel.add(btnSumarioAzul);
        cartaoPanel.add(sumario);

        JButton btnSumarioVerde = new JButton();
        btnSumarioVerde.setPreferredSize(new Dimension(20, 20));
        btnSumarioVerde.setBackground(VERDE_ESCURO);

        JLabel sumarioDinheiro = new JLabel("Dinheiro");
        JPanel dinheiroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dinheiroPanel.add(btnSumarioVerde);
        dinheiroPanel.add(sumarioDinheiro);

        JButton btnSumarioVermelho = new JButton();
        btnSumarioVermelho.setPreferredSize(new Dimension(20, 20));
        btnSumarioVermelho.setBackground(VERMELHO);

        JLabel sumarioRetirada = new JLabel("Retirada");
        JPanel retiradaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        retiradaPanel.add(btnSumarioVermelho);
        retiradaPanel.add(sumarioRetirada);

        sumarioPanel.add(cartaoPanel);
        sumarioPanel.add(dinheiroPanel);
        sumarioPanel.add(retiradaPanel);
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
            totalDoDiaLabel.setFont(new Font("Inter", Font.BOLD, 20));

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


    public void relatoriosDoDia(List<RelatoriosResponse.Relatorios.RelatorioDoDia> relatorioDoDiaList, JPanel relatoriosDoDiaPanel){
        for (RelatoriosResponse.Relatorios.RelatorioDoDia relatorioDoDia : relatorioDoDiaList) {
            relatorioButton = new JButton();
            relatorioButton.setBackground(Color.WHITE);
            relatorioButton.setBorderPainted(false);
            relatorioButton.setFocusPainted(false);
            relatorioButton.setContentAreaFilled(true);
            relatorioButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel buttonContent = new JPanel(new BorderLayout());
            buttonContent.setBackground(Color.WHITE);

            JLabel idLabel = new JLabel("#" + relatorioDoDia.relatorio_id());
            idLabel.setForeground(Color.RED);

            String tipoPagamento = Converter.converterTipoPagamento(relatorioDoDia.tipo_pagamento());

            JLabel horarioDescricaoLabel = new JLabel(relatorioDoDia.horario().format(DateTimeFormatter.ofPattern("HH:mm")) + "      " + relatorioDoDia.relatorio() + " (" + tipoPagamento + ")");
            horarioDescricaoLabel.setBorder(BorderFactory.createEmptyBorder(4, 20, 10, 10));
            horarioDescricaoLabel.setFont(new Font("Roboto", Font.BOLD, 15));
            horarioDescricaoLabel.setForeground(CINZA_ESCURO);

            JLabel valorLabel = new JLabel("R$ " + FormatarFloat.format(relatorioDoDia.valor()));
            valorLabel.setBorder(BorderFactory.createEmptyBorder(4, 20, 10, 10));
            valorLabel.setFont(new Font("Roboto", Font.BOLD, 15));

            if (relatorioDoDia.valor() < 0) valorLabel.setForeground(VERMELHO);
            else if (Integer.parseInt(relatorioDoDia.tipo_pagamento()) == 1) valorLabel.setForeground(VERDE_ESCURO);
            else valorLabel.setForeground(AZUL_ESCURO);

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.setOpaque(false);
            leftPanel.setBackground(Color.WHITE);
            leftPanel.add(idLabel);
            leftPanel.add(horarioDescricaoLabel);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.setOpaque(false);
            rightPanel.setBackground(Color.WHITE);
            rightPanel.add(valorLabel);

            buttonContent.add(leftPanel, BorderLayout.WEST);
            buttonContent.add(rightPanel, BorderLayout.EAST);

            relatorioButton.add(buttonContent, BorderLayout.CENTER);

            relatoriosDoDiaPanel.add(relatorioButton);

            relatorioButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    relatorioButton.setFont(new Font("Roboto", Font.PLAIN, 25));
                    relatorioButton.setBackground(CINZA_CLARO);
                    relatorioButton.setForeground(CINZA_CLARO);
                    leftPanel.setBackground(CINZA_CLARO);
                    rightPanel.setBackground(CINZA_CLARO);
                    buttonContent.setBackground(CINZA_CLARO);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    relatorioButton.setFont(new Font("Roboto", Font.PLAIN, 20));
                    relatorioButton.setBackground(Color.white);
                    leftPanel.setBackground(Color.white);
                    rightPanel.setBackground(Color.white);
                    buttonContent.setBackground(Color.white);
                }
            });
        }
    }

    private void initializePanel() {
        setLayout(new BorderLayout());

        RelatoriosResponse response = relatoriosRepository.relatoriosResponse();

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Relatórios", Icones.relatorios);
        identificadorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

        topPanel.add(identificadorPanel);

        btnPesquisar.setPreferredSize(new Dimension(125, 40));
        btnPesquisar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarRelatorioPorData(relatoriosRepository);
            }
        });

        btnAdicionar.setPreferredSize(new Dimension(125, 40));
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdicionarRelatorioFrame(relatoriosRepository, RelatoriosPanel.this);
            }
        });

        JPanel sumarioPanel = new JPanel();
        sumarioPanel(sumarioPanel);

        identificadorPanel.add(btnPesquisar);
        identificadorPanel.add(btnAdicionar);
        identificadorPanel.add(sumarioPanel);

        JLabel totalLabel = new JLabel("Total: R$ " + FormatarFloat.format(response.total()));
        totalLabel.setFont(new Font("Roboto", Font.PLAIN, 30));
        totalLabel.setForeground(VERDE_ESCURO);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 30));

        topPanel.add(totalLabel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(topPanel, BorderLayout.NORTH);

        JPanel relatoriosPanel = new JPanel();
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));

        for (RelatoriosResponse.Relatorios relatorio : response.relatorios()) {
            JPanel relatorioDiaPanel = new JPanel();
            relatorioDiaPanel.setBackground(Color.white);
            relatorioDiaPanel.setLayout(new BorderLayout());
            relatorioDiaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel dataLabel = new JLabel(relatorio.data());
            dataLabel.setForeground(Color.white);
            dataLabel.setFont(new Font("Roboto", Font.PLAIN, 20));

            JLabel totalDoDiaLabel = new JLabel("Total do dia: R$ " + FormatarFloat.format(relatorio.total_do_dia()));
            totalDoDiaLabel.setForeground(Color.white);
            totalDoDiaLabel.setFont(new Font("Roboto", Font.PLAIN, 20));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(66, 75, 152));
            headerPanel.add(dataLabel, BorderLayout.WEST);
            headerPanel.add(totalDoDiaLabel, BorderLayout.EAST);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 45));

            relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);

            JPanel relatoriosDoDiaPanel = new JPanel();
            relatoriosDoDiaPanel.setLayout(new GridLayout(0, 1, 0, 1));
            relatoriosDoDiaPanel.setBackground(Cor.CINZA_CLARO);
            relatoriosDoDiaPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
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
