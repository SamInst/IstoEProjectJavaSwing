package principals.panels.pernoitePanels;

import com.toedter.calendar.JCalendar;
import enums.StatusPernoiteEnum;
import enums.StatusQuartoEnum;
import lombok.Getter;
import principals.tools.BotaoArredondado;
import principals.tools.CorPersonalizada;
import principals.tools.CustomJCalendar;
import principals.tools.Icones;
import repository.PernoitesRepository;
import repository.PrecosRepository;
import repository.QuartosRepository;
import request.PernoiteRequest;
import response.QuartoResponse;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static principals.tools.CorPersonalizada.VERDE_ESCURO;

public class PernoitePanel extends JPanel {
    Long quarto_id = null;
    LocalDate dataEntrada = LocalDate.now();
    LocalDate dataSaida = LocalDate.now();
    private final ObservableValue<Integer> quantidadeDePessoas = new ObservableValue<>(0);
    private final ObservableValue<Integer> quantidadeDeDiarias = new ObservableValue<>(0);
    private final ObservableValue<Float> valorTotalGlobal = new ObservableValue<>(0F);
    List<Long> pessoas = new ArrayList<>();

    JPanel pernoitesPanel = new JPanel();

    PernoitesRepository pernoitesRepository = new PernoitesRepository();
    PrecosRepository precosRepository = new PrecosRepository();
    QuartosRepository quartosRepository = new QuartosRepository();

    public PernoitePanel() {
        verificaDiariasEncerradas();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Pernoites", Icones.pernoites);
        identificadorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

        topPanel.add(identificadorPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttonPanel.setMinimumSize(new Dimension(20, 20));

        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.setPreferredSize(new Dimension(125, 40));
        btnAdicionar.addActionListener(e -> abrirJanelaAdicionarPernoite());

        JLabel hopedados = new JLabel("Hospedados: " + pernoitesRepository.hospedados());
        hopedados.setFont(new Font("Roboto", Font.BOLD, 30));
        hopedados.setForeground(VERDE_ESCURO);
        hopedados.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 30));

        topPanel.add(hopedados, BorderLayout.EAST);

        buttonPanel.add(btnAdicionar);
        identificadorPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        pernoitesPanel.setLayout(new BoxLayout(pernoitesPanel, BoxLayout.Y_AXIS));

        add(topPanel, BorderLayout.NORTH);

        for (int i = 0; i < StatusPernoiteEnum.values().length; i++){
            pernoitesPanel.add(new BlocosPernoitesAtivos().blocoPernoitesAtivos(new JPanel(), pernoitesRepository, StatusPernoiteEnum.values()[i]));
        }

