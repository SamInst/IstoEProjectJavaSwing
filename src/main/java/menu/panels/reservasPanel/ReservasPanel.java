package menu.panels.reservasPanel;

import buttons.Botoes;
import buttons.ShadowButton;
import calendar2.DatePicker;
import calendar2.event.TimeSelectionEvent;
import calendar2.event.TimeSelectionListener;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import customOptionPane.GlassPanePopup;
import customOptionPane.Message;
import enums.TipoPagamentoEnum;
import lateralMenu.tabbed.TabbedForm;
import raven.alerts.MessageAlerts;
import raven.popup.component.PopupCallbackAction;
import raven.popup.component.PopupController;
import repository.PernoitesRepository;
import repository.PessoaRepository;
import repository.QuartosRepository;
import repository.ReservasRepository;
import request.AtualizarReservaRequest;
import request.BuscaReservasResponse;
import request.PernoiteRequest;
import response.DatasReserva;
import response.PessoaResponse;
import response.QuartoResponse;
import timePicker.time.TimePicker;
import tools.*;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.*;

import static buttons.Botoes.*;
import static enums.GeneroEnum.FEMININO;
import static java.time.LocalDate.now;
import static notifications.Notification.notification;
import static notifications.Notifications.Location.TOP_CENTER;
import static notifications.Notifications.Type;
import static tools.CorPersonalizada.*;
import static tools.Icones.*;
import static tools.ImagemArredodanda.arredondar;
import static tools.ImagemArredodanda.convertImageIconToBufferedImage;
import static tools.Resize.resizeIcon;
import static tools.TruncateText.truncateText;

public class ReservasPanel extends TabbedForm implements Refreshable {
    private final QuartosRepository quartosRepository = new QuartosRepository();
    private final ReservasRepository reservasRepository = new ReservasRepository();
    private final PernoitesRepository pernoitesRepository = new PernoitesRepository();
    private final PessoaRepository pessoaRepository = new PessoaRepository();
    private LocalDate currentMonth;
    private JPanel daysHeader;
    private JPanel roomsPanel;
    private JPanel backgroundPanel;
    private final Map<Long, LocalDate> checkInDateMap = new HashMap<>();
    private List<BuscaReservasResponse> currentReservations;
    private final Dimension cellSize = new Dimension(300, 60);
    private final Border defaultCellBorder = BorderFactory.createLineBorder(BACKGROUND_GRAY);
    private final Color selectedColor = GREEN;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    ShadowButton pernoiteButton;
    ShadowButton cancelarButton;
    JLabel labelPessoasValue;
    JLabel labelDiariasValue;
    JLabel labelValorDiariaValue;
    JLabel labelTotalValue;
    private List<DatasReserva> reservasDoQuarto;
    private String selectedRoom;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    JFormattedTextField checkinField;
    JFormattedTextField checkoutField;
    JComboBox<String> quartoComboBox;
    JComboBox<String> tipoPagamentoComboBox;
    private final List<PessoaResponse> selectedPeople = new ArrayList<>();
    JButton btnPrev;
    TimePicker timePicker = new TimePicker();

    private class CalendarCell extends JPanel {
        Long roomId;
        LocalDate date;
        int row;
        int col;

        CalendarCell(Long roomId, LocalDate date, int row, int col) {
            this.roomId = roomId;
            this.date = date;
            this.row = row;
            this.col = col;
            setPreferredSize(cellSize);
            setBorder(defaultCellBorder);
        }
    }

    private class CalendarLabel extends JLabel {
        int col;

        CalendarLabel(String text, int col) {
            super(text, SwingConstants.CENTER);
            this.col = col;
            setBorder(defaultCellBorder);
            setPreferredSize(cellSize);
            setOpaque(true);
        }
    }

