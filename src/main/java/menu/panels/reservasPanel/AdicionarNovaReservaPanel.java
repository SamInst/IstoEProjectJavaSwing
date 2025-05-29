package menu.panels.reservasPanel;


import buttons.ShadowButton;
import calendar2.DatePicker;
import enums.TipoPagamentoEnum;
import notifications.Notifications;
import repository.PessoaRepository;
import repository.QuartosRepository;
import repository.ReservasRepository;
import request.AdicionarReservasRequest;
import request.BuscaReservasResponse;
import request.PagamentoRequest;
import response.DatasReserva;
import response.PessoaResponse;
import timePicker.time.TimePicker;
import tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import static buttons.Botoes.*;
import static notifications.Notification.notification;
import static notifications.Notifications.Location.TOP_CENTER;
import static tools.Converter.converterTipoPagamento;
import static tools.Converter.converterTipoPagamentoParaInt;
import static tools.CorPersonalizada.*;
import static tools.Icones.*;
import static tools.Resize.resizeIcon;

public class AdicionarNovaReservaPanel {
    private final ReservasPanel reservasPanel;

    List<BuscaReservasResponse.Pessoas> pessoasList = new ArrayList<>();

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final QuartosRepository quartosRepository;
    private final ReservasRepository reservasRepository;
    private final PessoaRepository pessoaRepository;

    private final AnimationManager animationManager;
    private final CalendarioPanel calendarioPanel;
    private JLabel labelPessoasValue;
    private JLabel labelDiariasValue;
    private JLabel labelValorDiariaValue;
    private JLabel labelTotalValue;
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;

    private final List<PessoaResponse> selectedPeople = new ArrayList<>();

    private JComboBox<String> quartoComboBox;

    private JFormattedTextField checkinField;
    private JFormattedTextField checkoutField;
    TimePicker timePicker = new TimePicker();

    private JPanel pagamentosListPanel;

    public AdicionarNovaReservaPanel(ReservasPanel reservasPanel, ReservasRepository reservasRepository,
                                     Long roomId,
                                     LocalDate checkIn,
                                     LocalDate checkOut,
                                     QuartosRepository quartosRepository,
                                     PessoaRepository pessoaRepository,
                                     AnimationManager animationManager,
                                     CalendarioPanel calendarioPanel) {
        this.reservasPanel = reservasPanel;
        this.reservasRepository = reservasRepository;
        this.quartosRepository = quartosRepository;
        this.pessoaRepository = pessoaRepository;
        this.animationManager = animationManager;
        this.calendarioPanel = calendarioPanel;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public void showReservationFrame(java.util.Map<Long, LocalDate> checkInDateMap) {
        JFrame frame = new JFrame("Adicionar nova Reserva");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(690, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ShadowButton popupContainer = new ShadowButton();
        popupContainer.setSize(690, 600);
        popupContainer.setLayout(new BorderLayout());
        frame.add(popupContainer);

        checkInDateMap.remove(roomId);

        popupContainer.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0)
                SwingUtilities.invokeLater(popupContainer::requestFocusInWindow);
        });

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel infoGridPanel = createInfoGridPanel();
        topPanel.add(infoGridPanel);

        int numDiarias = Period.between(checkIn, checkOut).getDays();
        double valorDiaria = quartosRepository.getValorCategoria(roomId, 0);
        double valorTotal = numDiarias * valorDiaria;

        labelDiariasValue.setText(String.format("%.0f", (double) numDiarias));
        labelValorDiariaValue.setText(String.format("R$ %.2f", valorDiaria));
        labelTotalValue.setText(String.format("R$ %.2f", valorTotal));

        animationManager.previousDiariasValue = numDiarias;
        animationManager.previousValorDiariaValue = valorDiaria;
        animationManager.previousTotalValue = valorTotal;

        popupContainer.add(topPanel, BorderLayout.NORTH);

        MaterialTabbed tabbedPane = new MaterialTabbed();
        tabbedPane.setForeground(GRAY);

        JPanel infoPanel = createInfoPanel();
        tabbedPane.addTab("Informações Gerais", infoPanel);

        JPanel pessoasTab = new JPanel();
//        pessoasTab.setBackground(WHITE);
        pessoasTab.setLayout(new BoxLayout(pessoasTab, BoxLayout.Y_AXIS));
        pessoasTab.setOpaque(false);
        pessoasTab.add(Box.createVerticalStrut(10));

