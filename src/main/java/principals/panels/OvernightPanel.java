package principals.panels;

import com.toedter.calendar.JCalendar;
import enums.StatusQuartoEnum;
import lombok.Getter;
import principals.panels.subPanels.BuscaPessoasPanel;
import principals.tools.BotaoArredondado;
import principals.tools.Cor;
import principals.tools.CustomJCalendar;
import repository.PernoitesRepository;
import repository.PessoaRepository;
import repository.PrecosRepository;
import repository.QuartosRepository;
import request.BuscaPessoaRequest;
import request.PernoiteRequest;
import response.QuartoResponse;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OvernightPanel extends JPanel {

    Long quarto_id = null;
    LocalDate dataEntrada = LocalDate.now();
    LocalDate dataSaida = LocalDate.now();
    private final ObservableValue<Integer> quantidadeDePessoas = new ObservableValue<>(0);
    private final ObservableValue<Integer> quantidadeDeDiarias = new ObservableValue<>(0);
    private final ObservableValue<Float> valorTotalGlobal = new ObservableValue<>(0F);
    List<Long> pessoas = new ArrayList<>();

    PessoaRepository pessoaRepository = new PessoaRepository();
    PernoitesRepository pernoitesRepository = new PernoitesRepository();
    PrecosRepository precosRepository = new PrecosRepository();
    QuartosRepository quartosRepository = new QuartosRepository();

    public OvernightPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        System.out.println(pernoitesRepository.buscaPernoite(35L));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.ORANGE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.ORANGE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttonPanel.setMinimumSize(new Dimension(20, 20));

        JButton adicionar = new JButton("Adicionar Pernoite");

        adicionar.addActionListener(e -> abrirJanelaAdicionarPernoite());

        buttonPanel.add(adicionar);
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        add(topPanel, BorderLayout.NORTH);
    }


    public void abrirJanelaAdicionarPernoite() {
        JFrame janelaAdicionar = new JFrame("Adicionar Pernoite");
        janelaAdicionar.setSize(580, 600);
        janelaAdicionar.setLayout(new BorderLayout());
        janelaAdicionar.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        janelaAdicionar.setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        // Blocos de Quarto e Datas e Diárias
        JPanel blocoQuartoEDatas = criarBlocoQuartoEData();
        JPanel blocoDiarias = criarBlocoDiarias();
        JPanel blocoBuscaPessoas = new BuscaPessoasPanel(pessoas);

        mainPanel.add(blocoQuartoEDatas);
        mainPanel.add(blocoDiarias);

        // Adicionar o bloco de busca de pessoas antes do botão
        JScrollPane scrollPane = new JScrollPane(blocoBuscaPessoas);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        mainPanel.add(scrollPane);  // Adicionando bloco de busca de pessoas

        // Novo bloco com botão à direita
        JPanel blocoBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        blocoBotao.setBackground(Color.WHITE);

        JButton adicionarButton = new JButton("Adicionar Pernoite");
        adicionarPernoite(adicionarButton, janelaAdicionar);
        blocoBotao.add(adicionarButton);


        // Adicionar bloco do botão após a busca de pessoas
        mainPanel.add(blocoBotao);  // Agora o botão fica abaixo da busca de pessoas

        janelaAdicionar.add(mainPanel, BorderLayout.NORTH);
        janelaAdicionar.setLocationRelativeTo(null);
        janelaAdicionar.setVisible(true);

        // Log para depuração
        System.out.println("Quarto: " + quarto_id + ", DataEntrada: " + dataEntrada + ", DataSaida: " + dataSaida
                + ", Quantidade Pessoas: " + quantidadeDePessoas.getValue() + ", Quantidade Dias: "
                + quantidadeDeDiarias.getValue() + " Valor Total: " + valorTotalGlobal);
    }

    public void adicionarPernoite(JButton adicionarButton, JFrame janelaAdicionar) {
        adicionarButton.setPreferredSize(new Dimension(150, 30));

        adicionarButton.addActionListener(e -> {
            if (quarto_id == null){
                JOptionPane.showMessageDialog(null,
                        "Selecione um quarto",
                        "Atenção",
                        JOptionPane.WARNING_MESSAGE);
            }
            if (Objects.equals(dataSaida, LocalDate.now())){
                JOptionPane.showMessageDialog(null,
                        "Selecione uma data de saida valida",
                        "Atenção",
                        JOptionPane.WARNING_MESSAGE);
            }
            if (quantidadeDePessoas.getValue() <= 0){
                JOptionPane.showMessageDialog(null,
                        "Insira uma quantidade de pessoas validas",
                        "Atenção",
                        JOptionPane.WARNING_MESSAGE);
            }
            if (pessoas.isEmpty()){
                JOptionPane.showMessageDialog(null,
                        "Insira ao menos uma Pessoa como representante",
                        "Atenção",
                        JOptionPane.WARNING_MESSAGE);
            }
            else {
                if (pernoitesRepository.adicionarPernoite(new PernoiteRequest(
                        quarto_id,
                        dataEntrada,
                        dataSaida,
                        quantidadeDePessoas.value,
                        pessoas,
                        valorTotalGlobal.value))
                ) {
                    int result = JOptionPane.showConfirmDialog(null,
                            "Pernoite Adicionado com Sucesso!",
                            "Sucesso",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                       janelaAdicionar.dispose();
                    }

                }

                System.out.println("Adicionado pernoite " + new PernoiteRequest(
                        quarto_id,dataEntrada,dataSaida, quantidadeDePessoas.value, pessoas, valorTotalGlobal.value
                ));
            }

        });
    }



    // Método para criar um bloco cinza com altura fixa
    private JPanel criarBlocoQuartoEData() {
        JPanel painelSuperior = new JPanel(new GridBagLayout());
        painelSuperior.setPreferredSize(new Dimension(800, 100));
        painelSuperior.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // Ocupar tanto a largura quanto a altura
        gbc.insets = new Insets(0, 10, 0, 10); // Espaçamento entre os blocos

        // Criar o bloco de quarto
        JPanel blocoQuarto = new JPanel(new GridBagLayout()); // Usar GridBagLayout para o botão
        blocoQuarto.setBackground(Color.WHITE);
        blocoQuarto.setPreferredSize(new Dimension(150, 100)); // Tamanho reduzido
        blocoQuarto.setMinimumSize(new Dimension(150, 100)); // Garante que o bloco não seja maior

        // Botão arredondado com texto inicial
        BotaoArredondado botaoQuarto = new BotaoArredondado("Buscar");
        botaoQuarto.setToolTipText("Selecione um quarto");
        botaoQuarto.setPreferredSize(new Dimension(100, 70)); // Define o tamanho do botão (largura: 100px, altura: 70px)
        botaoQuarto.setBackground(Cor.AZUL_ESCURO); // Define a cor de fundo do botão
        botaoQuarto.setForeground(Color.WHITE); // Define a cor do texto
        botaoQuarto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botaoQuarto.setFont(new Font("Inter", Font.BOLD, 18)); // Definir uma fonte maior e em negrito

        // Adicionar o efeito de hover
        botaoQuarto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botaoQuarto.setBackground(Cor.AZUL_ESCURO.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botaoQuarto.setBackground(Cor.AZUL_ESCURO);
            }
        });

        // Ao clicar no botão, abrir a lista de quartos disponíveis
        botaoQuarto.addActionListener(e -> abrirListaQuartosDisponiveis(botaoQuarto));

        // Adicionar o botão ao bloco de quarto
        blocoQuarto.add(botaoQuarto, new GridBagConstraints());

        // Configurações do blocoQuarto no painel superior
        gbc.gridx = 0;
        gbc.weightx = 0.3; // Define a proporção do bloco
        painelSuperior.add(blocoQuarto, gbc);

        // Criar os dois blocos laranjas ao lado
        JPanel blocoDatas = blocoDatas();
        JPanel blocoPreco = blocoTotalEQuantidadePessoas();

        // Configurações para o blocoLaranja1
        gbc.gridx = 1;
        gbc.weightx = 0.7; // Define a proporção maior para os blocos laranja
        painelSuperior.add(blocoDatas, gbc);

        // Configurações para o blocoLaranja2
        gbc.gridx = 2;
        gbc.weightx = 0.7;
        painelSuperior.add(blocoPreco, gbc);

        return painelSuperior;
    }

    private JPanel blocoDatas() {
        JPanel bloco = new JPanel();
        bloco.setBackground(Color.WHITE);
        bloco.setPreferredSize(new Dimension(300, 100)); // Definir um tamanho maior para os blocos
        bloco.setMinimumSize(new Dimension(300, 100)); // Define o tamanho mínimo
        bloco.setLayout(new GridBagLayout()); // Usar GridBagLayout para controle preciso do layout
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 0, 10, 10); // Espaçamento entre os componentes

        // Ícone do calendário
        ImageIcon iconeCalendario = new ImageIcon("src/main/resources/icons/calendar.png");

        // Redimensionar a imagem do ícone
        Image image = iconeCalendario.getImage();
        Image imageRedimensionada = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon iconeRedimensionado = new ImageIcon(imageRedimensionada);

        JLabel newIconeCalendario = new JLabel(iconeRedimensionado);

        // Configuração para o ícone estar mais à esquerda
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.WEST; // Alinhar mais para a esquerda
        bloco.add(newIconeCalendario, gbc);

        // Campo para Data de Entrada
        BotaoArredondado dataEntrada = new BotaoArredondado(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataEntrada.setToolTipText("Selecione uma data de Entrada");
        dataEntrada.setPreferredSize(new Dimension(180, 40)); // Define o tamanho do botão
        dataEntrada.setFont(new Font("Inter", Font.BOLD, 20));
        dataEntrada.setBackground(Color.WHITE);
        dataEntrada.setForeground(Color.ORANGE);

        dataEntrada.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                dataEntrada.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                dataEntrada.setBackground(Cor.BRANCO.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dataEntrada.setBackground(Color.WHITE);
            }
        });

        // Borda vermelha ao redor do campo de entrada
        dataEntrada.setBorder(BorderFactory.createLineBorder(Color.RED, 3)); // Borda vermelha mais grossa

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        bloco.add(dataEntrada, gbc);

        // Campo para Data de Saída
        BotaoArredondado dataSaida = new BotaoArredondado(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataSaida.setToolTipText("Selecione uma data de Saida");
        dataSaida.setPreferredSize(new Dimension(180, 40)); // Define o tamanho do botão
        dataSaida.setFont(new Font("Inter", Font.BOLD, 20));
        dataSaida.setBackground(Color.WHITE);
        dataSaida.setForeground(Color.ORANGE);

        dataSaida.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                dataSaida.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                dataSaida.setBackground(Cor.BRANCO.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dataSaida.setBackground(Color.WHITE);
            }
        });

        // Borda vermelha ao redor do campo de saída
        dataSaida.setBorder(BorderFactory.createLineBorder(Color.RED, 3)); // Borda vermelha mais grossa

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST; // Alinhar mais para a esquerda
        bloco.add(dataSaida, gbc);

        // Evento para abrir o CustomJCalendar ao clicar na Data de Entrada
        dataEntrada.addActionListener(e -> abrirCalendario(dataEntrada, true));

        // Evento para abrir o CustomJCalendar ao clicar na Data de Saída
        dataSaida.addActionListener(e -> abrirCalendario(dataSaida, false));

        return bloco;
    }


    private void abrirCalendario(BotaoArredondado botaoData, boolean isDataEntrada) {
        JFrame calendarioFrame = new JFrame("Selecione uma Data");
        calendarioFrame.setSize(400, 400);
        calendarioFrame.setLocationRelativeTo(null); // Centraliza na tela

        CustomJCalendar customJCalendar = new CustomJCalendar();
        JCalendar jCalendar = customJCalendar.createCustomCalendar();

        jCalendar.getDayChooser().addPropertyChangeListener("day", evt -> {
            Date selectedDate = jCalendar.getDate();
            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
            LocalDate localDate = sqlDate.toLocalDate();

            if (isDataEntrada) {
                dataEntrada = localDate;
            } else {
                dataSaida = localDate;
            }

            botaoData.setText(localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            botaoData.setBackground(Color.WHITE);
            botaoData.setForeground(Color.ORANGE);

            calendarioFrame.dispose();
        });

        calendarioFrame.add(jCalendar);
        calendarioFrame.setVisible(true);
    }



    // Método para criar o bloco total e quantidade de pessoas
    private JPanel blocoTotalEQuantidadePessoas() {
        // Criar o painel principal com layout de GridBag
        JPanel bloco = new JPanel();
        bloco.setBackground(Color.WHITE); // Placeholder de cor laranja
        bloco.setPreferredSize(new Dimension(300, 100)); // Define o tamanho do bloco
        bloco.setMinimumSize(new Dimension(300, 100)); // Define o tamanho mínimo
        bloco.setLayout(new GridBagLayout()); // Usar GridBagLayout para controle preciso

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Definir o espaçamento

        // Criar o label para o Total e definir o valor inicial
        JLabel labelTotal = new JLabel("Total: ");
        labelTotal.setFont(new Font("Inter", Font.BOLD, 18));
        labelTotal.setForeground(Color.DARK_GRAY);

        JLabel valorTotal = new JLabel("R$ 0,00"); // Valor inicial
        valorTotal.setFont(new Font("Inter", Font.BOLD, 20));
        valorTotal.setForeground(Cor.VERDE_ESCURO);

        // Adicionar os componentes de total à parte superior
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        bloco.add(labelTotal, gbc);

        gbc.gridx = 1;
        bloco.add(valorTotal, gbc);

        // Criar o ícone de pessoas
        ImageIcon iconePessoas = new ImageIcon("src/main/resources/icons/users.png");
        Image image = iconePessoas.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JLabel newIconePessoas = new JLabel(new ImageIcon(image));

        // Campo para quantidade de pessoas
        JTextField quantidadePessoasField = new JTextField(5);
        quantidadePessoasField.setToolTipText("Insira a quantidade de Pessoas");
        quantidadePessoasField.setFont(new Font("Inter", Font.BOLD, 18));
        quantidadePessoasField.setBorder(BorderFactory.createLineBorder(Color.GRAY.brighter(), 1, true));

        // Adicionar um DocumentListener para monitorar alterações no campo de texto
        quantidadePessoasField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarTotal();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarTotal();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarTotal();
            }

            private void atualizarTotal() {
                try {
                    int quantidadePessoas = Integer.parseInt(quantidadePessoasField.getText());

                    quantidadeDePessoas.setValue(quantidadePessoas);

                    // Limitar a quantidade de pessoas a 5

                    //TODO: pega q quantidade maxima de pessoa possivel
                    if (quantidadePessoas > 5) {
                        quantidadePessoas = 5;
                        quantidadePessoasField.setText(String.valueOf(quantidadePessoas));
                    }

                    // Chamar o método para calcular o novo total
                    float novoTotal = calcularNovoTotal(quantidadePessoas);
                    valorTotalGlobal.setValue(novoTotal);
                    valorTotal.setText(String.format("R$ %.2f", novoTotal).replace(".", ",")); // Atualizar o valor do total
                } catch (NumberFormatException ex) {
                    valorTotal.setText("R$ 0,00"); // Caso haja um valor inválido, resetar o total
                }
            }
        });

        // Adicionar o ícone e o campo de pessoas à parte inferior
        gbc.gridx = 0;
        gbc.gridy = 1;
        bloco.add(newIconePessoas, gbc);

        gbc.gridx = 1;
        bloco.add(quantidadePessoasField, gbc);

        return bloco;
    }

    private float calcularNovoTotal(int quantidadePessoas) {
        int dias = Period.between(dataEntrada, dataSaida).getDays();
        quantidadeDeDiarias.setValue(dias);

        if (dias < 1) dias = 1;

        return dias * precosRepository.precoDiaria(quantidadePessoas);
    }


    private List<QuartoResponse> buscaQuartosDisponiveis() {
        return quartosRepository.buscaQuartosPorStatus(StatusQuartoEnum.DISPONIVEL);
    }

    private void abrirListaQuartosDisponiveis(BotaoArredondado botaoQuarto) {
        JFrame frameLista = new JFrame("Selecione um Quarto");
        frameLista.setSize(300, 300);
        frameLista.setLocationRelativeTo(null);

        JPanel listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));

        List<QuartoResponse> quartosDisponiveis = buscaQuartosDisponiveis();

        for (QuartoResponse quarto : quartosDisponiveis) {
            JPanel quartoPanel = new JPanel();
            quartoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            quartoPanel.setBackground(Cor.BRANCO);
            quartoPanel.setPreferredSize(new Dimension(380, 40));

            JLabel quartoLabel = new JLabel("<html>Quarto " + (quarto.quarto_id() < 10L ? "0" + quarto.quarto_id() : quarto.quarto_id()) + "   | " + quarto.quantidade_pessoas() + " pessoas  |   "
                    + "<span style='color:rgb(" + Cor.VERDE_ESCURO.getRed() + "," + Cor.VERDE_ESCURO.getGreen() + "," + Cor.VERDE_ESCURO.getBlue() + ")'>" + quarto.status_quarto_enum() + "</span></html>");

            quartoLabel.setForeground(Cor.CINZA_ESCURO);
            quartoLabel.setFont(new Font("Inter", Font.BOLD, 15));

            quartoPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    quartoPanel.setBackground(Cor.AZUL_ESCURO.brighter());
                    quartoLabel.setForeground(Color.WHITE);
                    quartoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    quartoPanel.setBackground(Cor.BRANCO);
                    quartoLabel.setForeground(Cor.CINZA_ESCURO);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    String quartoNumero = String.valueOf(quarto.quarto_id() < 10L ? "0"+ quarto.quarto_id() : quarto.quarto_id());
                    System.out.println(quarto_id);
                    quarto_id = quarto.quarto_id();
                    System.out.println(quarto_id);
                    System.out.println(quarto.quarto_id());
                    if (!botaoQuarto.getText().equals(quartoNumero)) {
                        botaoQuarto.setText(quartoNumero);
                        botaoQuarto.setFont(new Font("Inter", Font.BOLD, 50));
                        botaoQuarto.setHorizontalAlignment(SwingConstants.CENTER);
                        botaoQuarto.setVerticalAlignment(SwingConstants.CENTER);
                        botaoQuarto.repaint();
                    }
                    frameLista.dispose();
                }
            });
            quartoPanel.add(quartoLabel);
            listaPanel.add(quartoPanel);
        }
        JScrollPane scrollPane = new JScrollPane(listaPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        frameLista.add(scrollPane);
        frameLista.setVisible(true);
    }

    private JPanel criarBlocoDiarias() {
        JPanel bloco = new JPanel();
        bloco.setBackground(Color.WHITE);
        bloco.setPreferredSize(new Dimension(800, 100)); // Tamanho fixo
        bloco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Garante que a largura se ajuste
        bloco.setLayout(new GridBagLayout());
        bloco.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel labelDiarias = new JLabel("Diárias: ");
        labelDiarias.setFont(new Font("Inter", Font.BOLD, 16));
        labelDiarias.setForeground(Cor.CINZA_ESCURO);
        bloco.add(labelDiarias, gbc);

        JLabel valorDiarias = new JLabel(quantidadeDeDiarias.getValue().toString());
        valorDiarias.setFont(new Font("Inter", Font.BOLD, 16));
        valorDiarias.setForeground(Cor.CINZA_ESCURO);

        quantidadeDeDiarias.addObserver(() -> {
            valorDiarias.setText(quantidadeDeDiarias.getValue().toString());
        });

        gbc.gridx = 1;
        gbc.weightx = 0.1;
        bloco.add(valorDiarias, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.EAST;

        JLabel labelValorDiaria = new JLabel("Valor diária: ");
        labelValorDiaria.setFont(new Font("Inter", Font.BOLD, 16));
        labelValorDiaria.setForeground(Cor.CINZA_ESCURO);
        bloco.add(labelValorDiaria, gbc);

        JLabel valorDiaria = new JLabel(precosRepository.precoDiaria(quantidadeDePessoas.getValue()).toString());
        valorDiaria.setFont(new Font("Inter", Font.BOLD, 16));
        valorDiaria.setForeground(Color.ORANGE);


        quantidadeDePessoas.addObserver(() -> {
            Float precoDiaria = precosRepository.precoDiaria(quantidadeDePessoas.getValue());

            valorDiaria.setText(
                    quantidadeDePessoas.getValue() != 0
                            ? "R$ "+ String.format("%.2f", precoDiaria).replace(".", ",")
                            : "R$ 0"
            );
        });

        gbc.gridx = 3;
        gbc.weightx = 0.1;
        bloco.add(valorDiaria, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.1;
        JButton botaoDesconto = new JButton(new ImageIcon("src/main/resources/icons/desconto.png"));
        botaoDesconto.setPreferredSize(new Dimension(30, 30));
        botaoDesconto.setBackground(Color.ORANGE);
        bloco.add(botaoDesconto, gbc);

        return bloco;
    }


    public class ObservableValue<T> {
        @Getter
        private T value;
        private List<Observer> observers = new ArrayList<>();

        public ObservableValue(T initialValue) {
            this.value = initialValue;
        }

        public void setValue(T value) {
            this.value = value;
            notifyObservers();
        }

        public void addObserver(Observer observer) {
            observers.add(observer);
        }

        private void notifyObservers() {
            for (Observer observer : observers) {
                observer.update();
            }
        }

        public interface Observer {
            void update();
        }
    }
}