        JScrollPane scrollPane = new JScrollPane(pernoitesPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
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

        JPanel blocoQuartoEDatas = criarBlocoQuartoEData();
        JPanel blocoDiarias = criarBlocoDiarias();
        JPanel blocoBuscaPessoas = new BuscaPessoasPanel(pessoas);

        mainPanel.add(blocoQuartoEDatas);
        mainPanel.add(blocoDiarias);

        JScrollPane scrollPane = new JScrollPane(blocoBuscaPessoas);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        mainPanel.add(scrollPane);

        JPanel blocoBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        blocoBotao.setBackground(Color.WHITE);

        JButton adicionarButton = new JButton("Adicionar Pernoite");
        adicionarPernoite(adicionarButton, janelaAdicionar);
        blocoBotao.add(adicionarButton);

        mainPanel.add(blocoBotao);

        janelaAdicionar.add(mainPanel, BorderLayout.NORTH);
        janelaAdicionar.setLocationRelativeTo(null);
        janelaAdicionar.setVisible(true);
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
                       pernoitesPanel.removeAll();
                        for (int i = 0; i < StatusPernoiteEnum.values().length; i++){
                            pernoitesPanel.add(new BlocosPernoitesAtivos().blocoPernoitesAtivos(new JPanel(), new PernoitesRepository(), StatusPernoiteEnum.values()[i]));
                        }
                        pernoitesPanel.revalidate();
                        pernoitesPanel.repaint();
                    }

                }
            }

        });
    }


    private JPanel criarBlocoQuartoEData() {
        JPanel painelSuperior = new JPanel(new GridBagLayout());
        painelSuperior.setPreferredSize(new Dimension(800, 100));
        painelSuperior.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 0, 10);

        JPanel blocoQuarto = new JPanel(new GridBagLayout());
        blocoQuarto.setBackground(Color.WHITE);
        blocoQuarto.setPreferredSize(new Dimension(150, 100));
        blocoQuarto.setMinimumSize(new Dimension(150, 100));

        BotaoArredondado botaoQuarto = new BotaoArredondado("Buscar");
        botaoQuarto.setToolTipText("Selecione um quarto");
        botaoQuarto.setPreferredSize(new Dimension(100, 70));
        botaoQuarto.setBackground(CorPersonalizada.AZUL_ESCURO);
        botaoQuarto.setForeground(Color.WHITE);
        botaoQuarto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botaoQuarto.setFont(new Font("Roboto", Font.BOLD, 18));

        botaoQuarto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botaoQuarto.setBackground(CorPersonalizada.AZUL_ESCURO.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botaoQuarto.setBackground(CorPersonalizada.AZUL_ESCURO);
            }
        });

        botaoQuarto.addActionListener(e -> abrirListaQuartosDisponiveis(botaoQuarto));

        blocoQuarto.add(botaoQuarto, new GridBagConstraints());

        gbc.gridx = 0;
        gbc.weightx = 0.3;
        painelSuperior.add(blocoQuarto, gbc);

        JPanel blocoDatas = blocoDatas();
        JPanel blocoPreco = blocoTotalEQuantidadePessoas();

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        painelSuperior.add(blocoDatas, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.7;
        painelSuperior.add(blocoPreco, gbc);

        return painelSuperior;
    }


    private JPanel blocoDatas() {
        JPanel bloco = new JPanel();
        bloco.setBackground(Color.WHITE);
        bloco.setPreferredSize(new Dimension(300, 100));
        bloco.setMinimumSize(new Dimension(300, 100));
        bloco.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 0, 10, 10);

        Image image = Icones.calendario.getImage();
        Image imageRedimensionada = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon iconeRedimensionado = new ImageIcon(imageRedimensionada);

        JLabel newIconeCalendario = new JLabel(iconeRedimensionado);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.WEST;
        bloco.add(newIconeCalendario, gbc);

        BotaoArredondado dataEntrada = new BotaoArredondado(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataEntrada.setToolTipText("Selecione uma data de Entrada");
        dataEntrada.setPreferredSize(new Dimension(180, 40));
        dataEntrada.setFont(new Font("Roboto", Font.BOLD, 20));
        dataEntrada.setBackground(Color.WHITE);
        dataEntrada.setForeground(Color.ORANGE);

        dataEntrada.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                dataEntrada.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                dataEntrada.setBackground(CorPersonalizada.BRANCO.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dataEntrada.setBackground(Color.WHITE);
            }
        });

        dataEntrada.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        bloco.add(dataEntrada, gbc);

        BotaoArredondado dataSaida = new BotaoArredondado(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataSaida.setToolTipText("Selecione uma data de Saida");
        dataSaida.setPreferredSize(new Dimension(180, 40));
        dataSaida.setFont(new Font("Roboto", Font.BOLD, 20));
        dataSaida.setBackground(Color.WHITE);
        dataSaida.setForeground(Color.ORANGE);

        dataSaida.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                dataSaida.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                dataSaida.setBackground(CorPersonalizada.BRANCO.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dataSaida.setBackground(Color.WHITE);
            }
        });

        dataSaida.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        bloco.add(dataSaida, gbc);

        dataEntrada.addActionListener(e -> abrirCalendario(dataEntrada, true));
        dataSaida.addActionListener(e -> abrirCalendario(dataSaida, false));

        return bloco;
    }


    private void abrirCalendario(BotaoArredondado botaoData, boolean isDataEntrada) {
        JFrame calendarioFrame = new JFrame("Selecione uma Data");
        calendarioFrame.setSize(400, 400);
        calendarioFrame.setLocationRelativeTo(null);

        CustomJCalendar customJCalendar = new CustomJCalendar();
        JCalendar jCalendar = customJCalendar.createCustomCalendar();

        jCalendar.getDayChooser().addPropertyChangeListener("day", evt -> {
            Date selectedDate = jCalendar.getDate();
            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
            LocalDate localDate = sqlDate.toLocalDate();

            if (isDataEntrada) dataEntrada = localDate;
            else dataSaida = localDate;

            botaoData.setText(localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            botaoData.setBackground(Color.WHITE);
            botaoData.setForeground(Color.ORANGE);

            calendarioFrame.dispose();
        });
        calendarioFrame.add(jCalendar);
        calendarioFrame.setVisible(true);
    }


    private JPanel blocoTotalEQuantidadePessoas() {
        JPanel bloco = new JPanel();
        bloco.setBackground(Color.WHITE);
        bloco.setPreferredSize(new Dimension(300, 100));
        bloco.setMinimumSize(new Dimension(300, 100));
        bloco.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel labelTotal = new JLabel("Total: ");
        labelTotal.setFont(new Font("Roboto", Font.BOLD, 18));
        labelTotal.setForeground(Color.DARK_GRAY);

        JLabel valorTotal = new JLabel("R$ 0,00");
        valorTotal.setFont(new Font("Roboto", Font.BOLD, 20));
        valorTotal.setForeground(CorPersonalizada.VERDE_ESCURO);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        bloco.add(labelTotal, gbc);

        gbc.gridx = 1;
        bloco.add(valorTotal, gbc);

        Image image = Icones.usuarios.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JLabel newIconePessoas = new JLabel(new ImageIcon(image));

        JTextField quantidadePessoasField = new JTextField(5);
        quantidadePessoasField.setToolTipText("Insira a quantidade de Pessoas");
        quantidadePessoasField.setFont(new Font("Roboto", Font.BOLD, 18));
        quantidadePessoasField.setBorder(BorderFactory.createLineBorder(Color.GRAY.brighter(), 1, true));

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

                    //TODO: pega q quantidade maxima de pessoas possivel
                    if (quantidadePessoas > 5) {
                        quantidadePessoas = 5;
                        quantidadePessoasField.setText(String.valueOf(quantidadePessoas));
                    }

                    float novoTotal = calcularNovoTotal(quantidadePessoas);
                    valorTotalGlobal.setValue(novoTotal);
                    valorTotal.setText(String.format("R$ %.2f", novoTotal).replace(".", ","));
                } catch (NumberFormatException ex) {
                    valorTotal.setText("R$ 0,00");
                }
            }
        });

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
            quartoPanel.setBackground(CorPersonalizada.BRANCO);
            quartoPanel.setPreferredSize(new Dimension(380, 40));

            JLabel quartoLabel = new JLabel("<html>Quarto " + (quarto.quarto_id() < 10L ? "0" + quarto.quarto_id() : quarto.quarto_id()) + "   | " + quarto.quantidade_pessoas() + " pessoas  |   "
                    + "<span style='color:rgb(" + CorPersonalizada.VERDE_ESCURO.getRed() + "," + CorPersonalizada.VERDE_ESCURO.getGreen() + "," + CorPersonalizada.VERDE_ESCURO.getBlue() + ")'>" + quarto.status_quarto_enum() + "</span></html>");

            quartoLabel.setForeground(CorPersonalizada.CINZA_ESCURO);
            quartoLabel.setFont(new Font("Roboto", Font.BOLD, 15));

            quartoPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    quartoPanel.setBackground(CorPersonalizada.AZUL_ESCURO.brighter());
                    quartoLabel.setForeground(Color.WHITE);
                    quartoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    quartoPanel.setBackground(CorPersonalizada.BRANCO);
                    quartoLabel.setForeground(CorPersonalizada.CINZA_ESCURO);
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
                        botaoQuarto.setFont(new Font("Roboto", Font.BOLD, 50));
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
        bloco.setPreferredSize(new Dimension(800, 100));
        bloco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        bloco.setLayout(new GridBagLayout());
        bloco.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel labelDiarias = new JLabel("Diárias: ");
        labelDiarias.setFont(new Font("Roboto", Font.BOLD, 16));
        labelDiarias.setForeground(CorPersonalizada.CINZA_ESCURO);
        bloco.add(labelDiarias, gbc);

        JLabel valorDiarias = new JLabel(quantidadeDeDiarias.getValue().toString());
        valorDiarias.setFont(new Font("Roboto", Font.BOLD, 16));
        valorDiarias.setForeground(CorPersonalizada.CINZA_ESCURO);

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
        labelValorDiaria.setFont(new Font("Roboto", Font.BOLD, 16));
        labelValorDiaria.setForeground(CorPersonalizada.CINZA_ESCURO);
        bloco.add(labelValorDiaria, gbc);

        JLabel valorDiaria = new JLabel(precosRepository.precoDiaria(quantidadeDePessoas.getValue()).toString());
        valorDiaria.setFont(new Font("Roboto", Font.BOLD, 16));
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
        JButton botaoDesconto = new JButton(Icones.desconto);
        botaoDesconto.setPreferredSize(new Dimension(30, 30));
        botaoDesconto.setBackground(Color.ORANGE);
        bloco.add(botaoDesconto, gbc);

        return bloco;
    }


    public static class ObservableValue<T> {
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

    public void verificaDiariasEncerradas(){
        pernoitesRepository.buscaPernoitesPorStatus(StatusPernoiteEnum.ATIVO)
                .forEach(pernoite -> {
            LocalDateTime dataHoraSaida = LocalDateTime.of(pernoite.data_saida(), LocalTime.of(12,0));
            if (dataHoraSaida.isBefore(LocalDateTime.now())) {
               pernoitesRepository.alterarStatusPernoite(StatusPernoiteEnum.DIARIA_ENCERRADA, pernoite.pernoite_id());
            }
        });
    }
}
