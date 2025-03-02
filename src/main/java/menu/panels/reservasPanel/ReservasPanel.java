package menu.panels.reservasPanel;

import buttons.Botoes;
import buttons.ShadowButton;
import repository.QuartosRepository;
import repository.ReservasRepository;
import request.BuscaReservasResponse;
import response.QuartoResponse;
import tools.CorPersonalizada;
import tools.Refreshable;
import tools.Resize;
import tools.Icones;
import tools.FormatarFloat;
import tools.Converter;
import tools.TruncateText;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static buttons.Botoes.*;
import static java.time.LocalDate.now;
import static notifications.Notification.notification;
import static notifications.Notifications.Location.TOP_CENTER;
import static notifications.Notifications.Type;
import static tools.CorPersonalizada.*;
import static tools.TruncateText.truncateText;

public class ReservasPanel extends JPanel implements Refreshable {
    private final QuartosRepository quartosRepository = new QuartosRepository();
    private final ReservasRepository reservasRepository = new ReservasRepository();
    private final JFrame menu;
    private LocalDate currentMonth;
    private JPanel daysHeader;
    private JPanel roomsPanel;
    private JPanel backgroundPanel;
    private final Color[] reservationColors = getAllColorsFromCorPersonalizada();
    private final Map<Long, LocalDate> checkInDateMap = new HashMap<>();
    private List<BuscaReservasResponse> currentReservations;
    private final Dimension cellSize = new Dimension(300, 60);
    private final Border defaultCellBorder = BorderFactory.createLineBorder(BACKGROUND_GRAY);
    private final Color selectedColor = GREEN;
    private final Random random = new Random();