    public ReservasPanel() {
        this.currentMonth = now();
        this.selectedRoom = "";
        this.checkinDate = LocalDate.now();
        this.checkoutDate = LocalDate.now().plusDays(1);
        initializePanel();
    }

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
        List<QuartoResponse> quartos = quartosRepository.buscaTodosOsQuartos();
        currentReservations = reservasRepository.buscaReservasAtivas();
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);


        int daysInMonth = currentMonth.lengthOfMonth();
        int startDayOfMonth = 1;

        if (currentMonth.getYear() == LocalDate.now().getYear() &&
                currentMonth.getMonth() == LocalDate.now().getMonth()) {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            startDayOfMonth = yesterday.getDayOfMonth();
            btnPrev.setEnabled(false);
        }

        int daysToShow = daysInMonth - startDayOfMonth + 1;

        int numRooms = quartos.size();
        daysHeader = createDaysHeaderPanel(daysToShow, startDayOfMonth);
        roomsPanel = createRoomsPanel(quartos, numRooms);
        JLayeredPane layeredPane = createLayeredPane(quartos, daysToShow, startDayOfMonth, numRooms);
        JScrollPane scrollPane = createScrollPane(layeredPane);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        String yearStr = String.valueOf(currentMonth.getYear());
        JLabel yearLabel = createLabel(yearStr, new Font("Roboto", Font.BOLD, 16), BLUE, BACKGROUND_GRAY);
        yearLabel.setHorizontalAlignment(SwingConstants.LEFT);
        yearLabel.setPreferredSize(new Dimension(70, 30));
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));
        navPanel.setOpaque(false);
        navPanel.setPreferredSize(new Dimension(260, 30));
        btnPrev = createButton(" < ", WHITE, BLUE, e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshPanel();
        });
        btnPrev.setPreferredSize(new Dimension(20, 30));
        String monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).toUpperCase();
        JLabel monthLabel = createLabel(monthName, new Font("Roboto", Font.BOLD, 18), BLUE, BACKGROUND_GRAY);
        monthLabel.setPreferredSize(new Dimension(150, 30));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JButton btnNext = createButton(" > ", WHITE, BLUE, e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshPanel();
        });
        btnNext.setPreferredSize(new Dimension(50, 30));
        navPanel.add(btnPrev);
        navPanel.add(Box.createHorizontalStrut(5));
        navPanel.add(monthLabel);
        navPanel.add(Box.createHorizontalStrut(5));
        navPanel.add(btnNext);
        headerPanel.add(yearLabel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createDaysHeaderPanel(int daysToShow, int startDay) {
        JPanel daysHeader = new JPanel(new GridLayout(1, daysToShow, 0, 0));
        for (int d = startDay; d <= currentMonth.lengthOfMonth(); d++) {
            LocalDate tmpDate = currentMonth.withDayOfMonth(d);
            String dayStr = String.format("%02d/%02d", d, currentMonth.getMonthValue());
            String dayOfWeek = tmpDate.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("pt", "BR"));
            String labelText = "<html><center>" + dayStr + "<br>" + dayOfWeek + "</center></html>";

            CalendarLabel dayLabel = new CalendarLabel(labelText, d);
            dayLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
            dayLabel.setForeground(tmpDate.isEqual(now()) ? DARK_GRAY : WHITE);
            dayLabel.setBackground(tmpDate.isEqual(now()) ? new Color(0xEBEBEB) : BLUE.brighter());
            daysHeader.add(dayLabel);
        }

        daysHeader.setPreferredSize(
                new Dimension(daysToShow * cellSize.width, cellSize.height)
        );

        return daysHeader;
    }


    private JPanel createRoomsPanel(List<QuartoResponse> quartos, int numRooms) {
        JPanel roomsPanel = new JPanel(new GridLayout(numRooms, 1, 0, 0));
        roomsPanel.setBackground(BACKGROUND_GRAY);
        Dimension roomCellSize = new Dimension(60, cellSize.height);
        for (QuartoResponse quarto : quartos) {
            Long roomId = quarto.quarto_id();
            JLabel roomLabel = createLabel(roomId < 10 ? "0" + roomId : roomId.toString(), new Font("Roboto", Font.BOLD, 14), BLUE, BACKGROUND_GRAY);
            roomLabel.setPreferredSize(roomCellSize);
            roomLabel.setBorder(defaultCellBorder);
            roomsPanel.add(roomLabel);
        }
        return roomsPanel;
    }

    private JLayeredPane createLayeredPane(List<QuartoResponse> quartos, int daysToShow, int startDay, int numRooms) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        int totalWidth = daysToShow * cellSize.width;
        int totalHeight = numRooms * cellSize.height;
        layeredPane.setPreferredSize(new Dimension(totalWidth, totalHeight));
        backgroundPanel = new JPanel(new GridLayout(numRooms, daysToShow, 0, 0));
        backgroundPanel.setBounds(0, 0, totalWidth, totalHeight);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        for (int row = 0; row < numRooms; row++) {
            Long roomId = quartos.get(row).quarto_id();
            for (int colIndex = 0; colIndex < daysToShow; colIndex++) {
                int dayOfMonth = startDay + colIndex;
                LocalDate date = currentMonth.withDayOfMonth(dayOfMonth);
                CalendarCell cell = new CalendarCell(roomId, date, row, colIndex);
                cell.setBackground(date.isEqual(now()) ? LIGHT_GRAY_2 : WHITE);


                BuscaReservasResponse reserva = findReservationForDate(roomId, date);
                cell.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if (checkInDateMap.containsKey(cell.roomId))
                            updateSelectionForRoom(cell.roomId, cell.date);
                    }
                });
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (reserva == null)
                            cell.setBackground(BACKGROUND_GRAY);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        boolean isSelected = isSelectedRange(cell.roomId, cell.date);
                        cell.setBackground(isSelected ? selectedColor : (cell.date.isEqual(now()) ? LIGHT_GRAY_2 : WHITE));
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleCellClick(cell);
                    }
                });
                backgroundPanel.add(cell);
            }
        }
        JPanel overlayPanel = new JPanel(null);
        overlayPanel.setOpaque(false);
        overlayPanel.setBounds(0, 0, totalWidth, totalHeight);
        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);

        LocalDate visibleStart = currentMonth.withDayOfMonth(startDay);
        LocalDate monthEnd = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth());


        for (BuscaReservasResponse reserva : currentReservations) {
            reservasDoQuarto = reservasRepository.datasReservadasPorQuarto(reserva.quarto(), reserva.reserva_id());
            LocalDate resStartDate = reserva.data_entrada();
            LocalDate resEndDate = reserva.data_saida();


            if (resEndDate.isBefore(visibleStart) || resStartDate.isAfter(monthEnd))
                continue;

            int rowIndex = encontrarIndiceDoQuarto(quartos, reserva.quarto());
            if (rowIndex < 0) continue;

            LocalDate clampedStartDate = resStartDate.isBefore(visibleStart) ? visibleStart : resStartDate;
            LocalDate clampedEndDate = resEndDate.isAfter(monthEnd) ? monthEnd : resEndDate;

            int startDayIndex = clampedStartDate.getDayOfMonth() - startDay;
            int endDayIndex = clampedEndDate.getDayOfMonth() - startDay;

            if (endDayIndex < 0) continue;
            startDayIndex = Math.max(0, startDayIndex);

            int checkInX = startDayIndex * cellSize.width + (cellSize.width / 2);
            int checkOutX = endDayIndex * cellSize.width + (cellSize.width / 2);
            int reservationWidth = (endDayIndex == startDayIndex) ? cellSize.width / 2 : checkOutX - checkInX;
            int roomY = rowIndex * cellSize.height;

            ShadowButton faixa = btn_cinza("");
            faixa.setBackground(GREEN);
            faixa.setForeground(LIGHT_GRAY_2);
            faixa.enableHoverEffect();
            faixa.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
            faixa.setLayout(new BoxLayout(faixa, BoxLayout.X_AXIS));
            faixa.setBounds(checkInX, roomY + 6, reservationWidth, cellSize.height - 6);
            ShadowButton qtdPessoa = btn_branco(" " + reserva.pessoas().size() + " ");
            String nome = reserva.pessoas().stream()
                    .filter(p -> p.representante())
                    .findFirst()
                    .map(p -> p.nome()).orElse("Reservado (sem pessoa definida)");
            JLabel labelNome = new JLabel(truncateText(nome, qtdPessoa, faixa.getWidth() - 90));
            labelNome.setFont(new Font("Roboto", Font.PLAIN, 13));
            qtdPessoa.setAlignmentY(Component.CENTER_ALIGNMENT);
            labelNome.setAlignmentY(0.65f);
            faixa.add(qtdPessoa);
            faixa.add(Box.createHorizontalStrut(5));
            faixa.add(labelNome);
            faixa.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    popUp(faixa, reserva);
                }
            });
            overlayPanel.add(faixa);
        }
        return layeredPane;
    }

    private JScrollPane createScrollPane(JLayeredPane layeredPane) {
        JScrollPane scrollPane = new JScrollPane(layeredPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setRowHeaderView(roomsPanel);
        scrollPane.setColumnHeaderView(daysHeader);
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
        return scrollPane;
    }

    private void handleCellClick(CalendarCell cell) {
        if (!checkInDateMap.containsKey(cell.roomId)) {
            checkInDateMap.put(cell.roomId, cell.date);
            cell.setBackground(selectedColor);
        } else {
            LocalDate checkIn = checkInDateMap.get(cell.roomId);
            LocalDate checkOut = cell.date;
            if (checkOut.isBefore(checkIn)) {
                LocalDate temp = checkIn;
                checkIn = checkOut;
                checkOut = temp;
            }
            if (isOverlappingExistingReservation(cell.roomId, checkIn, checkOut)) {
                notification(Type.ERROR, TOP_CENTER, "Período já reservado para este quarto.");
                checkInDateMap.remove(cell.roomId);
            } else {
                showReservationFrame(cell.roomId, checkIn, checkOut);
            }
            resetCellsForRoom(cell.roomId);
        }
    }

    private int encontrarIndiceDoQuarto(List<QuartoResponse> quartos, Long quartoId) {
        for (int i = 0; i < quartos.size(); i++) {
            if (quartos.get(i).quarto_id().equals(quartoId))
                return i;
        }
        return -1;
    }

    private BuscaReservasResponse findReservationForDate(Long roomId, LocalDate date) {
        return currentReservations.stream()
                .filter(reserva -> reserva.quarto().equals(roomId))
                .filter(reserva -> !date.isBefore(reserva.data_entrada()) && !date.isAfter(reserva.data_saida()))
                .findFirst().orElse(null);
    }

    private boolean isOverlappingExistingReservation(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return currentReservations.stream()
                .filter(reserva -> reserva.quarto().equals(roomId))
                .anyMatch(reserva -> {
                    LocalDate resStart = reserva.data_entrada().plusDays(1);
                    LocalDate resEnd = reserva.data_saida();
                    return !checkOut.isBefore(resStart) && !checkIn.isAfter(resEnd);
                });
    }

    private void showReservationFrame(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        JFrame frame = new JFrame("Nova Reserva");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new GridLayout(3, 1));
        frame.add(createLabel("Quarto: " + roomId, new Font("Roboto", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE));
        frame.add(createLabel("Data de Entrada: " + checkIn, new Font("Roboto", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE));
        frame.add(createLabel("Data de Saída: " + checkOut, new Font("Roboto", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE));
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        checkInDateMap.remove(roomId);
    }

    private void updateSelectionForRoom(Long roomId, LocalDate hoveredDate) {
        LocalDate checkIn = checkInDateMap.get(roomId);
        if (checkIn == null) return;
        LocalDate start = checkIn.isBefore(hoveredDate) ? checkIn : hoveredDate;
        LocalDate end = checkIn.isAfter(hoveredDate) ? checkIn : hoveredDate;
        for (Component comp : backgroundPanel.getComponents()) {
            if (comp instanceof CalendarCell cell && cell.roomId.equals(roomId)) {
                cell.setBackground(!cell.date.isBefore(start) && !cell.date.isAfter(end)
                        ? selectedColor
                        : (cell.date.isEqual(now()) ? LIGHT_GRAY_2 : WHITE));
            }
        }
    }

    private boolean isSelectedRange(Long roomId, LocalDate date) {
        LocalDate checkIn = checkInDateMap.get(roomId);
        return date.equals(checkIn);
    }

    public void popUp(ShadowButton shadowButton, BuscaReservasResponse reserva) {
        ShadowButton popupContainer = new ShadowButton();
        popupContainer.setBackground(BACKGROUND_GRAY);
        popupContainer.setLayout(new BorderLayout());
        popupContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        popupContainer.setPreferredSize(new Dimension(690, 590));
        shadowButton.showPopupWithButtons(popupContainer, popupContainer);
        detalhesReserva(popupContainer, reserva, shadowButton);
    }

    private void resetCellsForRoom(Long roomId) {
        for (Component comp : backgroundPanel.getComponents()) {
            if (comp instanceof CalendarCell cell && cell.roomId.equals(roomId)) {
                cell.setBackground(cell.date.isEqual(LocalDate.now()) ? LIGHT_GRAY_2 : WHITE);
                cell.repaint();
            }
        }
    }

    private JLabel createLabel(String text, Font font, Color foreground, Color background) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(foreground);
        if (background != null) {
            label.setBackground(background);
            label.setOpaque(true);
        }
        return label;
    }

    private JButton createButton(String text, Color background, Color foreground, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.addActionListener(listener);
        return button;
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
                System.out.println(reserva.pessoas().size());
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
                    MessageAlerts.OK_OPTION,
                    new PopupCallbackAction() {
                        @Override
                        public void action(PopupController pc, int i) {
                            if (i == MessageAlerts.OK_OPTION) {
                                reservasRepository.desativarReserva(reserva.reserva_id());
                                refreshPanel();
                                notification(Type.SUCCESS, TOP_CENTER,
                                        "Reserva cancelada com sucesso!\n#" + reserva.reserva_id() + " - " + quartoInfo + "\n" +
                                                reserva.pessoas().get(0).nome());
                            }
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
                    new PopupCallbackAction() {
                        @Override
                        public void action(PopupController pc, int i) {
                            if (i == MessageAlerts.YES_OPTION) {
                                reservasRepository.desativarReserva(reserva.reserva_id());
                                refreshPanel();
                                notification(Type.SUCCESS, TOP_CENTER,
                                        "Pernoite adicionado com sucesso!\n#" +
                                                reserva.pessoas().get(0).nome());
                            }
                        }
                    });
        });
    }

    private void mudarReservaParaPernoite(BuscaReservasResponse reserva) {
        List<Long> pessoasIds = new ArrayList<>();
        List<BuscaReservasResponse.Pagamentos> pagamentosList = new ArrayList<>(reserva.pagamentos());
        reserva.pessoas().forEach(pessoa -> pessoasIds.add(pessoa.pessoa_id()));
        var diaria = quartosRepository.getValorCategoria(reserva.quarto(), reserva.pessoas().size());
        int qtd_dias = Period.between(reserva.data_entrada(), reserva.data_saida()).getDays();
        float total = (diaria != null ? diaria : 0) * qtd_dias;
        reservasRepository.desativarReserva(reserva.reserva_id());
        pernoitesRepository.adicionarPernoite(new PernoiteRequest(
                reserva.quarto(), reserva.data_entrada(), reserva.data_saida(),
                reserva.pessoas().size(), pessoasIds, pagamentosList, total));
    }

    private void detalhesReserva(ShadowButton popupContainer, BuscaReservasResponse reserva, ShadowButton shadowButton) {
        popupContainer.removeAll();
        popupContainer.revalidate();
        popupContainer.repaint();
        popupContainer.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0)
                SwingUtilities.invokeLater(popupContainer::requestFocusInWindow);
        });

        reservasDoQuarto = reservasRepository.datasReservadasPorQuarto(reserva.quarto(), reserva.reserva_id());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = createLabel("Detalhes da reserva # " + reserva.reserva_id(), new Font("Roboto", Font.BOLD, 17), DARK_GRAY, null);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        ShadowButton closeButton = Botoes.btn_backgroung("");
        closeButton.setIcon(resizeIcon(close, 15, 15));
        closeButton.enableHoverEffect();
        headerPanel.add(closeButton, BorderLayout.EAST);
        topPanel.add(headerPanel);

        JPanel infoGridPanel = createInfoGridPanel(reserva);
        topPanel.add(infoGridPanel);

        popupContainer.add(topPanel, BorderLayout.NORTH);

        MaterialTabbed tabbedPane = new MaterialTabbed();
        tabbedPane.setForeground(GRAY);

        JPanel infoPanel = createInfoPanel(reserva);
        tabbedPane.addTab("Informações Gerais", infoPanel);

        JPanel pessoasTab = new JPanel();
        pessoasTab.setLayout(new BoxLayout(pessoasTab, BoxLayout.Y_AXIS));
        pessoasTab.setOpaque(false);
        pessoasTab.add(Box.createVerticalStrut(10));
        createGoogleStyleBuscaPessoaPanel(pessoasTab, reserva);
        tabbedPane.addTab("Pessoas", pessoasTab);

        Font textFont = new Font("Roboto", Font.PLAIN, 14);

        JPanel pagamentosTab = new JPanel();
        pagamentosTab.setLayout(new BoxLayout(pagamentosTab, BoxLayout.Y_AXIS));
        pagamentosTab.setOpaque(false);
        pagamentosTab.setAlignmentX(Component.LEFT_ALIGNMENT);
        pagamentosTab.add(Box.createVerticalStrut(20));

        JPanel descricaoPagamentoPanel = new JPanel(new BorderLayout());
        descricaoPagamentoPanel.setPreferredSize(new Dimension(620, 30));
        descricaoPagamentoPanel.setMaximumSize(new Dimension(620, 30));
        descricaoPagamentoPanel.setBackground(BACKGROUND_GRAY);

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
        adicionarPagamento.setBackground(BACKGROUND_GRAY);

        JLabel tipoPagamento = new JLabel("Tipo de pagamento: ");
        tipoPagamento.setFont(textFont);
        tipoPagamento.setForeground(GRAY);
        adicionarPagamento.add(tipoPagamento, BorderLayout.WEST);

        Vector<String> tipoPagamentoItems = new Vector<>();
        for (TipoPagamentoEnum tipo : TipoPagamentoEnum.values()) {
            tipoPagamentoItems.add(Converter.converterTipoPagamento(String.valueOf(tipo.getCodigo())));
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

        JPanel pagamentosListPanel = new JPanel();

        ShadowButton adicionarPagamentoButton = btn_laranja("Adicionar Pagamento");
        adicionarPagamentoButton.setPreferredSize(new Dimension(150, 40));
        adicionarPagamentoButton.enableHoverEffect();
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
                    Converter.converterTipoPagamento(Objects.requireNonNull(tipoPagamentoSelecionado)),
                    LocalDateTime.now().format(dtf),
                    reserva,
                    pagamentosListPanel);

            pagamentosListPanel.add(pagamentoPanel);

            reservasRepository.adicionarPagamentoReserva(
                    reserva.reserva_id(),
                    new BuscaReservasResponse.Pagamentos(
                            descricao,
                            Converter.converterTipoPagamentoParaInt(Objects.requireNonNull(tipoPagamentoSelecionado)),
                            Float.parseFloat(valor),
                            LocalDateTime.now())
            );

            notification(Type.SUCCESS, TOP_CENTER, "Pagamento Adicionado: \n" + descricao + "\n R$ " + FormatarFloat.format(Float.parseFloat(valor)));

            pagamentosListPanel.revalidate();
            pagamentosListPanel.repaint();
        });

        JPanel adicionarPagamentoButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        adicionarPagamentoButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 10));
        adicionarPagamentoButtonPanel.add(adicionarPagamentoButton);
        adicionarPagamentoButtonPanel.setPreferredSize(new Dimension(150, 30));
        adicionarPagamentoButtonPanel.setBackground(BACKGROUND_GRAY);
        pagamentosTab.add(adicionarPagamentoButtonPanel);

        pagamentosListPanel.setLayout(new BoxLayout(pagamentosListPanel, BoxLayout.Y_AXIS));
        pagamentosListPanel.setOpaque(false);

        JScrollPane pagamentosScrollPane = new JScrollPane(pagamentosListPanel);
        pagamentosScrollPane.setPreferredSize(new Dimension(900, 200));
        pagamentosScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        pagamentosTab.add(pagamentosScrollPane);

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
        rightButtons.add(pernoiteButton);

        buttonPanel.add(leftButtons, BorderLayout.WEST);
        buttonPanel.add(rightButtons, BorderLayout.EAST);

        cancelarReserva(reserva, shadowButton);
        novoPernoite(reserva, shadowButton);

        popupContainer.add(buttonPanel, BorderLayout.SOUTH);
        popupContainer.setPreferredSize(new Dimension(800, 600));

        SwingUtilities.invokeLater(() -> {
            popupContainer.revalidate();
            popupContainer.repaint();
        });

        closeButton.addActionListener(e -> shadowButton.closeJDialog());

        carregarPagamentosExistentes(reserva, pagamentosListPanel);
    }

    private JPanel createPagamentoPanel(String descricao, String valor, String tipoPagamento, String dataHora, BuscaReservasResponse reserva, JPanel pagamentosListPanel) {
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
            reservasRepository.removerPagamentoReserva(reserva.reserva_id(), descricao);
            pagamentosListPanel.remove(pagamentoPanel);
            pagamentosListPanel.revalidate();
            pagamentosListPanel.repaint();

            notification(Type.WARNING, TOP_CENTER, "Pagamento removido: \n" + descricao + "\n R$ " + FormatarFloat.format(Float.parseFloat(valor)));
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


    private void carregarPagamentosExistentes(BuscaReservasResponse reserva, JPanel pagamentosListPanel) {
        List<BuscaReservasResponse.Pagamentos> pagamentos = reservasRepository.buscarPagamentosPorReserva(reserva.reserva_id());
        for (BuscaReservasResponse.Pagamentos pagamento : pagamentos) {
            JPanel pagamentoPanel = createPagamentoPanel(pagamento.descricao(), String.valueOf(pagamento.valor_pagamento()), Converter.converterTipoPagamento(pagamento.tipo_pagamento()), pagamento.data_hora_pagamento().format(dtf), reserva, pagamentosListPanel);
            pagamentosListPanel.add(pagamentoPanel);
        }
        pagamentosListPanel.revalidate();
        pagamentosListPanel.repaint();
    }

    private JPanel createInfoGridPanel(BuscaReservasResponse reserva) {
        JPanel infoGridPanel = new JPanel(new GridLayout(2, 2, 15, 5));
        infoGridPanel.setOpaque(false);
        infoGridPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        labelPessoasValue = createLabel(String.valueOf(reserva.pessoas().size()), new Font("Roboto", Font.BOLD, 14), DARK_GRAY, null);
        labelValorDiariaValue = createLabel("R$ 0,00", new Font("Roboto", Font.BOLD, 14), DARK_GRAY, null);
        labelDiariasValue = createLabel("0", new Font("Roboto", Font.BOLD, 14), DARK_GRAY, null);
        labelTotalValue = createLabel("R$ 0,00", new Font("Roboto", Font.BOLD, 14), GREEN, null);
        infoGridPanel.add(createFlowPanel("Pessoas:", labelPessoasValue));
        infoGridPanel.add(createFlowPanel("Valor diária:", labelValorDiariaValue));
        infoGridPanel.add(createFlowPanel("Diárias:", labelDiariasValue));
        infoGridPanel.add(createFlowPanel("Total:", labelTotalValue));
        return infoGridPanel;
    }

    private JPanel createFlowPanel(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        panel.add(createLabel(labelText, new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null));
        panel.add(valueLabel);
        return panel;
    }

    private JPanel createInfoPanel(BuscaReservasResponse reserva) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel quartoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quartoPanel.setOpaque(false);
        JLabel quartoLabel = createLabel("Quarto:", new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null);
        JLabel categoriaLabel = createLabel("Categoria:", new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null);
        JLabel categoriaDescricaoLabel = createLabel(
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

        quartoPanel.add(quartoLabel);
        quartoPanel.add(quartoComboBox);
        quartoPanel.add(Box.createHorizontalStrut(5));
        quartoPanel.add(categoriaLabel);
        quartoPanel.add(categoriaDescricaoLabel);
        infoPanel.add(quartoPanel);

        JPanel mainHorizontalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainHorizontalPanel.setBorder(BorderFactory.createEmptyBorder(-8, 0, 0, 0));
        mainHorizontalPanel.setBackground(BACKGROUND_GRAY);

        JPanel datePanel = new JPanel(new BorderLayout());
        JPanel checkinCheckoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        checkinCheckoutPanel.setOpaque(false);

        JLabel checkinLabel = createLabel("Check-in:", new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null);
        checkinLabel.setPreferredSize(new Dimension(70, 25));
        checkinField = new JFormattedTextField();
        checkinField.setText(reserva.data_entrada().format(df));
        checkinField.setColumns(7);

        JLabel checkoutLabel = createLabel("Check-out:", new Font("Roboto", Font.PLAIN, 14), DARK_GRAY, null);
        checkoutLabel.setPreferredSize(new Dimension(70, 25));
        checkoutField = new JFormattedTextField();
        checkoutField.setText(reserva.data_saida().format(df));
        checkoutField.setColumns(7);

        checkinCheckoutPanel.add(checkinLabel);
        checkinCheckoutPanel.add(checkinField);
        checkinCheckoutPanel.add(checkoutLabel);
        checkinCheckoutPanel.add(checkoutField);
        datePanel.add(checkinCheckoutPanel, BorderLayout.NORTH);

        DatePicker datePickerRange = new DatePicker();
        datePickerRange.setReservasDoQuarto(reservasDoQuarto);
        datePickerRange.setDateSelectionAble(date -> true);
        datePickerRange.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
        datePickerRange.setSelectedDateRange(reserva.data_entrada(), reserva.data_saida());
        datePickerRange.setPreferredSize(new Dimension(260, 250));
        datePickerRange.setAlignmentX(Component.CENTER_ALIGNMENT);
        datePanel.add(datePickerRange, BorderLayout.CENTER);

        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setOpaque(false);
        JLabel horarioTitulo = createLabel(
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

        Runnable updateDiariasInfo = () -> {
            try {
                LocalDate newIn = LocalDate.parse(checkinField.getText(), df);
                LocalDate newOut = LocalDate.parse(checkoutField.getText(), df);
                checkinDate = newIn;
                checkoutDate = newOut;
            } catch (Exception ex) {

            }
        };

        datePickerRange.addDateSelectionListener(e -> {
            LocalDate[] sel = datePickerRange.getSelectedDateRange();
            if (sel != null && sel[0] != null && sel[1] != null) {
                checkinField.setText(sel[0].format(df));
                checkoutField.setText(sel[1].format(df));
                updateDiariasInfo.run();
            }
        });

        checkinField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            public void update() {
                updateDiariasInfo.run();
                try {
                    LocalDate novaIn = LocalDate.parse(checkinField.getText(), df);
                    long quartoId = Long.parseLong(
                            ((String) quartoComboBox.getSelectedItem())
                                    .split(" - ")[0]
                                    .replace("Quarto ", "")
                                    .replaceFirst("^0+", "")
                    );

                    if (reservasRepository.existeConflitoReserva(quartoId, novaIn, checkoutDate, reserva.reserva_id())) {
                        notification(Type.ERROR, TOP_CENTER, "Conflito de reserva neste período!");

                        checkinField.setText(checkinDate.format(df));
                        return;
                    }

                    checkinDate = novaIn;
                    reservasRepository.atualizarDataEntrada(reserva.reserva_id(), novaIn);
                    notification(Type.SUCCESS, TOP_CENTER, "Check-in atualizado para " + novaIn.format(df));
                    refreshPanel();
                } catch (Exception ex) {}
            }
        });

        checkoutField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            public void update() {
                updateDiariasInfo.run();
                try {
                    LocalDate novaOut = LocalDate.parse(checkoutField.getText(), df);
                    long quartoId = Long.parseLong(
                            ((String) quartoComboBox.getSelectedItem())
                                    .split(" - ")[0]
                                    .replace("Quarto ", "")
                                    .replaceFirst("^0+", "")
                    );
                    if (reservasRepository.existeConflitoReserva(quartoId, checkinDate, novaOut, reserva.reserva_id())) {
                        notification(Type.ERROR, TOP_CENTER, "Conflito de reserva neste período!");
                        checkoutField.setText(checkoutDate.format(df));
                        return;
                    }
                    checkoutDate = novaOut;
                    reservasRepository.atualizarDataSaida(reserva.reserva_id(), novaOut);
                    notification(Type.SUCCESS, TOP_CENTER, "Check-out atualizado para " + novaOut.format(df));
                    refreshPanel();
                } catch (Exception ex) {
                }
            }
        });

        quartoComboBox.addActionListener(e -> {
            long novoQuarto = Long.parseLong(
                    ((String) quartoComboBox.getSelectedItem())
                            .split(" - ")[0]
                            .replace("Quarto ", "")
                            .replaceFirst("^0+", "")
            );

            if (reservasRepository.existeConflitoReserva(novoQuarto, checkinDate, checkoutDate, reserva.reserva_id())) {
                notification(Type.ERROR, TOP_CENTER, "Este quarto já está reservado no período selecionado!");

                quartoComboBox.setSelectedItem(
                        "Quarto " + reserva.quarto() + " - " + selectedPeople.size() + " pessoas"
                );
                return;
            }

            reservasRepository.atualizarQuarto(reserva.reserva_id(), novoQuarto);
            categoriaDescricaoLabel.setText(
                    quartosRepository.buscaQuartoPorId(novoQuarto)
                            .categoria()
                            .categoria()
                            .toUpperCase()
            );
            notification(Type.SUCCESS, TOP_CENTER, "Alterado para o Quarto: " + novoQuarto);
            refreshPanel();
        });

        return infoPanel;
    }


    private void animateLabelSpin(JLabel label, double oldValue, double newValue, boolean isMoney) {
        if (oldValue == newValue) return;
        final boolean goingUp = (newValue > oldValue);
        final int totalSteps = 25;
        final double step = (newValue - oldValue) / totalSteps;
        final int delayMs = 30;
        final double[] currentValue = {oldValue};
        Timer timer = new Timer(delayMs, null);
        timer.addActionListener(e -> {
            currentValue[0] += step;
            boolean acabou = goingUp ? (currentValue[0] >= newValue) : (currentValue[0] <= newValue);
            if (acabou) {
                currentValue[0] = newValue;
                timer.stop();
            }
            label.setText(isMoney ? String.format("R$ %.2f", currentValue[0]) : String.format("%.0f", currentValue[0]));
        });
        timer.start();
    }

    private double parseLabelValue(String text) {
        text = text.replace("R$", "").replace(",", ".").trim();
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private BotaoArredondado adicionarBlocoPessoa(
            PessoaResponse pessoa,
            BuscaReservasResponse.Pessoas registro,
            BuscaReservasResponse reserva,
            JPanel pessoasContainer
    ) {
        BufferedImage foto = null;
        try { foto = pessoaRepository.buscarFotoBufferedPessoaPorId(pessoa.id()); }
        catch (SQLException | IOException ignored) {}

        LabelArredondado labelFoto = new LabelArredondado("");
        labelFoto.setBackground(BACKGROUND_GRAY);
        ImageIcon icon = (foto != null)
                ? resizeIcon(new ImageIcon(arredondar(foto)), 50, 50)
                : resizeIcon(new ImageIcon(arredondar(
                convertImageIconToBufferedImage(
                        pessoa.sexo().equals(FEMININO.ordinal()) ? user_sem_foto_feminino : user_sem_foto
                )
        )), 50, 50);
        labelFoto.setIcon(icon);

        BotaoArredondado bloco = new BotaoArredondado("");
        bloco.setBorderPainted(false);
        bloco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bloco.setLayout(new BorderLayout());
        bloco.setOpaque(false);
        bloco.setContentAreaFilled(false);
        bloco.setFocusPainted(false);
        bloco.setBackground(BACKGROUND_GRAY);
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

        ShadowButton definir = btn_backgroung("Definir Representante");
        definir.setPreferredSize(new Dimension(150, 30));
        definir.setFocusPainted(false);
        definir.enableHoverEffect();

        btns.add(registro.representante() ? badge : definir);

        definir.addActionListener(e -> {
            try {
                List<BuscaReservasResponse.Pessoas> todos =
                        reservasRepository.buscarPessoasPorReserva(reserva.reserva_id());
                for (var p : todos) {
                    if (p.representante() && p.pessoa_id() != pessoa.id()) {
                        reservasRepository.definirRepresentanteDaReserva(
                                reserva.reserva_id(), p.pessoa_id(), false);
                    }
                }
                reservasRepository.definirRepresentanteDaReserva(
                        reserva.reserva_id(), pessoa.id(), true);
                notification(Type.SUCCESS, TOP_CENTER,
                        pessoa.nome() + " definido como representante!");

                atualizarPainelPessoas(reserva, pessoasContainer);
            } catch (Exception ex) {
                ex.printStackTrace();
                notification(Type.ERROR, TOP_CENTER,
                        "Erro ao definir representante: " + ex.getMessage());
            }
            refreshPanel();
        });

        ShadowButton remove = btn_backgroung("");
        remove.setIcon(resizeIcon(close, 15, 15));
        remove.setPreferredSize(new Dimension(40, 30));
        remove.setFocusPainted(false);
        remove.enableHoverEffect();
        remove.addActionListener(e -> {
            reservasRepository.removerPessoaReserva(pessoa.id(), reserva.reserva_id());
            notification(Type.WARNING, TOP_CENTER,
                    "Pessoa removida: " + pessoa.nome());
            atualizarPainelPessoas(reserva, pessoasContainer);
            refreshPanel();
        });
        btns.add(remove);

        if (reservasRepository.buscarPessoasPorReserva(reserva.reserva_id()).size() < 2) {
            remove.setEnabled(false);
        }

        bloco.add(labelFoto, BorderLayout.WEST);
        bloco.add(center, BorderLayout.CENTER);
        bloco.add(btns, BorderLayout.EAST);

        return bloco;
    }


    private void createGoogleStyleBuscaPessoaPanel(JPanel pessoasTab, BuscaReservasResponse reserva) {
        selectedPeople.clear();
        reserva.pessoas().forEach(r -> {
            PessoaResponse pessoa = pessoaRepository.buscarPessoaPorID(r.pessoa_id());
            if (!selectedPeople.contains(pessoa)) {
                selectedPeople.add(pessoa);
            }
        });

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel buscarPessoaPanel = new JPanel(new BorderLayout());
        buscarPessoaPanel.setBackground(BACKGROUND_GRAY);

        JTextField buscarPessoaField = new JTextField(40);
        JPanel buscarPessoaInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscarPessoaInputPanel.setBackground(BACKGROUND_GRAY);
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

        atualizarPainelPessoas(reserva, pessoasContainer);

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
                    List<Long> pessoasIds = reserva.pessoas().stream().map(BuscaReservasResponse.Pessoas::pessoa_id).toList();
                    resultados.removeIf(p -> pessoasIds.contains(p.id()));

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
                        reservasRepository.adicionarPessoaReserva(reserva.reserva_id(), pessoaSelecionada.id());
                        notification(Type.SUCCESS, TOP_CENTER, "Pessoa adicionada com sucesso! \n" + pessoaSelecionada.nome());

                        atualizarPainelPessoas(reserva, pessoasContainer);
                        refreshPanel();

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

    private void atualizarPainelPessoas(BuscaReservasResponse reserva, JPanel pessoasContainer) {
        pessoasContainer.removeAll();
        List<BuscaReservasResponse.Pessoas> atuais =
                reservasRepository.buscarPessoasPorReserva(reserva.reserva_id());

        for (BuscaReservasResponse.Pessoas registro : atuais) {
            PessoaResponse pessoa = pessoaRepository.buscarPessoaPorID(registro.pessoa_id());
            BotaoArredondado bloco =
                    adicionarBlocoPessoa(pessoa, registro, reserva, pessoasContainer);
            bloco.setAlignmentX(Component.LEFT_ALIGNMENT);
            pessoasContainer.add(bloco);
        }

        pessoasContainer.revalidate();
        pessoasContainer.repaint();
    }
}
