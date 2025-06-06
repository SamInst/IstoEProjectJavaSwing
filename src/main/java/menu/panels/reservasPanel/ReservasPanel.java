package menu.panels.reservasPanel;

import buttons.Botoes;
import buttons.ShadowButton;
import calendar2.DatePicker;
import lateralMenu.tabbed.TabbedForm;
import lombok.Getter;
import lombok.Setter;
import raven.alerts.MessageAlerts;
import repository.PessoaRepository;
import repository.QuartosRepository;
import repository.ReservasRepository;
import request.BuscaReservasResponse;
import response.DatasReserva;
import response.QuartoResponse;
import timePicker.time.TimePicker;
import tools.Converter;
import tools.Icones;
import tools.MaterialTabbed;
import tools.Refreshable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

import static buttons.Botoes.btn_azul;
import static buttons.Botoes.btn_vermelho;
import static java.time.LocalDate.now;
import static notifications.Notification.notification;
import static notifications.Notifications.Location.TOP_CENTER;
import static notifications.Notifications.Type;
import static tools.CorPersonalizada.*;
import static tools.Icones.close;
import static tools.Resize.resizeIcon;

public class ReservasPanel extends TabbedForm implements Refreshable {
    @Getter
    private final QuartosRepository quartosRepository = new QuartosRepository();
    @Getter
    private final ReservasRepository reservasRepository = new ReservasRepository();
    @Getter
    private final PessoaRepository pessoaRepository = new PessoaRepository();
    @Getter
    private LocalDate currentMonth;
    @Setter
    @Getter
    private JPanel backgroundPanel;
    @Getter
    private final Map<Long, LocalDate> checkInDateMap = new HashMap<>();
    @Getter
    private List<BuscaReservasResponse> currentReservations;
    @Getter
    private JLabel labelPessoasValue;
    @Getter
    private JLabel labelDiariasValue;
    @Getter
    private JLabel labelValorDiariaValue;
    @Getter
    private JLabel labelTotalValue;
    @Getter
    private final AnimationManager animationManager;

    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private ShadowButton pernoiteButton;
    private ShadowButton cancelarButton;
    private JFormattedTextField checkinField;
    private JFormattedTextField checkoutField;
    private JComboBox<String> quartoComboBox;
    private JButton btnPrev;
    private JButton btnNext;
    private final CalendarioPanel calendarioPanel;
    private final PeoplePanel peoplePanel;
    private final PaymentPanel paymentPanel;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    TimePicker timePicker = new TimePicker();

    public ReservasPanel() {
        this.currentMonth = now();
        this.checkinDate = LocalDate.now();
        this.checkoutDate = LocalDate.now().plusDays(1);
        this.calendarioPanel = new CalendarioPanel(this);
        this.peoplePanel = new PeoplePanel(this);
        this.paymentPanel = new PaymentPanel(this);
        this.animationManager = new AnimationManager();
        initializePanel();
    }

    public void refreshReservasPanel() { refreshPanel(); }

    @Override
    public void refreshPanel() {
        removeAll();
        initializePanel();
        revalidate();
        repaint();
    }

