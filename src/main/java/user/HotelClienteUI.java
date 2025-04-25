package user;

import com.formdev.flatlaf.FlatLightLaf;
import net.miginfocom.swing.MigLayout;
import tools.BotaoArredondado;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HotelClienteUI {
    // Cores
    private static final Color BG_COLOR = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    private static final Color TEXT_SECONDARY = new Color(71, 85, 105);
    private static final Color DIVIDER_COLOR = new Color(226, 232, 240);
    private static final Color BUTTON_PRIMARY = new Color(37, 99, 235);
    private static final Color BUTTON_SECONDARY = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    // Fontes
    private static final Font FONT_TITLE = new Font("Roboto", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font("Roboto", Font.BOLD, 18);
    private static final Font FONT_HEADER = new Font("Roboto", Font.BOLD, 16);
    private static final Font FONT_REGULAR = new Font("Roboto", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Roboto", Font.BOLD, 14);
    private static final Font FONT_SMALL = new Font("Roboto", Font.PLAIN, 12);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelClienteUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Configurar FlatLaf
        try {
            FlatLightLaf.setup();
            UIManager.setLookAndFeel(new FlatLightLaf());

            // Personalizar componentes
            UIManager.put("Button.arc", 6);
            UIManager.put("Component.arc", 6);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            UIManager.put("ScrollBar.track", new Color(245, 245, 245));
        } catch (Exception ex) {
            System.err.println("Falha ao inicializar FlatLaf");
        }

        JFrame frame = new JFrame("Sistema de Hotel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.getContentPane().setBackground(BG_COLOR);

        // Dividir a tela em duas partes
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.35);
        splitPane.setBorder(null);
        splitPane.setDividerSize(1);
        splitPane.setBackground(BG_COLOR);
        splitPane.setOpaque(true);

        // Painel do cliente (esquerda)
        JPanel clientePanel = createClientePanel();
        clientePanel.setPreferredSize(new Dimension(400, 700));

        // Painel do histórico (direita)
        JPanel historicoPanel = createHistoricoPanel();

        splitPane.setLeftComponent(clientePanel);
        splitPane.setRightComponent(historicoPanel);

        frame.getContentPane().add(splitPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel createClientePanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 20, gap 0", "[grow]", "[]15[]10[]20[]push[]"));
        panel.setBackground(BG_COLOR);

        // Componente da foto de perfil
        JPanel photoPanel = new JPanel(new MigLayout("fill", "[center]", "[center]"));
        photoPanel.setBackground(BG_COLOR);

        JLabel photoLabel = createCircularPhotoLabel(175);
        photoPanel.add(photoLabel, "align center");

        // Dados do cliente
        PessoaResponse pessoa = MockData.getPessoa();

        // Nome do cliente
        JLabel nameLabel = new JLabel(pessoa.nome());
        nameLabel.setFont(FONT_TITLE);
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Painel de informações básicas
        JPanel infoPanel = new JPanel(new MigLayout("fillx, insets 0, gap 5", "[25%]5[grow]", "[]10[]"));
        infoPanel.setBackground(BG_COLOR);

        addInfoRow(infoPanel, "Idade:", pessoa.idade() + " ANOS");
        addInfoRow(infoPanel, "Gênero:", pessoa.sexo() == 1 ? "MASCULINO" : "FEMININO");

        // Separador
        JSeparator separator = new JSeparator();
        separator.setForeground(DIVIDER_COLOR);

        // Painel de Endereço e Empresa
        JPanel detailsPanel = new JPanel(new MigLayout("fillx, insets 0, gap 15", "[50%][50%]", "[]"));
        detailsPanel.setBackground(BG_COLOR);

        // Endereço
        JPanel addressPanel = new JPanel(new MigLayout("fillx, insets 0, gap 2", "[grow]", "[]5[]5[]5[]"));
        addressPanel.setBackground(BG_COLOR);

        JLabel addressHeader = new JLabel("Endereço");
        addressHeader.setFont(FONT_BOLD);
        addressHeader.setForeground(TEXT_PRIMARY);

        addressPanel.add(addressHeader, "wrap");
        addressPanel.add(createDetailLabel("Rua: AAA"), "wrap");
        addressPanel.add(createDetailLabel("Número: AA"), "wrap");
        addressPanel.add(createDetailLabel("Telefone: 63550-8897"), "wrap");

        // Empresa
        JPanel companyPanel = new JPanel(new MigLayout("fillx, insets 0, gap 2", "[grow]", "[]5[]5[]5[]5[]"));
        companyPanel.setBackground(BG_COLOR);

        JLabel companyHeader = new JLabel("Empresa");
        companyHeader.setFont(FONT_BOLD);
        companyHeader.setForeground(TEXT_PRIMARY);

        companyPanel.add(companyHeader, "wrap");
        companyPanel.add(createDetailLabel("SAM HELSON LTDA"), "wrap");
        companyPanel.add(createDetailLabel("52.006.953/0307"), "wrap");
        companyPanel.add(createDetailLabel("(98) 88450-8997"), "wrap");
        companyPanel.add(createDetailLabel("sanhelson@gmail.com"), "wrap");

        detailsPanel.add(addressPanel, "grow");
        detailsPanel.add(companyPanel, "grow");

        // Painel de botões
        JPanel buttonsPanel = new JPanel(new MigLayout("fillx, insets 0, gap 10", "[50%][50%]", "[]"));
        buttonsPanel.setBackground(BG_COLOR);

        JButton editButton = createButton("Editar Dados", false);
        BotaoArredondado saveButton = createButton("Salvar Dados", true);

        buttonsPanel.add(editButton, "grow");
        buttonsPanel.add(saveButton, "grow");

        // Adicionar componentes ao painel principal
        panel.add(photoPanel, "grow, wrap");
        panel.add(nameLabel, "align center, wrap");
        panel.add(infoPanel, "grow, wrap");
        panel.add(separator, "grow, wrap");
        panel.add(detailsPanel, "grow, wrap");
        panel.add(buttonsPanel, "grow, wrap");

        return panel;
    }

    private static JPanel createHistoricoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        // Cabeçalho
        JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 20", "[grow][]", "[]"));
        headerPanel.setBackground(BG_COLOR);

        JLabel titleLabel = new JLabel("Histórico de hospedagem");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel dateLabel = new JLabel("12/03/2024");
        dateLabel.setFont(FONT_REGULAR);
        dateLabel.setForeground(TEXT_PRIMARY);
        dateLabel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)));

        headerPanel.add(titleLabel, "grow");
        headerPanel.add(dateLabel, "right");

        // Conteúdo
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_COLOR);

        // Março 2024
        addMonthSection(contentPanel, "Março 2024");

        // Quarto 101
        addRoomSection(contentPanel, "Quarto 101", "27/03/2024 - 29/03/2024",
                "Sam Hélson Nunes Diniz", "128-42-2322.00");

        // Diárias
        addDiariaSection(contentPanel, "1 Diária", "27/03/2024 - 23/03/2024",
                "Sam Hélson Nunes Diniz", "Individual", "CREDIFÔ");

        addDiariaSection(contentPanel, "2 Diárias", "26/03/2024 - 26/03/2024",
                "Sam Hélson Nunes Diniz", "Individual", "CREDIFÔ");

        // Abril 2024
        addMonthSection(contentPanel, "Abril 2024");

        // Quarto 18
        addRoomSection(contentPanel, "Quarto 18", "15/04/2024 - 18/04/2024",
                "Sam Hélson Nunes Diniz", "15/04/-2024");

        // Diárias
        addDiariaSection(contentPanel, "1 Diária", "15/04/2024 - 16/04/2024",
                "Sam Hélson Nunes Diniz", "Individual", "CREDIFÔ");

        addDiariaSection(contentPanel, "2 Diárias", "16/04/2024 - 18/04/2024",
                "Sam Hélson Nunes Diniz", "Individual", "CREDIFÔ");

        // Scrollpane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BG_COLOR);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Métodos Auxiliares

    private static JLabel createCircularPhotoLabel(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Configurar renderização de alta qualidade
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Criar máscara circular
        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, size, size);
        g2.setClip(circle);

        // Preencher com cor de placeholder
        g2.setColor(new Color(200, 200, 200));
        g2.fillRect(0, 0, size, size);

        // Desenhar foto de perfil (menina) - placeholder
        g2.setColor(new Color(230, 190, 180)); // Tom de pele
        g2.fillOval(size/4, size/4, size/2, size/2);

        g2.setColor(new Color(180, 120, 80)); // Cabelo
        g2.fillArc(size/8, size/8, size*3/4, size*3/4, 0, 180);

        g2.dispose();

        JLabel label = new JLabel(new ImageIcon(image));
        return label;
    }

    private static void addInfoRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(FONT_BOLD);
        labelComponent.setForeground(TEXT_PRIMARY);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(FONT_REGULAR);
        valueComponent.setForeground(TEXT_PRIMARY);

        panel.add(labelComponent, "");
        panel.add(valueComponent, "wrap");
    }

    private static JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_REGULAR);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private static BotaoArredondado createButton(String text, boolean isPrimary) {
        BotaoArredondado button = new BotaoArredondado(text);
        button.setFont(FONT_BOLD);
        button.setFocusPainted(false);

        if (isPrimary) {
            button.setBackground(BUTTON_PRIMARY);
            button.setForeground(WHITE);
            button.setBorder(new EmptyBorder(10, 15, 10, 15));
        } else {
            button.setBackground(BUTTON_SECONDARY);
            button.setForeground(TEXT_PRIMARY);
            button.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(9, 14, 9, 14)));
        }

        return button;
    }

    private static void addMonthSection(JPanel panel, String month) {
        JPanel monthPanel = new JPanel(new MigLayout("fillx, insets 20 20 10 20", "[]", "[]"));
        monthPanel.setBackground(WHITE);

        JLabel monthLabel = new JLabel(month);
        monthLabel.setFont(FONT_SUBTITLE);
        monthLabel.setForeground(TEXT_PRIMARY);

        monthPanel.add(monthLabel);
        panel.add(monthPanel);
    }

    private static void addRoomSection(JPanel panel, String roomNumber, String period, String guestName, String code) {
        JPanel roomPanel = new JPanel(new MigLayout("fillx, insets 5 20 5 20", "[grow][]", "[]5[]"));
        roomPanel.setBackground(WHITE);

        JLabel roomLabel = new JLabel(roomNumber);
        roomLabel.setFont(FONT_BOLD);
        roomLabel.setForeground(TEXT_PRIMARY);

        JLabel guestLabel = new JLabel(guestName + " · " + code);
        guestLabel.setFont(FONT_SMALL);
        guestLabel.setForeground(TEXT_SECONDARY);

        JLabel periodLabel = new JLabel(period);
        periodLabel.setFont(FONT_REGULAR);
        periodLabel.setForeground(TEXT_PRIMARY);

        roomPanel.add(roomLabel, "split 2, span");
        roomPanel.add(periodLabel, "align right, wrap");
        roomPanel.add(guestLabel, "wrap");

        panel.add(roomPanel);
    }

    private static void addDiariaSection(JPanel panel, String title, String period, String guestName,
                                         String paymentType, String authorizedBy) {
        JPanel diariaPanel = new JPanel(new MigLayout("fillx, insets 5 20 5 40", "[grow][]", "[]5[]5[]5[]"));
        diariaPanel.setBackground(WHITE);

        JLabel titleLabel = new JLabel(title + " " + period);
        titleLabel.setFont(FONT_BOLD);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel guestLabel = new JLabel(guestName);
        guestLabel.setFont(FONT_REGULAR);
        guestLabel.setForeground(TEXT_PRIMARY);

        JLabel paymentLabel = new JLabel("Parcelamento: " + paymentType);
        paymentLabel.setFont(FONT_REGULAR);
        paymentLabel.setForeground(TEXT_PRIMARY);

        JLabel authLabel = new JLabel("Autorizado por " + authorizedBy);
        authLabel.setFont(FONT_REGULAR);
        authLabel.setForeground(TEXT_PRIMARY);

        JLabel totalLabel = new JLabel("Total: R$ ");
        totalLabel.setFont(FONT_BOLD);
        totalLabel.setForeground(TEXT_PRIMARY);

        diariaPanel.add(titleLabel, "split 2, span");
        diariaPanel.add(totalLabel, "align right, wrap");
        diariaPanel.add(guestLabel, "wrap");
        diariaPanel.add(paymentLabel, "wrap");
        diariaPanel.add(authLabel, "wrap");

        panel.add(diariaPanel);

        // Adicionar espaçamento
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(1, 10));
        spacerPanel.setBackground(WHITE);
        panel.add(spacerPanel);
    }

    // Classes de dados
    static class MockData {
        public static PessoaResponse getPessoa() {
            return new PessoaResponse(
                    1L,
                    "2025-01-01T10:00",
                    "Amélia Santos Andrade",
                    LocalDate.of(2002, 12, 12),
                    "244.212.580-32",
                    "1111111111",
                    "amelia@email.com",
                    "(98) 98988-9898",
                    null, null, null,
                    "Rua A, 123",
                    "Complemento A",
                    true,
                    false,
                    5,
                    "65000-000",
                    "Centro",
                    22,
                    "123",
                    2
            );
        }

        public static List<HistoricoHospedagemResponse> getHistorico() {
            return List.of(
                    new HistoricoHospedagemResponse(
                            101L,
                            getPessoa(),
                            LocalDate.of(2025, 3, 27),
                            LocalDate.of(2025, 3, 29),
                            List.of(
                                    new HistoricoHospedagemResponse.Diaria(
                                            LocalDate.of(2025, 3, 27),
                                            LocalDate.of(2025, 3, 28),
                                            150.0f,
                                            List.of(),
                                            List.of(
                                                    new HistoricoHospedagemResponse.Diaria.Pagamento(
                                                            LocalDateTime.of(2025, 3, 27, 15, 0),
                                                            "PIX", "Confirmado", 150.0f
                                                    )
                                            )
                                    )
                            )
                    ),
                    new HistoricoHospedagemResponse(
                            102L,
                            getPessoa(),
                            LocalDate.of(2025, 4, 15),
                            LocalDate.of(2025, 4, 18),
                            List.of(
                                    new HistoricoHospedagemResponse.Diaria(
                                            LocalDate.of(2025, 4, 15),
                                            LocalDate.of(2025, 4, 16),
                                            180.0f,
                                            List.of(),
                                            List.of(
                                                    new HistoricoHospedagemResponse.Diaria.Pagamento(
                                                            LocalDateTime.of(2025, 4, 15, 14, 30),
                                                            "Cartão de Crédito", "Confirmado", 180.0f
                                                    )
                                            )
                                    )
                            )
                    )
            );
        }
    }

    public record PessoaResponse(
            Long id,
            String data_hora_cadastro,
            String nome,
            LocalDate data_nascimento,
            String cpf,
            String rg,
            String email,
            String telefone,
            Object pais,
            Object estado,
            Object municipio,
            String endereco,
            String complemento,
            Boolean hospedado,
            Boolean clienteNovo,
            Integer vezes_hospedado,
            String cep,
            String bairro,
            Integer idade,
            String numero,
            Integer sexo
    ) {}

    public record HistoricoHospedagemResponse(
            Long quarto_id,
            PessoaResponse pessoa_principal,
            LocalDate checkin,
            LocalDate checkout,
            List<Diaria> diariaList
    ) {
        public record Diaria(
                LocalDate checkin,
                LocalDate checkout,
                Float totalDiaria,
                List<PessoaResponse> acompanhantes,
                List<Pagamento> pagamentos
        ) {
            public record Pagamento(
                    LocalDateTime data_hora_pagamento,
                    String tipo_pagamento,
                    String status_pagamento,
                    Float valor_pagamento
            ) {}
        }
    }
}