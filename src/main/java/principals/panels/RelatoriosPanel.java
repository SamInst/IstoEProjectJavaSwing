package principals.panels;

import com.toedter.calendar.JCalendar;
import principals.tools.CustomJCalendar;
import repository.RelatoriosRepository;
import response.RelatoriosResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

public class RelatoriosPanel extends JPanel {
    RelatoriosRepository relatoriosRepository = new RelatoriosRepository();
    JButton btnPesquisar = new JButton("Pesquisar");
    JButton btnAdicionar = new JButton("Adicionar");

    private static final Color CINZA_CLARO = new Color(240, 240, 240);
    private static final Color CINZA_ESCURO = new Color(0x696363);
    private static final Color AZUL_ESCURO = new Color(0x424B98);
    private static final Color VERDE_ESCURO = new Color(0x148A20);
    private static final Color VERMELHO = new Color(0xF85A5A);
    public RelatoriosPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Obter os dados do relatório
        RelatoriosResponse response = relatorios();

        // Painel superior com total e botões
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.ORANGE);

        // Botões "Pesquisar" e "Adicionar"
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.ORANGE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttonPanel.setMinimumSize(new Dimension(20, 20));


        btnPesquisar.setPreferredSize(new Dimension(125, 40));

        btnPesquisar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarRelatorioPorData();
            }
        });

        btnAdicionar.setPreferredSize(new Dimension(125, 40));

        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        buttonPanel.add(btnPesquisar);
        buttonPanel.add(btnAdicionar);

        // Label do total usando o método relatorios()
        JLabel totalLabel = new JLabel("Total: R$ " + String.format("%.2f", response.total()));
        totalLabel.setFont(new Font("Inter", Font.PLAIN, 30));
        totalLabel.setForeground(VERDE_ESCURO); // Verde escuro
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 30));

        // Posicionar os botões à direita e o total à esquerda
        topPanel.add(totalLabel, BorderLayout.EAST);
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // Adicionar o painel superior ao topo
        add(topPanel, BorderLayout.NORTH);

        // Painel de relatórios principal (scrollable)
        JPanel relatoriosPanel = new JPanel();
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));  // Organizar as datas verticalmente


        // Iterar sobre os relatórios usando o método relatorios()
        for (RelatoriosResponse.Relatorios relatorio : response.relatorios()) {
            // Painel para cada dia de relatórios
            JPanel relatorioDiaPanel = new JPanel();
            relatorioDiaPanel.setBackground(Color.white);
            relatorioDiaPanel.setLayout(new BorderLayout()); // Use BorderLayout para garantir o posicionamento correto
            relatorioDiaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding no painel vermelho

            // Data e total do dia
            JLabel dataLabel = new JLabel(relatorio.data().toString());
            dataLabel.setForeground(Color.white);
            dataLabel.setFont(new Font("Inter", Font.BOLD, 20));
            JLabel totalDoDiaLabel = new JLabel("Total do dia: R$ " + String.format("%.2f", relatorio.total_do_dia()));
            totalDoDiaLabel.setForeground(Color.white);
            totalDoDiaLabel.setFont(new Font("Inter", Font.BOLD, 20));

            // Painel de cabeçalho com data e total do dia
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(66, 75, 152));
            headerPanel.add(dataLabel, BorderLayout.WEST);
            headerPanel.add(totalDoDiaLabel, BorderLayout.EAST);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 45));

            relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);

            // Painel para os relatórios do dia (FlowLayout com quebra de linha automática)
//            JPanel relatoriosDoDiaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));

            // Alterar o layout para BoxLayout
            // Alterar o layout do relatoriosDoDiaPanel para garantir que os botões se ajustem dinamicamente
            JPanel relatoriosDoDiaPanel = new JPanel();
            relatoriosDoDiaPanel.setLayout(new GridLayout(0, 1, 0, 1)); // Ajuste dinâmico vertical, com espaçamento zero
            relatoriosDoDiaPanel.setBackground(CINZA_CLARO);
            relatoriosDoDiaPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

// Remova qualquer tamanho fixo e deixe o painel se ajustar automaticamente ao conteúdo
            relatoriosDoDiaPanel.setPreferredSize(null);
            relatoriosDoDiaPanel.setMinimumSize(null);
            relatoriosDoDiaPanel.setMaximumSize(null);