    private void initializePanel() {
        removeAll();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<QuartoResponse> quartos = quartosRepository.buscaTodosOsQuartos();
        currentReservations = reservasRepository.buscaReservasAtivas();

        JPanel topContentPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        JPanel occupancyPanel = calendarioPanel.createOccupancyPanel();

        topContentPanel.add(headerPanel, BorderLayout.NORTH);
        topContentPanel.add(occupancyPanel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.add(topContentPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        int daysInMonth = currentMonth.lengthOfMonth();
        int startDayOfMonth = 1;

        if (currentMonth.getYear() == LocalDate.now().getYear() &&
            currentMonth.getMonth() == LocalDate.now().getMonth()) {
            startDayOfMonth = LocalDate.now().getDayOfMonth();
            btnPrev.setEnabled(false);
        }

        int daysToShow = daysInMonth - startDayOfMonth + 1;
        int numRooms = quartos.size();

        JPanel daysHeader = calendarioPanel.createDaysHeaderPanel(daysToShow, startDayOfMonth, currentMonth);
        JPanel roomsPanel = calendarioPanel.createRoomsPanel(quartos, numRooms);
        JLayeredPane layeredPane = calendarioPanel.createLayeredPane(quartos, daysToShow, startDayOfMonth, numRooms);
        JScrollPane scrollPane = calendarioPanel.createScrollPane(layeredPane, roomsPanel, daysHeader);

        mainPanel.add(scrollPane);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        String yearStr = String.valueOf(currentMonth.getYear());
        JLabel yearLabel = calendarioPanel.createLabel("Calendário de Reservas " + yearStr, new Font("Roboto", Font.PLAIN, 25), GRAY, null);
        yearLabel.setHorizontalAlignment(SwingConstants.LEFT);
        yearLabel.setPreferredSize(new Dimension(450, 30));

        ShadowButton navPanel = new ShadowButton();
        navPanel.setLayout(new BorderLayout(5, 0));
        navPanel.setOpaque(false);
        navPanel.setPreferredSize(new Dimension(200, 45));

        btnPrev = calendarioPanel.createButton("", WHITE, DARK_GRAY, e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshPanel();
        });

        JPanel monthPanel = new JPanel(new BorderLayout());
        monthPanel.setOpaque(false);

        String monthName = calendarioPanel.getMonthName(currentMonth);
        String formattedMonthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
        JLabel monthLabel = calendarioPanel.createLabel(formattedMonthName, new Font("Roboto", Font.PLAIN, 18), DARK_GRAY, null);

        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        monthPanel.add(monthLabel, BorderLayout.CENTER);

        btnNext = calendarioPanel.createButton("", WHITE, DARK_GRAY, e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshPanel();
        });

        btnPrev.setIcon(Icones.back);
        btnNext.setIcon(Icones.new_forward);

        navPanel.add(btnPrev, BorderLayout.WEST);
        navPanel.add(monthPanel, BorderLayout.CENTER);
        navPanel.add(btnNext, BorderLayout.EAST);

        headerPanel.add(yearLabel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);

        return headerPanel;
    }

    public void handleCellClick(CalendarioPanel.CalendarCell cell) {
        calendarioPanel.updateOccupancyPanel(cell.date);

        if (!checkInDateMap.containsKey(cell.roomId)) {
            checkInDateMap.put(cell.roomId, cell.date);
            cell.setBackground(calendarioPanel.getSelectedColor());
        } else {
            LocalDate checkIn = checkInDateMap.get(cell.roomId);
            LocalDate checkOut = cell.date;

            if (checkOut.isBefore(checkIn)) {
                LocalDate temp = checkIn;
                checkIn = checkOut;
                checkOut = temp;
            }

            showReservationFrame(cell.roomId, checkIn, checkOut);

            calendarioPanel.resetCellsForRoom(cell.roomId);
        }
    }

    private void showReservationFrame(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        new AdicionarNovaReservaPanel(
                this,
                reservasRepository,
                roomId,
                checkIn,
                checkOut,
                quartosRepository,
                pessoaRepository,
                animationManager,
                calendarioPanel)
            .showReservationFrame(checkInDateMap);
    }

    public void popUp(ShadowButton shadowButton, BuscaReservasResponse reserva) {
        ShadowButton popupContainer = new ShadowButton();
        popupContainer.setBackground(BACKGROUND_GRAY);
        popupContainer.setLayout(new BorderLayout());
        popupContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        popupContainer.setPreferredSize(new Dimension(690, 600));

        shadowButton.showPopupWithButtons(popupContainer, popupContainer);
        detalhesReserva(popupContainer, reserva, shadowButton);
    }