    public ReservasPanel(JFrame menu) {
        this.menu = menu;
        this.currentMonth = now();
        initializePanel();
    }

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
        var quartos = quartosRepository.buscaTodosOsQuartos();
        currentReservations = reservasRepository.buscaReservasAtivas();

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        int daysInMonth = currentMonth.lengthOfMonth();
        int numRooms = quartos.size();
        daysHeader = createDaysHeaderPanel(daysInMonth);
        roomsPanel = createRoomsPanel(quartos, numRooms);
        JLayeredPane layeredPane = createLayeredPane(daysInMonth, numRooms);
        JScrollPane scrollPane = createScrollPane(layeredPane);
        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        String yearStr = String.valueOf(currentMonth.getYear());
        JLabel yearLabel = createLabel(yearStr, new Font("Roboto", Font.BOLD, 16), WHITE, BLUE);
        yearLabel.setHorizontalAlignment(SwingConstants.LEFT);
        yearLabel.setPreferredSize(new Dimension(70, 30));
        yearLabel.setMaximumSize(new Dimension(70, 30));

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));
        navPanel.setOpaque(false);
        navPanel.setPreferredSize(new Dimension(260, 30));

        JButton btnPrev = createButton(" < ", BLUE, WHITE, e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshPanel();
        });
        btnPrev.setPreferredSize(new Dimension(50, 30));
        btnPrev.setMaximumSize(new Dimension(50, 30));

        String monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).toUpperCase();
        JLabel monthLabel = createLabel(monthName, new Font("Roboto", Font.BOLD, 16), WHITE, BLUE);
        monthLabel.setPreferredSize(new Dimension(150, 30));
        monthLabel.setMaximumSize(new Dimension(150, 30));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnNext = createButton(" > ", BLUE, WHITE, e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshPanel();
        });
        btnNext.setPreferredSize(new Dimension(50, 30));
        btnNext.setMaximumSize(new Dimension(50, 30));

        navPanel.add(btnPrev);
        navPanel.add(Box.createHorizontalStrut(5));
        navPanel.add(monthLabel);
        navPanel.add(Box.createHorizontalStrut(5));
        navPanel.add(btnNext);

        headerPanel.add(yearLabel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);
        return headerPanel;
    }



    private JPanel createDaysHeaderPanel(int daysInMonth) {
        JPanel daysHeader = new JPanel(new GridLayout(1, daysInMonth, 0, 0));
        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate tmpDate = currentMonth.withDayOfMonth(d);
            String dayStr = String.format("%02d/%02d", d, currentMonth.getMonthValue());
            String dayOfWeek = tmpDate.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("pt", "BR"));
            String labelText = "<html><center>" + dayStr + "<br>" + dayOfWeek + "</center></html>";
            CalendarLabel dayLabel = new CalendarLabel(labelText, d);
            dayLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
            dayLabel.setForeground(WHITE);
            dayLabel.setBackground(tmpDate.isEqual(now()) ? GRAY : BLUE);
            daysHeader.add(dayLabel);
        }
        return daysHeader;
    }

    private JPanel createRoomsPanel(List<QuartoResponse> quartos, int numRooms) {
        JPanel roomsPanel = new JPanel(new GridLayout(numRooms, 1, 0, 0));
        roomsPanel.setBackground(BACKGROUND_GRAY);
        Dimension roomCellSize = new Dimension(60, cellSize.height);
        for (int row = 0; row < numRooms; row++) {
            QuartoResponse quarto = quartos.get(row);
            Long roomId = quarto.quarto_id();
            JLabel roomLabel = createLabel(roomId < 10 ? "0" + roomId : roomId.toString(), new Font("Roboto", Font.BOLD, 14), BLUE, BACKGROUND_GRAY);
            roomLabel.setPreferredSize(roomCellSize);
            roomLabel.setBorder(defaultCellBorder);
            roomsPanel.add(roomLabel);
        }
        return roomsPanel;
    }

    private JLayeredPane createLayeredPane(int daysInMonth, int numRooms) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        int totalWidth = daysInMonth * cellSize.width;
        int totalHeight = numRooms * cellSize.height;
        layeredPane.setPreferredSize(new Dimension(totalWidth, totalHeight));
        backgroundPanel = new JPanel(new GridLayout(numRooms, daysInMonth, 0, 0));
        backgroundPanel.setBounds(0, 0, totalWidth, totalHeight);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);
        for (int row = 0; row < numRooms; row++) {
            QuartoResponse quarto = quartosRepository.buscaTodosOsQuartos().get(row);
            Long roomId = quarto.quarto_id();
            for (int d = 1; d <= daysInMonth; d++) {
                LocalDate date = currentMonth.withDayOfMonth(d);
                CalendarCell cell = new CalendarCell(roomId, date, row, d);
                cell.setBackground(date.isEqual(now()) ? LIGHT_GRAY_2 : WHITE);
                BuscaReservasResponse reserva = findReservationForDate(roomId, date);
                cell.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if (checkInDateMap.containsKey(cell.roomId)) updateSelectionForRoom(cell.roomId, cell.date);
                    }
                });
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (reserva == null) cell.setBackground(BACKGROUND_GRAY);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (reserva == null) {
                            boolean isSelected = isSelectedRange(cell.roomId, cell.date);
                            cell.setBackground(isSelected ? selectedColor : (cell.date.isEqual(now()) ? LIGHT_GRAY_2 : WHITE));
                        }
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
        LocalDate monthStart = currentMonth.withDayOfMonth(1);
        LocalDate monthEnd = currentMonth.withDayOfMonth(daysInMonth);
        for (BuscaReservasResponse reserva : currentReservations) {
            LocalDate resStartDate = reserva.data_entrada();
            LocalDate resEndDate = reserva.data_saida();
            if (resEndDate.isBefore(monthStart) || resStartDate.isAfter(monthEnd)) continue;
            int rowIndex = encontrarIndiceDoQuarto(quartosRepository.buscaTodosOsQuartos(), reserva.quarto());
            if (rowIndex < 0) continue;
            LocalDate clampedStartDate = resStartDate.isBefore(monthStart) ? monthStart : resStartDate;
            LocalDate clampedEndDate = resEndDate.isAfter(monthEnd) ? monthEnd : resEndDate;
            int startDayIndex = clampedStartDate.getDayOfMonth() - 1;
            int endDayIndex = clampedEndDate.getDayOfMonth() - 1;
            int checkInX = startDayIndex * cellSize.width + (cellSize.width / 2);
            int checkOutX = endDayIndex * cellSize.width + (cellSize.width / 2);
            int reservationWidth = checkOutX - checkInX;
            if (endDayIndex == startDayIndex) { reservationWidth = cellSize.width / 2; checkOutX = checkInX + reservationWidth; }
            int roomY = rowIndex * cellSize.height;
            int reservationHeight = cellSize.height;
            ShadowButton faixa = Botoes.btn_cinza("");
            faixa.setBackground(GREEN);
            faixa.setForeground(LIGHT_GRAY_2);
            faixa.setHoverEffect(true);
            faixa.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
            faixa.setLayout(new BoxLayout(faixa, BoxLayout.X_AXIS));
            faixa.setBounds(checkInX, roomY + 6, reservationWidth, reservationHeight - 6);
            ShadowButton qtdPessoa = btn_branco(" " + reserva.pessoas().size() + " ");
            String nome = reserva.pessoas().isEmpty() ? "RESERVADO" : reserva.pessoas().get(0).nome();
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
            if (checkOut.isBefore(checkIn)) { LocalDate temp = checkIn; checkIn = checkOut; checkOut = temp; }
            if (isOverlappingExistingReservation(cell.roomId, checkIn, checkOut)) {
                notification(menu, Type.ERROR, TOP_CENTER, "Período já reservado para este quarto.");
                checkInDateMap.remove(cell.roomId);
            } else {
                showReservationFrame(cell.roomId, checkIn, checkOut);
            }
            resetCellsForRoom(cell.roomId);
        }
    }

    private int encontrarIndiceDoQuarto(List<QuartoResponse> quartos, Long quartoId) {
        for (int i = 0; i < quartos.size(); i++) {
            if (quartos.get(i).quarto_id().equals(quartoId)) return i;
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
        JLabel lblRoom = createLabel("Quarto: " + roomId, new Font("Roboto", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE);
        JLabel lblCheckIn = createLabel("Data de Entrada: " + checkIn, new Font("Roboto", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE);
        JLabel lblCheckOut = createLabel("Data de Saída: " + checkOut, new Font("Roboto", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE);
        frame.add(lblRoom);
        frame.add(lblCheckIn);
        frame.add(lblCheckOut);
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
                cell.setBackground(!cell.date.isBefore(start) && !cell.date.isAfter(end) ? selectedColor : (cell.date.isEqual(now()) ? LIGHT_GRAY_2 : WHITE));
            }
        }
    }

    private boolean isSelectedRange(Long roomId, LocalDate date) {
        LocalDate checkIn = checkInDateMap.get(roomId);
        return checkIn != null && date.equals(checkIn);
    }

    public void popUp(ShadowButton shadowButton, BuscaReservasResponse reserva) {
        ShadowButton popupContainer = new ShadowButton();
        popupContainer.setBackground(BACKGROUND_GRAY);
        popupContainer.setLayout(new BorderLayout());
        popupContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        JLabel reservaLabel = createLabel("Reserva #" + reserva.reserva_id(), new Font("Roboto", Font.BOLD, 16), DARK_GRAY, null);
        reservaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(reservaLabel);
        topPanel.add(Box.createVerticalStrut(5));

        JPanel checkinPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkinPanel.setOpaque(false);
        checkinPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ShadowButton datas = Botoes.btn_backgroung(" " + reserva.data_entrada().format(dtf) + " - " + reserva.data_saida().format(dtf));
        datas.setIcon(Resize.resizeIcon(Icones.calendario, 15, 15));
        datas.setFont(new Font("Roboto", Font.PLAIN, 14));
        checkinPanel.add(datas);
        topPanel.add(checkinPanel);
        topPanel.add(Box.createVerticalStrut(0));

        JPanel previsaoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        previsaoPanel.setOpaque(false);
        previsaoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ShadowButton previsaoTitle = Botoes.btn_backgroung(" 13:50");
        previsaoTitle.setIcon(Resize.resizeIcon(Icones.relogio, 15, 15));
        previsaoTitle.setFont(new Font("Roboto", Font.PLAIN, 14));
        previsaoTitle.setForeground(DARK_GRAY);
        previsaoPanel.add(previsaoTitle);
        topPanel.add(previsaoPanel);
        topPanel.add(Box.createVerticalStrut(5));

        JPanel diariasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        diariasPanel.setOpaque(false);
        diariasPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel diariasTitle = createLabel("Diárias: ", new Font("Roboto", Font.BOLD, 14), DARK_GRAY, null);
        JLabel diariaQtd = createLabel(String.valueOf(Period.between(reserva.data_entrada(), reserva.data_saida()).getDays()), new Font("Roboto", Font.PLAIN, 14), GRAY, null);
        diariasPanel.add(diariasTitle);
        diariasPanel.add(diariaQtd);
        topPanel.add(diariasPanel);
        topPanel.add(Box.createVerticalStrut(20));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel pessoasPanel = new JPanel();
        pessoasPanel.setLayout(new BoxLayout(pessoasPanel, BoxLayout.Y_AXIS));
        pessoasPanel.setOpaque(false);
        pessoasPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel pessoasHeader = createLabel("Pessoas", new Font("Roboto", Font.BOLD, 14), DARK_GRAY, null);
        pessoasPanel.add(pessoasHeader);
        pessoasPanel.add(Box.createVerticalStrut(5));
        for (var pessoa : reserva.pessoas()) {
            JLabel pessoaLabel = createLabel(pessoa.nome(), new Font("Roboto", Font.PLAIN, 14), GRAY, null);
            JLabel pessoaTelefoneLabel = createLabel(pessoa.telefone(), new Font("Roboto", Font.PLAIN, 14), GRAY, null);
            pessoasPanel.add(pessoaLabel);
            pessoasPanel.add(pessoaTelefoneLabel);
            pessoasPanel.add(Box.createVerticalStrut(15));
        }
        pessoasPanel.add(Box.createVerticalStrut(10));
        JPanel pagamentosPanel = new JPanel();
        pagamentosPanel.setLayout(new BoxLayout(pagamentosPanel, BoxLayout.Y_AXIS));
        pagamentosPanel.setOpaque(false);
        pagamentosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel pagamentosHeader = createLabel("Pagamentos", new Font("Roboto", Font.BOLD, 15), DARK_GRAY, null);
        pagamentosPanel.add(pagamentosHeader);
        pagamentosPanel.add(Box.createVerticalStrut(5));
        for (var pagamento : reserva.pagamentos()) {
            JPanel pagamentoLinePanel = new JPanel(new BorderLayout());
            pagamentoLinePanel.setOpaque(false);
            pagamentoLinePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel pagamentoLeftLabel = createLabel(
                    pagamento.data_hora_pagamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " " +
                            Converter.converterTipoPagamento(pagamento.tipo_pagamento()),
                    new Font("Roboto", Font.PLAIN, 14), GRAY, null);
            JLabel pagamentoRightLabel = createLabel(
                    "     R$ " + FormatarFloat.format(pagamento.valor_pagamento()),
                    new Font("Roboto", Font.PLAIN, 14), GRAY, null);
            pagamentoLinePanel.add(pagamentoLeftLabel, BorderLayout.WEST);
            pagamentoLinePanel.add(pagamentoRightLabel, BorderLayout.EAST);
            pagamentosPanel.add(pagamentoLinePanel);
            pagamentosPanel.add(Box.createVerticalStrut(10));
        }
        pagamentosPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(pessoasPanel);
        centerPanel.add(pagamentosPanel);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftButtonsPanel.setOpaque(false);
        ShadowButton editarButton = Botoes.btn_branco("Editar");
        editarButton.setHoverEffect(true);
        ShadowButton cancelarButton = Botoes.btn_vermelho("Cancelar");
        cancelarButton.setHoverEffect(true);
        leftButtonsPanel.add(editarButton);
        leftButtonsPanel.add(cancelarButton);
        JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightButtonsPanel.setOpaque(false);
        ShadowButton pernoiteButton = Botoes.btn_azul("Pernoite");
        pernoiteButton.setHoverEffect(true);
        rightButtonsPanel.add(pernoiteButton);
        bottomPanel.add(leftButtonsPanel, BorderLayout.WEST);
        bottomPanel.add(rightButtonsPanel, BorderLayout.EAST);
        popupContainer.add(topPanel, BorderLayout.NORTH);
        popupContainer.add(centerPanel, BorderLayout.CENTER);
        popupContainer.add(bottomPanel, BorderLayout.SOUTH);
        shadowButton.showPopupWithButtons(popupContainer);
    }

    private Color[] getAllColorsFromCorPersonalizada() {
        List<Color> colors = new ArrayList<>();
        Field[] fields = CorPersonalizada.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(Color.class)) {
                try {
                    Color color = (Color) field.get(null);
                    if (!field.getName().equals("WHITE") &&
                            !field.getName().equals("LIGHT_GRAY") &&
                            !field.getName().equals("LIGHT_GRAY_2") &&
                            !field.getName().equals("BACKGROUND_GRAY"))
                        colors.add(color);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return colors.toArray(new Color[0]);
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

    private JButton createButton(String text, Color background, Color foreground, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.addActionListener(listener);
        return button;
    }
}