        createGoogleStyleBuscaPessoaPanel(pessoasTab);
        tabbedPane.addTab("Pessoas", pessoasTab);

        JPanel pagamentosTab = createPaymentsTab();
        tabbedPane.addTab("Pagamentos", pagamentosTab);
        pagamentosTab.setBackground(WHITE);

        popupContainer.add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(WHITE);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtons.setBackground(WHITE);

        ShadowButton salvarReservaButton = btn_verde("Adicionar Nova Reserva");

        rightButtons.add(salvarReservaButton);

        salvarReservaButton.addActionListener(e -> {
            AdicionarReservasRequest request = montarAdicionarReservasRequest();
            try {
                reservasRepository.adicionarReserva(request);
                reservasPanel.refreshPanel();
                frame.dispose();
                notification(Notifications.Type.SUCCESS, TOP_CENTER, "Reserva adicionada com sucesso!");

            } catch (Exception ex) {
                ex.printStackTrace();
                notification(Notifications.Type.ERROR, TOP_CENTER, "Erro ao adicionar reserva" + ex);
            }
        });


        buttonPanel.add(rightButtons, BorderLayout.EAST);

        popupContainer.add(buttonPanel, BorderLayout.SOUTH);
        popupContainer.setPreferredSize(new Dimension(800, 600));