    private void detalhesReserva(ShadowButton popupContainer, BuscaReservasResponse reserva, ShadowButton shadowButton) {
        popupContainer.removeAll();
        popupContainer.revalidate();
        popupContainer.repaint();

        popupContainer.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0)
                SwingUtilities.invokeLater(popupContainer::requestFocusInWindow);
        });

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = calendarioPanel.createLabel("Detalhes da reserva # " + reserva.reserva_id(),
                new Font("Roboto", Font.BOLD, 17), DARK_GRAY, null);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        ShadowButton closeButton = Botoes.btn_backgroung("");
        closeButton.setIcon(resizeIcon(close, 15, 15));
        closeButton.enableHoverEffect();
        headerPanel.add(closeButton, BorderLayout.EAST);

        topPanel.add(headerPanel);

        JPanel infoGridPanel = createInfoGridPanel(reserva);
        topPanel.add(infoGridPanel);

        int numDiarias = Period.between(reserva.data_entrada(), reserva.data_saida()).getDays();

        double valorDiaria = quartosRepository.getValorCategoria(reserva.quarto(), reserva.pessoas().size()) != null ?
                quartosRepository.getValorCategoria(reserva.quarto(), reserva.pessoas().size()) : 0.0;

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

        JPanel infoPanel = createInfoPanel(reserva);
        tabbedPane.addTab("Informações Gerais", infoPanel);

        JPanel pessoasTab = new JPanel();
        pessoasTab.setLayout(new BoxLayout(pessoasTab, BoxLayout.Y_AXIS));
        pessoasTab.setOpaque(false);
        pessoasTab.add(Box.createVerticalStrut(10));

        peoplePanel.createGoogleStyleBuscaPessoaPanel(pessoasTab, reserva);
        tabbedPane.addTab("Pessoas", pessoasTab);

        JPanel pagamentosTab = paymentPanel.createPaymentsTab(reserva);
        tabbedPane.addTab("Pagamentos", pagamentosTab);

        popupContainer.add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(BACKGROUND_GRAY);

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtons.setBackground(BACKGROUND_GRAY);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtons.setBackground(BACKGROUND_GRAY);

        cancelarButton = btn_vermelho("Cancelar Reserva");
        pernoiteButton = btn_azul("Mudar para Pernoite");

        leftButtons.add(cancelarButton);

        if (!reserva.hospedado()) rightButtons.add(pernoiteButton);

        buttonPanel.add(leftButtons, BorderLayout.WEST);
        buttonPanel.add(rightButtons, BorderLayout.EAST);

        cancelarReserva(reserva, shadowButton);
        novoPernoite(reserva, shadowButton);

        popupContainer.add(buttonPanel, BorderLayout.SOUTH);
        popupContainer.setPreferredSize(new Dimension(800, 600));

        SwingUtilities.invokeLater(() -> {
            popupContainer.revalidate();
            popupContainer.repaint();
            atualizarContadores(reserva);
        });

        closeButton.addActionListener(e -> shadowButton.closeJDialog());

        paymentPanel.carregarPagamentosExistentes(reserva);
    }

    private JPanel createInfoGridPanel(BuscaReservasResponse reserva) {
        JPanel infoGridPanel = new JPanel(new GridLayout(2, 2, 15, 5));
        infoGridPanel.setOpaque(false);
        infoGridPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        labelPessoasValue = calendarioPanel.createLabel(String.valueOf(reserva.pessoas().size()),
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

    public JPanel createInfoPanel(BuscaReservasResponse reserva) {
        JPanel infoPanel = new JPanel();
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
                quartosRepository.buscaQuartoPorId(reserva.quarto())
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
                roomItems.get(reserva.quarto().intValue() - 1)
        );
        if (reserva.hospedado()) quartoComboBox.setEnabled(false);

        quartoPanel.add(quartoLabel);
        quartoPanel.add(quartoComboBox);
        quartoPanel.add(Box.createHorizontalStrut(5));
        quartoPanel.add(categoriaLabel);
        quartoPanel.add(categoriaDescricaoLabel);
        infoPanel.add(quartoPanel);

        JPanel mainHorizontalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainHorizontalPanel.setBorder(BorderFactory.createEmptyBorder(-8, 0, 0, 0));

        JPanel datePanel = new JPanel(new BorderLayout());
        JPanel checkinCheckoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        checkinCheckoutPanel.setOpaque(false);

        JLabel checkinLabel = calendarioPanel.createLabel("Check-in:",
                new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null);
        checkinLabel.setPreferredSize(new Dimension(70, 25));

        checkinField = new JFormattedTextField();
        checkinField.setText(reserva.data_entrada().format(df));
        checkinField.setColumns(7);

        JLabel checkoutLabel = calendarioPanel.createLabel("Check-out:",
                new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null);
        checkoutLabel.setPreferredSize(new Dimension(70, 25));

        checkoutField = new JFormattedTextField();
        checkoutField.setText(reserva.data_saida().format(df));
        checkoutField.setColumns(7);

        checkinCheckoutPanel.add(checkinLabel);
        checkinCheckoutPanel.add(checkinField);
        checkinCheckoutPanel.add(checkoutLabel);
        checkinCheckoutPanel.add(checkoutField);
        datePanel.add(checkinCheckoutPanel, BorderLayout.NORTH);

        List<DatasReserva> reservasDoQuarto =
                reservasRepository.datasReservadasPorQuarto(reserva.quarto(), reserva.reserva_id());

        DatePicker datePickerRange = new DatePicker();
        datePickerRange.setReservasDoQuarto(reservasDoQuarto);
        datePickerRange.setDateSelectionAble(date -> true);
        datePickerRange.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
        datePickerRange.setSelectedDateRange(reserva.data_entrada(), reserva.data_saida());
        datePickerRange.setPreferredSize(new Dimension(200, 260));
        datePickerRange.setAlignmentX(Component.CENTER_ALIGNMENT);
        datePanel.add(datePickerRange, BorderLayout.CENTER);

        if (reserva.hospedado()) {
            checkinField.setEnabled(false);
            checkoutField.setEnabled(false);
            datePickerRange.setEnabled(false);
            timePicker.setEnabled(false);
        }

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
        timePicker.setSelectedTime(reserva.hora_prevista());

        JPanel timePickerContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timePickerContainer.setOpaque(false);
        timePickerContainer.add(timePicker);

        timePanel.add(horarioTitulo, BorderLayout.NORTH);
        timePanel.add(timePickerContainer, BorderLayout.CENTER);

        mainHorizontalPanel.add(datePanel);
        mainHorizontalPanel.add(timePanel);
        infoPanel.add(mainHorizontalPanel);

        datePickerRange.addDateSelectionListener(e -> {
            LocalDate[] sel = datePickerRange.getSelectedDateRange();
            if (sel != null && sel[0] != null && sel[1] != null) {
                LocalDate novaDataEntrada = sel[0];
                LocalDate novaDataSaida = sel[1];

                if (validarEAtualizarDatas(reserva, novaDataEntrada, novaDataSaida)) {
                    checkinField.setText(novaDataEntrada.format(df));
                    checkoutField.setText(novaDataSaida.format(df));
                }
            }
        });

        checkinField.addActionListener(e -> {
            try {
                LocalDate novaDataEntrada = LocalDate.parse(checkinField.getText(), df);
                if (!validarEAtualizarDatas(reserva, novaDataEntrada, checkoutDate)) {
                    checkinField.setText(checkinDate.format(df));
                }
            } catch (Exception ex) {
                checkinField.setText(checkinDate.format(df));
            }
        });

        checkoutField.addActionListener(e -> {
            try {
                LocalDate novaDataSaida = LocalDate.parse(checkoutField.getText(), df);
                if (!validarEAtualizarDatas(reserva, checkinDate, novaDataSaida)) {
                    checkoutField.setText(checkoutDate.format(df));
                }
            } catch (Exception ex) {
                checkoutField.setText(checkoutDate.format(df));
            }
        });

        quartoComboBox.addActionListener(e -> {
            long novoQuarto = Long.parseLong(
                    ((String) Objects.requireNonNull(quartoComboBox.getSelectedItem()))
                            .split(" - ")[0]
                            .replace("Quarto ", "")
                            .replaceFirst("^0+", "")
            );

            if (!reservasRepository.podeMoverReserva(novoQuarto, checkinDate, checkoutDate, reserva.reserva_id())) {
                notification(Type.ERROR, TOP_CENTER, "Este quarto já está reservado no período selecionado!");
                quartoComboBox.setSelectedItem(
                        "Quarto " + (reserva.quarto() < 10 ? "0" + reserva.quarto() : reserva.quarto()) +
                        " - " + quartosRepository.buscaQuartoPorId(reserva.quarto()).quantidade_pessoas() + " pessoas"
                );
                return;
            }

            List<DatasReserva> novasDatas = reservasRepository.datasReservadasPorQuarto(novoQuarto, reserva.reserva_id());
            datePickerRange.setReservasDoQuarto(novasDatas);

            reservasRepository.atualizarQuarto(reserva.reserva_id(), novoQuarto);
            categoriaDescricaoLabel.setText(
                    quartosRepository.buscaQuartoPorId(novoQuarto)
                            .categoria()
                            .categoria()
                            .toUpperCase()
            );

            int dias = Period.between(checkinDate, checkoutDate).getDays();

            double valorDia = quartosRepository.getValorCategoria(novoQuarto, reserva.pessoas().size()) != null ?
                    quartosRepository.getValorCategoria(novoQuarto, reserva.pessoas().size()) : 0.0;

            double total = dias * valorDia;

            animationManager.animateDiariasLabel(labelDiariasValue, dias);
            animationManager.animateValorDiariaLabel(labelValorDiariaValue, valorDia);
            animationManager.animateTotalLabel(labelTotalValue, total);

            refreshPanel();
            notification(Type.SUCCESS, TOP_CENTER, "Alterado para o Quarto: " + novoQuarto);
        });

        return infoPanel;
    }

    private boolean validarEAtualizarDatas(BuscaReservasResponse reserva, LocalDate novaDataEntrada, LocalDate novaDataSaida) {
        long quartoId = Long.parseLong(
                ((String) Objects.requireNonNull(quartoComboBox.getSelectedItem()))
                        .split(" - ")[0]
                        .replace("Quarto ", "")
                        .replaceFirst("^0+", "")
        );

        if (reservasRepository.existeConflitoReserva(quartoId, novaDataEntrada, novaDataSaida, reserva.reserva_id())) {
            notification(Type.ERROR, TOP_CENTER, "Ja existe Datas reservadas neste período!");
            return false;
        }

        reservasRepository.atualizarDataEntrada(reserva.reserva_id(), novaDataEntrada);
        reservasRepository.atualizarDataSaida(reserva.reserva_id(), novaDataSaida);

        checkinDate = novaDataEntrada;
        checkoutDate = novaDataSaida;

        int dias = Period.between(novaDataEntrada, novaDataSaida).getDays();
        double valorDia = quartosRepository.getValorCategoria(quartoId, reserva.pessoas().size()) != null ?
                quartosRepository.getValorCategoria(quartoId, reserva.pessoas().size()) : 0.0;
        double total = dias * valorDia;

        animationManager.animateDiariasLabel(labelDiariasValue, dias);
        animationManager.animateValorDiariaLabel(labelValorDiariaValue, valorDia);
        animationManager.animateTotalLabel(labelTotalValue, total);

        notification(Type.SUCCESS, TOP_CENTER, "Datas atualizadas com sucesso!");
        refreshPanel();

        return true;
    }

    private void cancelarReserva(BuscaReservasResponse reserva, ShadowButton shadowButton) {
        for (ActionListener al : cancelarButton.getActionListeners()) {
            cancelarButton.removeActionListener(al);
        }
        cancelarButton.addActionListener(e -> {
            shadowButton.closeJDialog();

            String quartoInfo = "Quarto " + reserva.quarto();
            StringBuilder pessoasFormatadas = new StringBuilder();
            if (!reserva.pessoas().isEmpty()) {
                reserva.pessoas().forEach(pessoa ->
                        pessoasFormatadas.append(pessoa.nome()).append(" - ").append(pessoa.telefone()).append("\n"));
            } else {
                pessoasFormatadas.append("Nenhuma pessoa registrada.");
            }
            StringBuilder pagamentosFormatados = new StringBuilder();
            if (!reserva.pagamentos().isEmpty()) {
                reserva.pagamentos().forEach(pagamento ->
                        pagamentosFormatados.append(pagamento.data_hora_pagamento().format(dtf))
                                .append(" ").append(Converter.converterTipoPagamento(pagamento.tipo_pagamento()))
                                .append(" R$ ").append(String.format("%.2f", pagamento.valor_pagamento()))
                                .append("\n"));
            } else {
                pagamentosFormatados.append("Nenhum pagamento registrado.");
            }

            MessageAlerts.getInstance().showMessage(
                    "Deseja cancelar a Reserva #" + reserva.reserva_id() + "?",
                    quartoInfo + "\n" +
                    "Check-in: " + reserva.data_entrada() + "  |  Checkout: " + reserva.data_saida() + "\n" +
                    "\n👥 Pessoas:\n" + pessoasFormatadas +
                    "\n💳 Pagamentos:\n" + pagamentosFormatados,
                    MessageAlerts.MessageType.ERROR,
                    MessageAlerts.YES_NO_OPTION,
                    (pc, i) -> {
                        if (i == MessageAlerts.YES_NO_OPTION) {
                            reservasRepository.desativarReserva(reserva.reserva_id());
                            refreshPanel();
                            notification(Type.SUCCESS, TOP_CENTER,
                                    "Reserva cancelada com sucesso!");
                        }
                    });
        });
    }

    private void novoPernoite(BuscaReservasResponse reserva, ShadowButton shadowButton) {
        for (ActionListener al : pernoiteButton.getActionListeners()) {
            pernoiteButton.removeActionListener(al);
        }

        pernoiteButton.addActionListener(e -> {
            shadowButton.closeJDialog();
            String quartoInfo = "Quarto " + reserva.quarto();
            StringBuilder pessoasFormatadas = new StringBuilder();
            if (!reserva.pessoas().isEmpty()) {
                reserva.pessoas().forEach(pessoa ->
                        pessoasFormatadas.append(pessoa.nome()).append(" - ").append(pessoa.telefone()).append("\n"));
            } else {
                pessoasFormatadas.append("Nenhuma pessoa registrada.");
            }
            StringBuilder pagamentosFormatados = new StringBuilder();
            if (!reserva.pagamentos().isEmpty()) {
                reserva.pagamentos().forEach(pagamento ->
                        pagamentosFormatados.append(pagamento.data_hora_pagamento().format(dtf))
                                .append(" ").append(Converter.converterTipoPagamento(pagamento.tipo_pagamento()))
                                .append(" R$ ").append(String.format("%.2f", pagamento.valor_pagamento()))
                                .append("\n"));
            } else {
                pagamentosFormatados.append("Nenhum pagamento registrado.");
            }

            MessageAlerts.getInstance().showMessage(
                    "Deseja mover a Reserva #" + reserva.reserva_id() + "\n para Pernoites?",
                    quartoInfo + "\n" +
                    "Check-in: " + reserva.data_entrada() + "  |  Checkout: " + reserva.data_saida() + "\n" +
                    "\n👥 Pessoas:\n" + pessoasFormatadas +
                    "\n💳 Pagamentos:\n" + pagamentosFormatados,
                    MessageAlerts.MessageType.DEFAULT,
                    MessageAlerts.YES_NO_OPTION,
                    (pc, i) -> {
                        if (i == MessageAlerts.YES_OPTION) {
                            reservasRepository.hospedarReserva(reserva.reserva_id());
                            refreshPanel();
                            notification(Type.SUCCESS, TOP_CENTER,
                                    "Pernoite adicionado com sucesso!\n#" +
                                    reserva.pessoas().get(0).nome());
                        }
                    });
        });
    }

    public void atualizarContadores(BuscaReservasResponse reserva) {
        BuscaReservasResponse reservaAtualizada = reservasRepository.buscarReservaPorId(reserva.reserva_id());

        if (reservaAtualizada == null) reservaAtualizada = reserva;

        labelPessoasValue.setText(String.valueOf(reservaAtualizada.pessoas().size()));

        int numDiarias = Period.between(reservaAtualizada.data_entrada(), reservaAtualizada.data_saida()).getDays();
        double valorDiaria = quartosRepository.getValorCategoria(
                reservaAtualizada.quarto(), reservaAtualizada.pessoas().size()) != null ?
                quartosRepository.getValorCategoria(
                        reservaAtualizada.quarto(), reservaAtualizada.pessoas().size()) : 0.0;
        double valorTotal = numDiarias * valorDiaria;

        animationManager.animateDiariasLabel(labelDiariasValue, numDiarias);
        animationManager.animateValorDiariaLabel(labelValorDiariaValue, valorDiaria);
        animationManager.animateTotalLabel(labelTotalValue, valorTotal);
    }

    public JPanel getPagamentosListPanel() {
        return paymentPanel.getPagamentosListPanel();
    }

    @Getter
    @Setter
    private List<JLabel> roomLabels;

}