// Adicionar quadrados brancos (relatórios) dentro do painel de relatórios do dia
            for (RelatoriosResponse.Relatorios.RelatorioDoDia relatorioDoDia : relatorio.relatorioDoDia()) {
                JButton relatorioButton = new JButton();

                // Ajustar o tamanho do botão
//                relatorioButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Permite que o botão ocupe toda a largura disponível
                relatorioButton.setBackground(Color.WHITE);
                relatorioButton.setBorderPainted(false); // Remove a borda do JButton
                relatorioButton.setFocusPainted(false);  // Remove a borda de foco ao clicar
                relatorioButton.setContentAreaFilled(true); // Preenche o fundo
                relatorioButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // Criar um layout interno com alinhamento à esquerda (ID e descrição) e à direita (valor)
                JPanel buttonContent = new JPanel(new BorderLayout());
                buttonContent.setBackground(Color.WHITE);

                // ID do relatório com "#" e pintado de vermelho
                JLabel idLabel = new JLabel("#" + relatorioDoDia.relatorio_id());
                idLabel.setForeground(Color.RED);

                // Traduzir o tipo de pagamento
                String tipoPagamento = switch (relatorioDoDia.tipo_pagamento()) {
                    case "0" -> "PIX";
                    case "1" -> "DINHEIRO";
                    case "2" -> "CARTAO DE CREDITO";
                    case "3" -> "CARTAO DE DEBITO";
                    case "4" -> "TRANSFERENCIA BANCARIA";
                    case "5" -> "CARTAO VIRTUAL";
                    default -> "DESCONHECIDO";
                };

                // Horário e descrição do relatório
                JLabel horarioDescricaoLabel = new JLabel(relatorioDoDia.horario() + "      " + relatorioDoDia.relatorio() + " (" + tipoPagamento + ")");
                horarioDescricaoLabel.setBorder(BorderFactory.createEmptyBorder(4, 20, 10, 10));
                horarioDescricaoLabel.setFont(new Font("Inter", Font.BOLD, 17));
                horarioDescricaoLabel.setForeground(CINZA_ESCURO);

                // Valor com cor conforme o tipo de pagamento
                JLabel valorLabel = new JLabel(String.format("R$ %.2f", relatorioDoDia.valor()));
                valorLabel.setBorder(BorderFactory.createEmptyBorder(4, 20, 10, 10));
                valorLabel.setFont(new Font("Inter", Font.BOLD, 18));
                valorLabel.setHorizontalAlignment(SwingConstants.LEFT);
                if (relatorioDoDia.valor() < 0) {
                    valorLabel.setForeground(VERMELHO);
                } else if (Integer.parseInt(relatorioDoDia.tipo_pagamento()) == 1) {
                    valorLabel.setForeground(VERDE_ESCURO);
                } else {
                    valorLabel.setForeground(AZUL_ESCURO);
                }

                // Painel para os textos à esquerda (ID, horário e descrição)
                JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                leftPanel.setOpaque(false);
                leftPanel.setBackground(Color.WHITE);
                leftPanel.add(idLabel);
                leftPanel.add(horarioDescricaoLabel);

                // Painel para o valor à direita
                JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                rightPanel.setOpaque(false);
                rightPanel.setBackground(Color.WHITE);
                rightPanel.add(valorLabel);

                // Adicionar os painéis de conteúdo ao botão
                buttonContent.add(leftPanel, BorderLayout.WEST);
                buttonContent.add(rightPanel, BorderLayout.EAST);

                // Definir o layout final do botão com o conteúdo ajustado
                relatorioButton.add(buttonContent, BorderLayout.CENTER);

                // Adicionar o botão de relatório ao painel do dia
                relatoriosDoDiaPanel.add(relatorioButton);

                // Adiciona o efeito de destaque ao passar o mouse
                relatorioButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        relatorioButton.setFont(new Font("Inter", Font.BOLD, 25));
                        relatorioButton.setBackground(CINZA_CLARO);
                        relatorioButton.setForeground(CINZA_CLARO);
                        leftPanel.setBackground(CINZA_CLARO);
                        rightPanel.setBackground(CINZA_CLARO);
                        buttonContent.setBackground(CINZA_CLARO);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        relatorioButton.setFont(new Font("Inter", Font.BOLD, 20));
                        relatorioButton.setBackground(Color.white);  // Retorna à cor padrão
                        leftPanel.setBackground(Color.white);
                        rightPanel.setBackground(Color.white);
                        buttonContent.setBackground(Color.white);
                    }
                });
            }

            // Adicionar o painel de relatórios do dia ao painel do dia
            relatorioDiaPanel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);

            // Adicionar o painel do dia ao painel principal de relatórios
            relatoriosPanel.add(relatorioDiaPanel);



        }

        // Adicionar o painel de relatórios ao centro da tela com scroll
        JScrollPane scrollPane = new JScrollPane(relatoriosPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Método para obter os relatórios
    public RelatoriosResponse relatorios() {
        RelatoriosRepository relatoriosRepository = new RelatoriosRepository();
        return relatoriosRepository.relatoriosResponse();
    }


    public void buscarRelatorioPorData() {
        // Criar uma nova janela
        JFrame janelaPesquisar = new JFrame("Pesquisar Relatórios por Data");
        janelaPesquisar.setBackground(Color.BLUE);

        // Criar o calendário customizado
        CustomJCalendar customJCalendar = new CustomJCalendar();
        JCalendar jCalendar = customJCalendar.createCustomCalendar();

        // Campo para inserir a data
        JPanel painelPesquisa = new JPanel();
        painelPesquisa.add(jCalendar);

        // Painel principal para exibir os resultados
        JPanel painelResultado = new JPanel();
        painelResultado.setLayout(new BoxLayout(painelResultado, BoxLayout.Y_AXIS));

        // Adicionar painel de pesquisa e resultado na janela
        janelaPesquisar.add(painelPesquisa, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(painelResultado);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        janelaPesquisar.add(scrollPane, BorderLayout.CENTER);

        // Adicionar o evento de seleção de data
        jCalendar.getDayChooser().addPropertyChangeListener("day", evt -> {
            // Limpar os resultados anteriores
            painelResultado.removeAll();

            // Obter a data selecionada pelo usuário no JCalendar
            java.util.Date selectedDate = jCalendar.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(janelaPesquisar, "Por favor, selecione uma data válida.");
                return;
            }

            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
            LocalDate data = sqlDate.toLocalDate();
            // Obter os relatórios com base na data
            var relatorio = relatoriosRepository.buscaRelatorioPorData(data);

            // Exibir os relatórios para a data especificada
            JPanel relatorioDiaPanel = new JPanel();
            relatorioDiaPanel.setBackground(Color.white);
            relatorioDiaPanel.setLayout(new BorderLayout());
            relatorioDiaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding no painel

            // Data e total do dia
            JLabel dataLabel = new JLabel(relatorio.data());
            dataLabel.setForeground(Color.white);
            dataLabel.setFont(new Font("Inter", Font.BOLD, 20));
            JLabel totalDoDiaLabel = new JLabel("Total do dia: R$ " + String.format("%.2f", relatorio.total_do_dia()));
            totalDoDiaLabel.setForeground(Color.white);
            totalDoDiaLabel.setFont(new Font("Inter", Font.BOLD, 20));

            // Painel de cabeçalho com data e total do dia
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(66, 75, 152));
            headerPanel.add(dataLabel, BorderLayout.WEST);
            headerPanel.add(totalDoDiaLabel, BorderLayout.EAST);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 45));

            relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);

            // Painel para os relatórios do dia (usando GridLayout)
            JPanel relatoriosDoDiaPanel = new JPanel();
            relatoriosDoDiaPanel.setLayout(new GridLayout(0, 1, 0, 1)); // Ajuste dinâmico vertical
            relatoriosDoDiaPanel.setBackground(CINZA_CLARO);
            relatoriosDoDiaPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

            // Adicionar quadrados brancos (relatórios) dentro do painel de relatórios do dia
            for (RelatoriosResponse.Relatorios.RelatorioDoDia relatorioDoDia : relatorio.relatorioDoDia()) {
                JButton relatorioButton = new JButton();
                relatorioButton.setBackground(Color.WHITE);
                relatorioButton.setBorderPainted(false); // Remove a borda do JButton
                relatorioButton.setFocusPainted(false);  // Remove a borda de foco ao clicar
                relatorioButton.setContentAreaFilled(true); // Preenche o fundo
                relatorioButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // Criar um layout interno com alinhamento à esquerda (ID e descrição) e à direita (valor)
                JPanel buttonContent = new JPanel(new BorderLayout());
                buttonContent.setBackground(Color.WHITE);

                // ID do relatório com "#" e pintado de vermelho
                JLabel idLabel = new JLabel("#" + relatorioDoDia.relatorio_id());
                idLabel.setForeground(Color.RED);

                // Traduzir o tipo de pagamento
                String tipoPagamento = switch (relatorioDoDia.tipo_pagamento()) {
                    case "0" -> "PIX";
                    case "1" -> "DINHEIRO";
                    case "2" -> "CARTAO DE CREDITO";
                    case "3" -> "CARTAO DE DEBITO";
                    case "4" -> "TRANSFERENCIA BANCARIA";
                    case "5" -> "CARTAO VIRTUAL";
                    default -> "DESCONHECIDO";
                };

                // Horário e descrição do relatório
                JLabel horarioDescricaoLabel = new JLabel(relatorioDoDia.horario() + "      " + relatorioDoDia.relatorio() + " (" + tipoPagamento + ")");
                horarioDescricaoLabel.setBorder(BorderFactory.createEmptyBorder(4, 20, 10, 10));
                horarioDescricaoLabel.setFont(new Font("Inter", Font.BOLD, 17));
                horarioDescricaoLabel.setForeground(CINZA_ESCURO);

                // Valor com cor conforme o tipo de pagamento
                JLabel valorLabel = new JLabel(String.format("R$ %.2f", relatorioDoDia.valor()));
                valorLabel.setBorder(BorderFactory.createEmptyBorder(4, 20, 10, 10));
                valorLabel.setFont(new Font("Inter", Font.BOLD, 18));
                if (relatorioDoDia.valor() < 0) {
                    valorLabel.setForeground(VERMELHO);
                } else if (Integer.parseInt(relatorioDoDia.tipo_pagamento()) == 1) {
                    valorLabel.setForeground(VERDE_ESCURO);
                } else {
                    valorLabel.setForeground(AZUL_ESCURO);
                }

                // Painel para os textos à esquerda (ID, horário e descrição)
                JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                leftPanel.setOpaque(false);
                leftPanel.setBackground(Color.WHITE);
                leftPanel.add(idLabel);
                leftPanel.add(horarioDescricaoLabel);

                // Painel para o valor à direita
                JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                rightPanel.setOpaque(false);
                rightPanel.setBackground(Color.WHITE);
                rightPanel.add(valorLabel);

                // Adicionar os painéis de conteúdo ao botão
                buttonContent.add(leftPanel, BorderLayout.WEST);
                buttonContent.add(rightPanel, BorderLayout.EAST);

                // Definir o layout final do botão com o conteúdo ajustado
                relatorioButton.add(buttonContent, BorderLayout.CENTER);

                // Adicionar o botão de relatório ao painel do dia
                relatoriosDoDiaPanel.add(relatorioButton);

                // Adiciona o efeito de destaque ao passar o mouse
                relatorioButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        relatorioButton.setFont(new Font("Inter", Font.BOLD, 25));
                        relatorioButton.setBackground(CINZA_CLARO);
                        relatorioButton.setForeground(CINZA_CLARO);
                        leftPanel.setBackground(CINZA_CLARO);
                        rightPanel.setBackground(CINZA_CLARO);
                        buttonContent.setBackground(CINZA_CLARO);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        relatorioButton.setFont(new Font("Inter", Font.BOLD, 20));
                        relatorioButton.setBackground(Color.white);  // Retorna à cor padrão
                        leftPanel.setBackground(Color.white);
                        rightPanel.setBackground(Color.white);
                        buttonContent.setBackground(Color.white);
                    }
                });
            }

            // Adicionar o painel de relatórios do dia ao painel do dia
            relatorioDiaPanel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);

            // Adicionar o painel do dia ao painel principal de relatórios
            painelResultado.add(relatorioDiaPanel);

            // Atualizar a interface com os novos resultados
            painelResultado.revalidate();
            painelResultado.repaint();
        });

        // Configurações da nova janela
        janelaPesquisar.setSize(1900, 1000);
        janelaPesquisar.setBackground(Color.BLUE);
        janelaPesquisar.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas a nova janela
        janelaPesquisar.setLocationRelativeTo(null); // Centraliza a janela na tela
        janelaPesquisar.setVisible(true);
    }


    public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private String datePattern = "yyyy-MM-dd"; // Formato da data
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }

}