        SwingUtilities.invokeLater(() -> {
            popupContainer.revalidate();
            popupContainer.repaint();
            atualizarContadores();
        });
    }

    public void atualizarContadores() {
        labelPessoasValue.setText((String.valueOf(pessoasList.size())));

        int numDiarias = Period.between(checkIn, checkOut).getDays();
        double valorDiaria = quartosRepository.getValorCategoria(roomId, pessoasList.size());
        double valorTotal = numDiarias * valorDiaria;

        animationManager.animateDiariasLabel(labelDiariasValue, numDiarias);
        animationManager.animateValorDiariaLabel(labelValorDiariaValue, valorDiaria);
        animationManager.animateTotalLabel(labelTotalValue, valorTotal);
    }

    private JPanel createInfoGridPanel() {
        JPanel infoGridPanel = new JPanel(new GridLayout(2, 2, 15, 5));
        infoGridPanel.setOpaque(false);
        infoGridPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        labelPessoasValue = calendarioPanel.createLabel(String.valueOf(0),
                new Font("Roboto", Font.BOLD, 14), DARK_GRAY, null);
        labelValorDiariaValue = calendarioPanel.createLabel("R$ 0,00",
                new Font("Roboto", Font.BOLD, 14), DARK_GRAY, null);
        labelDiariasValue = calendarioPanel.createLabel("0",
                new Font("Roboto", Font.BOLD, 14), DARK_GRAY, null);
        labelTotalValue = calendarioPanel.createLabel("R$ 0,00",
                new Font("Roboto", Font.BOLD, 14), GREEN, null);

        infoGridPanel.add(createFlowPanel("Pessoas:", labelPessoasValue));
        infoGridPanel.add(createFlowPanel("Valor diária:", labelValorDiariaValue));
        infoGridPanel.add(createFlowPanel("Diárias:", labelDiariasValue));
        infoGridPanel.add(createFlowPanel("Total:", labelTotalValue));

        return infoGridPanel;
    }

    private JPanel createFlowPanel(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        panel.add(calendarioPanel.createLabel(labelText, new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null));
        panel.add(valueLabel);
        return panel;
    }

    public JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(WHITE);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel quartoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quartoPanel.setOpaque(false);

        JLabel quartoLabel = calendarioPanel.createLabel("Quarto:",
                new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null);
        JLabel categoriaLabel = calendarioPanel.createLabel("Categoria:",
                new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null);

        JLabel categoriaDescricaoLabel = calendarioPanel.createLabel(
                quartosRepository.buscaQuartoPorId(roomId)
                        .categoria()
                        .categoria()
                        .toUpperCase(),
                new Font("Roboto", Font.BOLD, 14),
                DARK_GRAY,
                null
        );

        Vector<String> roomItems = new Vector<>();
        quartosRepository.buscaTodosOsQuartos()
                .forEach(q ->
                        roomItems.add(
                                "Quarto "
                                        + (q.quarto_id() < 10 ? "0" + q.quarto_id() : q.quarto_id())
                                        + " - " + q.quantidade_pessoas() + " pessoas"
                        )
                );

        quartoComboBox = new JComboBox<>(roomItems);
        quartoComboBox.setPreferredSize(new Dimension(165, 25));
        quartoComboBox.setSelectedItem(
                roomItems.get(roomId.intValue() - 1)
        );

        quartoPanel.add(quartoLabel);
        quartoPanel.add(quartoComboBox);
        quartoPanel.add(Box.createHorizontalStrut(5));
        quartoPanel.add(categoriaLabel);
        quartoPanel.add(categoriaDescricaoLabel);
        infoPanel.add(quartoPanel);

        JPanel mainHorizontalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainHorizontalPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        mainHorizontalPanel.setBackground(WHITE);

        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(WHITE);

        JPanel checkinCheckoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        checkinCheckoutPanel.setBackground(WHITE);
        checkinCheckoutPanel.setOpaque(false);

        JLabel checkinLabel = calendarioPanel.createLabel("Check-in:",
                new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null);
        checkinLabel.setPreferredSize(new Dimension(70, 25));

        checkinField = new JFormattedTextField();
        checkinField.setText(checkIn.format(df));
        checkinField.setColumns(7);

        JLabel checkoutLabel = calendarioPanel.createLabel("Check-out:",
                new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null);
        checkoutLabel.setPreferredSize(new Dimension(70, 25));

        checkoutField = new JFormattedTextField();
        checkoutField.setText(checkOut.format(df));
        checkoutField.setColumns(7);

        checkinCheckoutPanel.add(checkinLabel);
        checkinCheckoutPanel.add(checkinField);
        checkinCheckoutPanel.add(checkoutLabel);
        checkinCheckoutPanel.add(checkoutField);
        datePanel.add(checkinCheckoutPanel, BorderLayout.NORTH);

        java.util.List<DatasReserva> reservasDoQuarto =
                reservasRepository.datasReservadasPorQuarto(roomId, null);

        DatePicker datePickerRange = new DatePicker();
        datePickerRange.setBackground(WHITE);
        datePickerRange.setReservasDoQuarto(reservasDoQuarto);
        datePickerRange.setDateSelectionAble(date -> true);
        datePickerRange.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
        datePickerRange.setSelectedDateRange(checkIn, checkOut);
        datePickerRange.setPreferredSize(new Dimension(200, 260));
        datePickerRange.setAlignmentX(Component.CENTER_ALIGNMENT);
        datePanel.add(datePickerRange, BorderLayout.CENTER);

        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setOpaque(false);

        JLabel horarioTitulo = calendarioPanel.createLabel(
                "Horário previsto de chegada:",
                new Font("Roboto", Font.PLAIN, 14),
                DARK_GRAY,
                null
        );
        horarioTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        timePicker.set24HourView(true);
        timePicker.setBackground(WHITE);

        JPanel timePickerContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timePickerContainer.setOpaque(false);
        timePickerContainer.add(timePicker);

        timePanel.add(horarioTitulo, BorderLayout.NORTH);
        timePanel.add(timePickerContainer, BorderLayout.CENTER);

        mainHorizontalPanel.add(datePanel);
        mainHorizontalPanel.add(timePanel);
        infoPanel.add(mainHorizontalPanel);

        Runnable updateDiariasInfo = () -> {
            try {
                LocalDate newIn = LocalDate.parse(checkinField.getText(), df);
                LocalDate newOut = LocalDate.parse(checkoutField.getText(), df);
                checkIn = newIn;
                checkOut = newOut;
            } catch (Exception ignored) {
            }
        };

        datePickerRange.addDateSelectionListener(e -> {
            LocalDate[] sel = datePickerRange.getSelectedDateRange();
            if (sel != null && sel[0] != null && sel[1] != null) {
                LocalDate novaDataEntrada = sel[0];
                LocalDate novaDataSaida = sel[1];

                if (validarEAtualizarDatas(novaDataEntrada, novaDataSaida)) {
                    checkinField.setText(novaDataEntrada.format(df));
                    checkoutField.setText(novaDataSaida.format(df));
                }
            }
        });

        checkinField.addActionListener(e -> {
            try {
                LocalDate novaDataEntrada = LocalDate.parse(checkinField.getText(), df);
                if (!validarEAtualizarDatas(novaDataEntrada, checkOut)) {
                    checkinField.setText(checkIn.format(df));
                }
            } catch (Exception ex) {
                checkinField.setText(checkIn.format(df));
            }
        });

        checkoutField.addActionListener(e -> {
            try {
                LocalDate novaDataSaida = LocalDate.parse(checkoutField.getText(), df);
                if (!validarEAtualizarDatas(checkIn, novaDataSaida)) {
                    checkoutField.setText(checkOut.format(df));
                }
            } catch (Exception ex) {
                checkoutField.setText(checkOut.format(df));
            }
        });

        quartoComboBox.addActionListener(e -> {
            long novoQuarto = Long.parseLong(
                    ((String) Objects.requireNonNull(quartoComboBox.getSelectedItem()))
                            .split(" - ")[0]
                            .replace("Quarto ", "")
                            .replaceFirst("^0+", "")
            );

            if (!reservasRepository.podeMoverReserva(novoQuarto, checkIn, checkOut, null)) {
                notification(Notifications.Type.ERROR, TOP_CENTER, "Este quarto já está reservado no período selecionado!");
                quartoComboBox.setSelectedItem(
                        "Quarto " + (roomId < 10 ? "0" + roomId : roomId) +
                                " - " + quartosRepository.buscaQuartoPorId(roomId).quantidade_pessoas() + " pessoas"
                );
                return;
            }
            roomId = novoQuarto;

            List<DatasReserva> novasDatas = reservasRepository.datasReservadasPorQuarto(novoQuarto, null);
            datePickerRange.setReservasDoQuarto(novasDatas);

//            reservasRepository.atualizarQuarto(reserva.reserva_id(), novoQuarto);
            categoriaDescricaoLabel.setText(
                    quartosRepository.buscaQuartoPorId(novoQuarto)
                            .categoria()
                            .categoria()
                            .toUpperCase()
            );

            int dias = Period.between(checkIn, checkOut).getDays();
            double valorDia = quartosRepository.getValorCategoria(novoQuarto, 0);
            double total = dias * valorDia;

            animationManager.animateDiariasLabel(labelDiariasValue, dias);
            animationManager.animateValorDiariaLabel(labelValorDiariaValue, valorDia);
            animationManager.animateTotalLabel(labelTotalValue, total);

            notification(Notifications.Type.SUCCESS, TOP_CENTER, "Alterado para o Quarto: " + novoQuarto);
            atualizarContadores();
        });

        return infoPanel;
    }

    private boolean validarEAtualizarDatas(LocalDate novaDataEntrada, LocalDate novaDataSaida) {
        long quartoId = Long.parseLong(
                ((String) Objects.requireNonNull(quartoComboBox.getSelectedItem()))
                        .split(" - ")[0]
                        .replace("Quarto ", "")
                        .replaceFirst("^0+", "")
        );

        if (reservasRepository.existeConflitoReserva(quartoId, novaDataEntrada, novaDataSaida, null)) {
            notification(Notifications.Type.ERROR, TOP_CENTER, "Ja existe Datas reservadas neste período!");
            return false;
        }
        checkIn = novaDataEntrada;
        checkOut = novaDataSaida;

        int dias = Period.between(novaDataEntrada, novaDataSaida).getDays();
        double valorDia = quartosRepository.getValorCategoria(quartoId, 0);
        double total = dias * valorDia;

        animationManager.animateDiariasLabel(labelDiariasValue, dias);
        animationManager.animateValorDiariaLabel(labelValorDiariaValue, valorDia);
        animationManager.animateTotalLabel(labelTotalValue, total);

        atualizarContadores();

        return true;
    }


    public void createGoogleStyleBuscaPessoaPanel(JPanel pessoasTab) {
        selectedPeople.clear();

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel buscarPessoaPanel = new JPanel(new BorderLayout());

        JTextField buscarPessoaField = new JTextField(40);
        JPanel buscarPessoaInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscarPessoaInputPanel.setBackground(WHITE);
        buscarPessoaInputPanel.add(new JLabel(resizeIcon(search, 15, 15)));
        buscarPessoaInputPanel.add(new JLabel("Buscar Pessoa: "));
        buscarPessoaInputPanel.add(buscarPessoaField);
        buscarPessoaPanel.add(buscarPessoaInputPanel, BorderLayout.NORTH);
        container.add(buscarPessoaPanel, BorderLayout.NORTH);

        JPanel pessoasContainer = new JPanel();
        pessoasContainer.setLayout(new BoxLayout(pessoasContainer, BoxLayout.Y_AXIS));
        pessoasContainer.setOpaque(false);

        JScrollPane scrollPanePessoas = new JScrollPane(pessoasContainer);
        scrollPanePessoas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPanePessoas.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanePessoas.setBorder(BorderFactory.createEmptyBorder());
        container.add(scrollPanePessoas, BorderLayout.CENTER);

        atualizarPainelPessoas(pessoasContainer);

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY));

        DefaultListModel<PessoaResponse> sugestaoModel = new DefaultListModel<>();
        JList<PessoaResponse> sugestaoList = new JList<>(sugestaoModel);
        sugestaoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sugestaoList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PessoaResponse pessoa) {
                    setText(pessoa.nome());
                }
                return this;
            }
        });

        JScrollPane scrollPaneSugestoes = new JScrollPane(sugestaoList);
        scrollPaneSugestoes.setPreferredSize(new Dimension(buscarPessoaField.getPreferredSize().width, 150));
        popupMenu.add(scrollPaneSugestoes);

        buscarPessoaField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                String texto = buscarPessoaField.getText().trim();
                if (texto.length() >= 3) {
                    List<PessoaResponse> resultados = pessoaRepository.buscarPessoaPorNome(texto);
//                    List<Long> pessoasIds = reserva.pessoas().stream().map(BuscaReservasResponse.Pessoas::pessoa_id).toList();
//                    resultados.removeIf(p -> pessoasIds.contains(p.id()));

                    sugestaoModel.clear();
                    if (!resultados.isEmpty()) {
                        resultados.forEach(sugestaoModel::addElement);
                        popupMenu.setFocusable(false);
                        popupMenu.show(buscarPessoaField, 0, buscarPessoaField.getHeight());
                        SwingUtilities.invokeLater(buscarPessoaField::requestFocusInWindow);
                    } else {
                        popupMenu.setVisible(false);
                    }
                } else {
                    popupMenu.setVisible(false);
                }
            }
        });

        sugestaoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    PessoaResponse pessoaSelecionada = sugestaoList.getSelectedValue();
                    if (pessoaSelecionada != null) {
                        pessoasList.add(new BuscaReservasResponse.Pessoas(
                                pessoaSelecionada.id(),
                                pessoaSelecionada.nome(),
                                pessoaSelecionada.telefone(),
                                pessoasList.isEmpty()
                        ));

                        notification(Notifications.Type.SUCCESS, TOP_CENTER, "Pessoa adicionada com sucesso! \n" + pessoaSelecionada.nome());

                        atualizarPainelPessoas(pessoasContainer);
                        atualizarContadores();

                        sugestaoModel.clear();
                        popupMenu.setVisible(false);
                        buscarPessoaField.setText("");
                    }

                }
            }
        });

        pessoasTab.setLayout(new BorderLayout());
        pessoasTab.add(container, BorderLayout.CENTER);
        pessoasTab.revalidate();
        pessoasTab.repaint();
    }

    private void atualizarPainelPessoas(JPanel pessoasContainer) {
        pessoasContainer.removeAll();
        List<BuscaReservasResponse.Pessoas> atuais = pessoasList;

        for (BuscaReservasResponse.Pessoas registro : atuais) {
            PessoaResponse pessoa = pessoaRepository.buscarPessoaPorID(registro.pessoa_id());
            BotaoArredondado bloco =
                    adicionarBlocoPessoa(pessoa, registro, pessoasContainer);
            bloco.setAlignmentX(Component.LEFT_ALIGNMENT);
            pessoasContainer.add(bloco);
        }

        pessoasContainer.revalidate();
        pessoasContainer.repaint();

        atualizarContadores();
    }

    private BotaoArredondado adicionarBlocoPessoa(
            PessoaResponse pessoa,
            BuscaReservasResponse.Pessoas registro,
            JPanel pessoasContainer
    ) {
        BufferedImage foto = null;
        try {
            foto = pessoaRepository.buscarFotoBufferedPessoaPorId(pessoa.id());
        } catch (SQLException | IOException ignored) {
        }

        LabelArredondado labelFoto = new LabelArredondado("");
        ImageIcon icon = (foto != null)
                ? resizeIcon(new ImageIcon(ImagemArredodanda.arredondar(foto)), 50, 50)
                : resizeIcon(new ImageIcon(ImagemArredodanda.arredondar(
                ImagemArredodanda.convertImageIconToBufferedImage(
                        pessoa.sexo().equals(enums.GeneroEnum.FEMININO.ordinal()) ? user_sem_foto_feminino : user_sem_foto
                )
        )), 50, 50);
        labelFoto.setIcon(icon);

        BotaoArredondado bloco = new BotaoArredondado("");
        bloco.setBorderPainted(false);
        bloco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bloco.setLayout(new BorderLayout());
        bloco.setBackground(new Color(250, 250, 250));
        bloco.setOpaque(false);
        bloco.setContentAreaFilled(false);
        bloco.setFocusPainted(false);
        bloco.setPreferredSize(new Dimension(0, 60));
        bloco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        JLabel lbl = new JLabel("<html>" + pessoa.nome() + "<br>" + pessoa.telefone() + "</html>");
        lbl.setForeground(GRAY);
        lbl.setFont(new Font("Roboto", Font.PLAIN, 14));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        center.add(lbl, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 15));
        btns.setOpaque(false);

        ShadowButton badge = btn_azul("Representante");
        badge.setPreferredSize(new Dimension(120, 30));
        badge.setFocusPainted(false);
        badge.enableHoverEffect();

        ShadowButton definirRepresentanteButton = btn_backgroung("Definir Representante");
        definirRepresentanteButton.setPreferredSize(new Dimension(150, 30));
        definirRepresentanteButton.setFocusPainted(false);
        definirRepresentanteButton.enableHoverEffect();

        btns.add(registro.representante() ? badge : definirRepresentanteButton);

        definirRepresentanteButton.addActionListener(e -> {
            for (int i = 0; i < pessoasList.size(); i++) {
                BuscaReservasResponse.Pessoas p = pessoasList.get(i);
                pessoasList.set(i, new BuscaReservasResponse.Pessoas(
                        p.pessoa_id(),
                        p.nome(),
                        p.telefone(),
                        p.pessoa_id().equals(pessoa.id())
                ));
            }

            notification(Notifications.Type.SUCCESS, TOP_CENTER,
                    pessoa.nome() + " definido como representante!");

            atualizarPainelPessoas(pessoasContainer);
        });


        ShadowButton remove = btn_backgroung("");
        remove.setIcon(resizeIcon(close, 15, 15));
        remove.setPreferredSize(new Dimension(40, 30));
        remove.setFocusPainted(false);
        remove.enableHoverEffect();
        remove.addActionListener(e -> {
            pessoasList.removeIf(p -> p.pessoa_id().equals(pessoa.id()));
            notification(Notifications.Type.WARNING, TOP_CENTER,
                    "Pessoa removida: " + pessoa.nome());
            atualizarPainelPessoas(pessoasContainer);
        });


        btns.add(remove);

        bloco.add(labelFoto, BorderLayout.WEST);
        bloco.add(center, BorderLayout.CENTER);
        bloco.add(btns, BorderLayout.EAST);

        return bloco;
    }


    public JPanel createPaymentsTab() {
        Font textFont = new Font("Roboto", Font.PLAIN, 14);

        JPanel pagamentosTab = new JPanel();
        pagamentosTab.setLayout(new BoxLayout(pagamentosTab, BoxLayout.Y_AXIS));
        pagamentosTab.setOpaque(false);
        pagamentosTab.setAlignmentX(Component.LEFT_ALIGNMENT);
        pagamentosTab.add(Box.createVerticalStrut(10));

        JPanel descricaoPagamentoPanel = new JPanel(new BorderLayout());
        descricaoPagamentoPanel.setBackground(WHITE);
        descricaoPagamentoPanel.setPreferredSize(new Dimension(620, 30));
        descricaoPagamentoPanel.setMaximumSize(new Dimension(620, 30));

        JLabel descricaoPagamentoLabel = new JLabel("Descrição: ");
        descricaoPagamentoLabel.setFont(textFont);
        descricaoPagamentoLabel.setForeground(GRAY);
        descricaoPagamentoPanel.add(descricaoPagamentoLabel, BorderLayout.WEST);

        JFormattedTextField descricaoPagamentoField = new JFormattedTextField();
        descricaoPagamentoField.setPreferredSize(new Dimension(530, 25));
        descricaoPagamentoField.setForeground(DARK_GRAY);
        descricaoPagamentoField.setFont(new Font("Roboto", Font.PLAIN, 15));
        descricaoPagamentoPanel.add(descricaoPagamentoField, BorderLayout.CENTER);

        JPanel adicionarPagamento = new JPanel(new BorderLayout());
        adicionarPagamento.setPreferredSize(new Dimension(620, 30));
        adicionarPagamento.setMaximumSize(new Dimension(620, 30));
        adicionarPagamento.setBackground(WHITE);

        JLabel tipoPagamento = new JLabel("Tipo de pagamento: ");
        tipoPagamento.setFont(textFont);
        tipoPagamento.setForeground(GRAY);
        adicionarPagamento.add(tipoPagamento, BorderLayout.WEST);

        Vector<String> tipoPagamentoItems = new Vector<>();
        for (TipoPagamentoEnum tipo : TipoPagamentoEnum.values()) {
            tipoPagamentoItems.add(converterTipoPagamento(String.valueOf(tipo.getCodigo())));
        }

        JComboBox<String> tipoPagamentoComboBox = new JComboBox<>(tipoPagamentoItems);
        tipoPagamentoComboBox.setPreferredSize(new Dimension(193, 25));
        adicionarPagamento.add(tipoPagamentoComboBox, BorderLayout.CENTER);

        JFormattedTextField valorPagamentoField = new JFormattedTextField();
        valorPagamentoField.setPreferredSize(new Dimension(200, 25));
        valorPagamentoField.setForeground(DARK_GRAY);
        valorPagamentoField.setFont(new Font("Roboto", Font.BOLD, 14));
        valorPagamentoField.setText("R$ ");
        Mascaras.mascaraValor(valorPagamentoField);
        adicionarPagamento.add(valorPagamentoField, BorderLayout.LINE_END);

        pagamentosTab.add(descricaoPagamentoPanel);
        pagamentosTab.add(Box.createVerticalStrut(10));
        pagamentosTab.add(adicionarPagamento);

        pagamentosListPanel = new JPanel();
        pagamentosListPanel.setLayout(new BoxLayout(pagamentosListPanel, BoxLayout.Y_AXIS));
        pagamentosListPanel.setOpaque(false);

        JPanel adicionarPagamentoButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        adicionarPagamentoButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 10));
        adicionarPagamentoButtonPanel.setPreferredSize(new Dimension(720, 40));
        adicionarPagamentoButtonPanel.setMaximumSize(new Dimension(720, 40));
        adicionarPagamentoButtonPanel.setBackground(WHITE);

        ShadowButton adicionarPagamentoButton = btn_laranja("Adicionar Pagamento");
        adicionarPagamentoButton.setPreferredSize(new Dimension(150, 40));
        adicionarPagamentoButton.enableHoverEffect();
        adicionarPagamentoButtonPanel.add(adicionarPagamentoButton);

        pagamentosTab.add(adicionarPagamentoButtonPanel);

        adicionarPagamentoButton.addActionListener(e -> {
            String descricao = descricaoPagamentoField.getText();
            String valor = valorPagamentoField.getText()
                    .replace("R$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .replaceAll("[^0-9.]", "");
            String tipoPagamentoSelecionado = (String) tipoPagamentoComboBox.getSelectedItem();

            JPanel pagamentoPanel = createPagamentoPanel(
                    descricao,
                    valor,
                    tipoPagamentoSelecionado,
                    LocalDateTime.now().format(dtf));

            pagamentosListPanel.add(pagamentoPanel);

            notification(Notifications.Type.SUCCESS, TOP_CENTER, "Pagamento Adicionado: \n" + descricao + "\n R$ " + FormatarFloat.format(Float.parseFloat(valor)));

            descricaoPagamentoField.setText("");
            valorPagamentoField.setText("R$ ");

            pagamentosListPanel.revalidate();
            pagamentosListPanel.repaint();
        });

        JScrollPane pagamentosScrollPane = new JScrollPane(pagamentosListPanel);
        pagamentosScrollPane.setPreferredSize(new Dimension(900, 200));
        pagamentosScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        pagamentosTab.add(pagamentosScrollPane);

        return pagamentosTab;
    }


    private JPanel createPagamentoPanel(String descricao, String valor, String tipoPagamento, String dataHora) {
        JPanel pagamentoPanel = new JPanel();
        pagamentoPanel.setLayout(new BoxLayout(pagamentoPanel, BoxLayout.Y_AXIS));
        pagamentoPanel.setOpaque(false);
        pagamentoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel linha1 = new JPanel(new BorderLayout());
        linha1.setOpaque(false);
        linha1.setMaximumSize(new Dimension(800, 30));

        JLabel descricaoLabel = new JLabel(descricao);
        descricaoLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        descricaoLabel.setForeground(DARK_GRAY);
        linha1.add(descricaoLabel, BorderLayout.WEST);

        JPanel valorButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        valorButtonPanel.setOpaque(false);

        JLabel valorLabel = new JLabel("R$ " + FormatarFloat.format(Float.valueOf(valor)));
        valorLabel.setFont(new Font("Roboto", Font.PLAIN, 15));
        valorLabel.setForeground(DARK_GRAY);
        valorButtonPanel.add(valorLabel);

        var x = btn_backgroung("");
        x.setIcon(resizeIcon(close, 10, 10));
        x.enableHoverEffect();
        x.setToolTipText("Remover");
        x.addActionListener(a -> {
            pagamentosListPanel.remove(pagamentoPanel);
            pagamentosListPanel.revalidate();
            pagamentosListPanel.repaint();

            notification(Notifications.Type.WARNING, TOP_CENTER, "Pagamento removido: \n" + descricao + "\n R$ " + FormatarFloat.format(Float.parseFloat(valor)));
        });

        Dimension buttonSize = new Dimension(30, 30);
        x.setPreferredSize(buttonSize);
        x.setMinimumSize(buttonSize);
        x.setMaximumSize(buttonSize);
        valorButtonPanel.add(x);

        linha1.add(valorButtonPanel, BorderLayout.EAST);

        JPanel dataTipoPagamento = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dataTipoPagamento.setOpaque(false);

        JLabel tipoPagamentoLabel = new JLabel(" " + tipoPagamento);
        tipoPagamentoLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        tipoPagamentoLabel.setForeground(GRAY);

        JLabel dataHoraLabel = new JLabel(dataHora);
        dataHoraLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        dataHoraLabel.setForeground(GRAY);
        dataTipoPagamento.add(dataHoraLabel);
        dataTipoPagamento.add(tipoPagamentoLabel);

        JPanel linha2 = new JPanel(new BorderLayout());
        linha2.setOpaque(false);
        linha2.setMaximumSize(new Dimension(800, 10));
        linha2.add(dataTipoPagamento, BorderLayout.WEST);

        pagamentoPanel.add(linha1);
        pagamentoPanel.add(linha2);

        return pagamentoPanel;
    }


    private AdicionarReservasRequest montarAdicionarReservasRequest() {
        try {
            String selectedItem = (String) Objects.requireNonNull(quartoComboBox.getSelectedItem());
            Long quartoId = Long.parseLong(selectedItem.split(" - ")[0].replace("Quarto ", "").replaceFirst("^0+", ""));

            LocalDate dataEntrada = LocalDate.parse(checkinField.getText(), df);
            LocalDate dataSaida = LocalDate.parse(checkoutField.getText(), df);

            LocalTime horarioPrevisto = timePicker.getSelectedTime();

            List<AdicionarReservasRequest.PessoaRepresentante> pessoasNovaLista = new ArrayList<>();
            this.pessoasList.forEach(pessoa -> {
                pessoasNovaLista.add(new AdicionarReservasRequest.PessoaRepresentante(
                        pessoa.pessoa_id(),
                        pessoa.representante()
                ));
            });

            List<PagamentoRequest> pagamentos = new ArrayList<>();
            for (Component c : pagamentosListPanel.getComponents()) {
                if (c instanceof JPanel pagamentoPanel) {
                    JPanel linha1 = (JPanel) pagamentoPanel.getComponent(0);
                    JPanel valorPanel = (JPanel) ((BorderLayout) linha1.getLayout()).getLayoutComponent(BorderLayout.EAST);
                    JLabel valorLabel = (JLabel) valorPanel.getComponent(0);
                    String valorTexto = valorLabel.getText().replace("R$", "").trim().replace(",", ".");
                    Float valor = Float.parseFloat(valorTexto);

                    JLabel descricaoLabel = (JLabel) ((BorderLayout) linha1.getLayout()).getLayoutComponent(BorderLayout.WEST);
                    String descricao = descricaoLabel.getText();

                    JPanel linha2 = (JPanel) pagamentoPanel.getComponent(1);
                    JPanel dataTipoPanel = (JPanel) ((BorderLayout) linha2.getLayout()).getLayoutComponent(BorderLayout.WEST);
                    JLabel tipoLabel = (JLabel) dataTipoPanel.getComponent(1);
                    Integer tipo = converterTipoPagamentoParaInt(tipoLabel.getText().trim());
                    System.out.println(tipo);

                    pagamentos.add(new PagamentoRequest(descricao, tipo, valor));
                }
            }

            return new AdicionarReservasRequest(
                    quartoId,
                    dataEntrada,
                    dataSaida,
                    pessoasNovaLista.size(),
                    horarioPrevisto,
                    pessoasNovaLista,
                    pagamentos
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}