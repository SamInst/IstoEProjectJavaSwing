package menu.panels.reservasPanel;

import buttons.Botoes;
import buttons.ShadowButton;
import calendar2.DatePicker;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import customOptionPane.GlassPanePopup;
import customOptionPane.Message;
import repository.PernoitesRepository;
import repository.PessoaRepository;
import repository.QuartosRepository;
import repository.ReservasRepository;
import request.BuscaReservasResponse;
import request.PernoiteRequest;
import response.DatasReserva;
import response.QuartoResponse;
import timePicker.time.TimePicker;
import tools.*;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.*;

import static buttons.Botoes.*;
import static java.time.LocalDate.now;
import static notifications.Notification.notification;
import static notifications.Notifications.Location.TOP_CENTER;
import static notifications.Notifications.Type;
import static tools.CorPersonalizada.*;
import static tools.Icones.*;
import static tools.Resize.resizeIcon;
import static tools.TruncateText.truncateText;

public class ReservasPanel extends JPanel implements Refreshable {
    private final QuartosRepository quartosRepository = new QuartosRepository();
    private final ReservasRepository reservasRepository = new ReservasRepository();
    private final PernoitesRepository pernoitesRepository = new PernoitesRepository();
    private final PessoaRepository pessoaRepository = new PessoaRepository();

    private final JFrame menu;
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
    DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

    ShadowButton pernoiteButton = btn_azul("Pernoite");
    ShadowButton editarButton = btn_branco("Editar");
    ShadowButton cancelarButton = btn_vermelho("Cancelar");

    private List<DatasReserva> reservasDoQuarto;

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
        JLabel yearLabel = createLabel(yearStr, new Font("sansseriff", Font.BOLD, 16), WHITE, BLUE);
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
        JLabel monthLabel = createLabel(monthName, new Font("sansseriff", Font.BOLD, 16), WHITE, BLUE);
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
            dayLabel.setFont(new Font("sansseriff", Font.PLAIN, 14));
            dayLabel.setForeground(WHITE);
            dayLabel.setBackground(tmpDate.isEqual(now()) ? GRAY.brighter() : BLUE.brighter());
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
            JLabel roomLabel = createLabel(roomId < 10 ? "0" + roomId : roomId.toString(), new Font("sansseriff", Font.BOLD, 14), BLUE, BACKGROUND_GRAY);
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

            reservasDoQuarto = reservasRepository.datasReservadasPorQuarto(reserva.quarto(), reserva.reserva_id());

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
            if (endDayIndex == startDayIndex) {
                reservationWidth = cellSize.width / 2;
                checkOutX = checkInX + reservationWidth;
            }
            int roomY = rowIndex * cellSize.height;
            int reservationHeight = cellSize.height;
            ShadowButton faixa = btn_cinza("");
            faixa.setBackground(GREEN);
            faixa.setForeground(LIGHT_GRAY_2);
            faixa.enableHoverEffect();
            faixa.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
            faixa.setLayout(new BoxLayout(faixa, BoxLayout.X_AXIS));
            faixa.setBounds(checkInX, roomY + 6, reservationWidth, reservationHeight - 6);
            ShadowButton qtdPessoa = btn_branco(" " + reserva.pessoas().size() + " ");
            String nome = reserva.pessoas().isEmpty() ? "RESERVADO" : reserva.pessoas().get(0).nome();
            JLabel labelNome = new JLabel(truncateText(nome, qtdPessoa, faixa.getWidth() - 90));
            labelNome.setFont(new Font("sansseriff", Font.PLAIN, 13));
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
        JLabel lblRoom = createLabel("Quarto: " + roomId, new Font("sansseriff", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE);
        JLabel lblCheckIn = createLabel("Data de Entrada: " + checkIn, new Font("sansseriff", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE);
        JLabel lblCheckOut = createLabel("Data de Saída: " + checkOut, new Font("sansseriff", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE);
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
        return date.equals(checkIn);
    }

    public void popUp(ShadowButton shadowButton, BuscaReservasResponse reserva) {
        ShadowButton popupContainer = new ShadowButton();
        popupContainer.setBackground(BACKGROUND_GRAY);
        popupContainer.setLayout(new BorderLayout());
        popupContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = createLabel("Detalhes da Reserva #" + reserva.reserva_id(), new Font("sansseriff", Font.BOLD, 16), DARK_GRAY, null);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        ShadowButton closeButton = Botoes.btn_backgroung("");
        closeButton.setIcon(resizeIcon(close, 15, 15));
        closeButton.setPreferredSize(new Dimension(40, 30));
        closeButton.addActionListener(e -> shadowButton.closePopup());
        closeButton.enableHoverEffect();
        headerPanel.add(closeButton, BorderLayout.EAST);

        popupContainer.add(headerPanel, BorderLayout.NORTH);

        JPanel reservaPanel = new JPanel();
        reservaPanel.setLayout(new BoxLayout(reservaPanel, BoxLayout.Y_AXIS));
        reservaPanel.setOpaque(false);
        reservaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        reservaPanel.add(Box.createVerticalStrut(10));

        ShadowButton quarto = btn_backgroung("Quarto: " + (reserva.quarto() < 10 ? "0" + reserva.quarto() : reserva.quarto().toString()));
        quarto.setIcon(resizeIcon(Icones.quarto, 20, 20));

        ShadowButton datas = btn_backgroung(" Checkin: " + reserva.data_entrada().format(df) + "  |  Checkout: " + reserva.data_saida().format(df));
        datas.setIcon(resizeIcon(calendario, 15, 15));

        ShadowButton horarioPrevisto = btn_backgroung(" Horário previsto de chegada: " + reserva.hora_prevista().format(tf));
        horarioPrevisto.setIcon(resizeIcon(relogio, 15, 15));

        ShadowButton diarias = btn_backgroung(" Diárias: " + Period.between(
                reserva.data_entrada(),
                reserva.data_saida()).getDays()
        );
        diarias.setIcon(resizeIcon(diarias_quantidade, 15, 15));

        reservaPanel.add(quarto);
        reservaPanel.add(datas);
        reservaPanel.add(horarioPrevisto);
        reservaPanel.add(diarias);

        JPanel pessoasPanel = new JPanel();
        pessoasPanel.setLayout(new BoxLayout(pessoasPanel, BoxLayout.Y_AXIS));
        pessoasPanel.setOpaque(false);
        pessoasPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pessoasPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

        for (var pessoa : reserva.pessoas()) {
            JLabel pessoaLabel = createLabel(pessoa.nome(), new Font("sansseriff", Font.PLAIN, 14), GRAY, null);
            JLabel pessoaTelefoneLabel = createLabel(pessoa.telefone(), new Font("sansseriff", Font.PLAIN, 14), GRAY, null);
            pessoasPanel.add(pessoaLabel);
            pessoasPanel.add(pessoaTelefoneLabel);
            pessoasPanel.add(Box.createVerticalStrut(15));
        }

        JPanel pagamentosPanel = new JPanel();
        pagamentosPanel.setLayout(new BoxLayout(pagamentosPanel, BoxLayout.Y_AXIS));
        pagamentosPanel.setOpaque(false);
        pagamentosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (var pagamento : reserva.pagamentos()) {
            JPanel pagamentoLinePanel = new JPanel();
            pagamentoLinePanel.setLayout(new BoxLayout(pagamentoLinePanel, BoxLayout.Y_AXIS));
            pagamentoLinePanel.setOpaque(false);
            pagamentoLinePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            pagamentoLinePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

            JLabel descricaoPagamento = createLabel(
                    pagamento.descricao(),
                    new Font("sansseriff", Font.PLAIN, 16), DARK_GRAY, null);

            JLabel pagamentoLeftLabel = createLabel(
                    pagamento.data_hora_pagamento().format(dtf) + " " +
                            Converter.converterTipoPagamento(pagamento.tipo_pagamento()),
                    new Font("sansseriff", Font.PLAIN, 14), GRAY, null
            );

            JLabel pagamentoRightLabel = createLabel(
                    "     R$ " + FormatarFloat.format(pagamento.valor_pagamento()),
                    new Font("sansseriff", Font.PLAIN, 14), GRAY, null
            );

            pagamentoRightLabel.setForeground(DARK_GREEN);
            pagamentoLinePanel.add(descricaoPagamento, BorderLayout.NORTH);
            pagamentoLinePanel.add(pagamentoLeftLabel, BorderLayout.WEST);
            pagamentoLinePanel.add(pagamentoRightLabel, BorderLayout.EAST);
            pagamentosPanel.add(pagamentoLinePanel);
            pagamentosPanel.add(Box.createVerticalStrut(15));
        }

        MaterialTabbed tabbedPane = new MaterialTabbed();
        tabbedPane.addTab("Informações", reservaPanel);
        tabbedPane.addTab("Pessoas (" + reserva.pessoas().size() + ")", pessoasPanel);
        tabbedPane.addTab("Pagamentos", pagamentosPanel);
        tabbedPane.setForeground(GRAY);

        popupContainer.add(tabbedPane, BorderLayout.CENTER);

        int popupWidth = Math.max(800, popupContainer.getPreferredSize().width);
        int popupHeight = popupContainer.getPreferredSize().height + 350;
        popupContainer.setPreferredSize(new Dimension(popupWidth, popupHeight));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtons.add(editarButton);
        leftButtons.add(cancelarButton);

        JPanel rightButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButton.add(pernoiteButton);

        buttonPanel.add(leftButtons, BorderLayout.WEST);
        buttonPanel.add(rightButton, BorderLayout.EAST);

        popupContainer.add(buttonPanel, BorderLayout.SOUTH);

        shadowButton.showPopupWithButtons(popupContainer);

        GlassPanePopup.install(menu);

        cancelarReserva(reserva);
        novoPernoite(reserva);
        editarReserva(popupContainer, reserva);
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

    private void cancelarReserva(BuscaReservasResponse reserva) {
        for (ActionListener al : cancelarButton.getActionListeners()) {
            cancelarButton.removeActionListener(al);
        }

        cancelarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String quartoInfo = "Quarto " + reserva.quarto();

                StringBuilder pessoasFormatadas = new StringBuilder();
                if (!reserva.pessoas().isEmpty()) {
                    for (var pessoa : reserva.pessoas()) {
                        pessoasFormatadas.append(pessoa.nome())
                                .append(" - ")
                                .append(pessoa.telefone())
                                .append("\n");
                    }
                } else {
                    pessoasFormatadas.append("Nenhuma pessoa registrada.");
                }

                StringBuilder pagamentosFormatados = new StringBuilder();
                if (!reserva.pagamentos().isEmpty()) {
                    for (var pagamento : reserva.pagamentos()) {
                        pagamentosFormatados.append(pagamento.data_hora_pagamento().format(dtf))
                                .append(" ")
                                .append(Converter.converterTipoPagamento(pagamento.tipo_pagamento()))
                                .append(" R$ ")
                                .append(String.format("%.2f", pagamento.valor_pagamento()))
                                .append("\n");
                    }
                } else pagamentosFormatados.append("Nenhum pagamento registrado.");

                Message obj = new Message(
                        "Deseja cancelar a reserva?",
                        "ID #" + reserva.reserva_id() + " - " + quartoInfo + "\n" +
                                "Check-in: " + reserva.data_entrada() + "  |  Checkout: " + reserva.data_saida() + "\n" +
                                "\n👥 Pessoas:\n" + pessoasFormatadas +
                                "\n💳 Pagamentos:\n" + pagamentosFormatados,
                        "Cancelar",
                        "Não",
                        RED_2);

                obj.eventOK(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        reservasRepository.desativarReserva(reserva.reserva_id());
                        GlassPanePopup.closePopupLast();
                        refreshPanel();
                        notification(menu, Type.SUCCESS, TOP_CENTER,
                                "Reserva cancelada com sucesso! \n" +
                                        "#" + reserva.reserva_id() + " - " + quartoInfo + "\n" +
                                        reserva.pessoas().get(0).nome()
                        );
                    }
                });
                GlassPanePopup.showPopup(obj);
            }
        });
    }

    private void novoPernoite(BuscaReservasResponse reserva) {
        for (ActionListener al : pernoiteButton.getActionListeners()) {
            pernoiteButton.removeActionListener(al);
        }

        pernoiteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String quartoInfo = "Quarto " + reserva.quarto();

                StringBuilder pessoasFormatadas = new StringBuilder();
                if (!reserva.pessoas().isEmpty()) {
                    for (var pessoa : reserva.pessoas()) {
                        pessoasFormatadas.append(pessoa.nome())
                                .append(" - ")
                                .append(pessoa.telefone())
                                .append("\n");
                    }
                } else pessoasFormatadas.append("Nenhuma pessoa registrada.");

                StringBuilder pagamentosFormatados = new StringBuilder();
                if (!reserva.pagamentos().isEmpty()) {
                    for (var pagamento : reserva.pagamentos()) {
                        pagamentosFormatados.append(pagamento.data_hora_pagamento().format(dtf))
                                .append(" ")
                                .append(Converter.converterTipoPagamento(pagamento.tipo_pagamento()))
                                .append(" R$ ")
                                .append(String.format("%.2f", pagamento.valor_pagamento()))
                                .append("\n");
                    }
                } else {
                    pagamentosFormatados.append("Nenhum pagamento registrado.");
                }

                Message obj = new Message(
                        "Deseja mover para pernoites?",
                        "ID #" + reserva.reserva_id() + " - " + quartoInfo + "\n" +
                                "Check-in: " + reserva.data_entrada() + "  |  Checkout: " + reserva.data_saida() + "\n" +
                                "\n👥 Pessoas:\n" + pessoasFormatadas +
                                "\n💳 Pagamentos:\n" + pagamentosFormatados,
                        "Novo Pernoite",
                        "Cancelar",
                        BLUE);

                obj.eventOK(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        mudarReservaParaPernoite(reserva);
                        GlassPanePopup.closePopupLast();
                        refreshPanel();
                        notification(menu, Type.SUCCESS, TOP_CENTER,
                                "Pernoite adicionado com sucesso! \n" +
                                        "#" + reserva.reserva_id() + " - " + quartoInfo + "\n" +
                                        reserva.pessoas().get(0).nome()
                        );
                    }
                });
                GlassPanePopup.showPopup(obj);
            }
        });
    }

    private void mudarReservaParaPernoite(BuscaReservasResponse reserva) {
        List<Long> pessoasIds = new ArrayList<>();
        List<BuscaReservasResponse.Pagamentos> pagamentosList = new ArrayList<>(reserva.pagamentos());

        reserva.pessoas().forEach(pessoa -> pessoasIds.add(pessoa.pessoa_id()));

        var diaria = quartosRepository.getValorCategoria(reserva.quarto(), reserva.pessoas().size());
        var qtd_dias = Period.between(reserva.data_entrada(), reserva.data_saida()).getDays();
        var total = diaria * qtd_dias;

        reservasRepository.desativarReserva(reserva.reserva_id());
        pernoitesRepository.adicionarPernoite(new PernoiteRequest(
                reserva.quarto(),
                reserva.data_entrada(),
                reserva.data_saida(),
                reserva.pessoas().size(),
                pessoasIds,
                pagamentosList,
                total
        ));
    }

    private void editarReserva(ShadowButton popupContainer, BuscaReservasResponse reserva) {
        editarButton.addActionListener(a -> {
            FlatLaf.registerCustomDefaultsSource("themes");
            FlatMacLightLaf.setup();
            popupContainer.removeAll();
            popupContainer.revalidate();
            popupContainer.repaint();

            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.setOpaque(false);

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);
            JLabel titleLabel = createLabel("Editar reserva #" + reserva.reserva_id(), new Font("sansseriff", Font.BOLD, 17), DARK_GRAY, null);
            headerPanel.add(titleLabel, BorderLayout.WEST);
            ShadowButton closeButton = Botoes.btn_backgroung("");
            closeButton.setIcon(Resize.resizeIcon(close, 15, 15));
            closeButton.enableHoverEffect();
            closeButton.addActionListener(e -> popupContainer.closePopup());
            headerPanel.add(closeButton, BorderLayout.EAST);
            topPanel.add(headerPanel);

            JPanel infoGridPanel = new JPanel(new GridLayout(2, 2, 15, 5));
            infoGridPanel.setOpaque(false);
            infoGridPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
            JLabel labelPessoas = createLabel("Pessoas:", new Font("sansseriff", Font.PLAIN, 14), DARK_GRAY, null);
            JLabel labelPessoasValue = createLabel(String.valueOf(reserva.pessoas().size()), new Font("sansseriff", Font.BOLD, 14), DARK_GRAY, null);
            JLabel labelValorDiaria = createLabel("Valor diária:", new Font("sansseriff", Font.PLAIN, 14), DARK_GRAY, null);
            JLabel labelValorDiariaValue = createLabel("R$ 0,00", new Font("sansseriff", Font.BOLD, 14), DARK_GRAY, null);
            JLabel labelDiarias = createLabel("Diárias:", new Font("sansseriff", Font.PLAIN, 14), DARK_GRAY, null);
            JLabel labelDiariasValue = createLabel("0", new Font("sansseriff", Font.BOLD, 14), DARK_GRAY, null);
            JLabel labelTotal = createLabel("Total:", new Font("sansseriff", Font.PLAIN, 14), DARK_GRAY, null);
            JLabel labelTotalValue = createLabel("R$ 0,00", new Font("sansseriff", Font.BOLD, 14), GREEN, null);
            JPanel pessoasPanelGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            pessoasPanelGrid.setOpaque(false);
            pessoasPanelGrid.add(labelPessoas);
            pessoasPanelGrid.add(labelPessoasValue);
            JPanel valorDiariaPanelGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            valorDiariaPanelGrid.setOpaque(false);
            valorDiariaPanelGrid.add(labelValorDiaria);
            valorDiariaPanelGrid.add(labelValorDiariaValue);
            JPanel diariasPanelGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            diariasPanelGrid.setOpaque(false);
            diariasPanelGrid.add(labelDiarias);
            diariasPanelGrid.add(labelDiariasValue);
            JPanel totalPanelGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            totalPanelGrid.setOpaque(false);
            totalPanelGrid.add(labelTotal);
            totalPanelGrid.add(labelTotalValue);
            infoGridPanel.add(pessoasPanelGrid);
            infoGridPanel.add(valorDiariaPanelGrid);
            infoGridPanel.add(diariasPanelGrid);
            infoGridPanel.add(totalPanelGrid);
            topPanel.add(infoGridPanel);
            popupContainer.add(topPanel, BorderLayout.NORTH);

            MaterialTabbed tabbedPane = new MaterialTabbed();
            tabbedPane.setForeground(GRAY);

            List<BuscaReservasResponse.Pessoas> pessoasSelecionadas = new ArrayList<>(reserva.pessoas());

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

            JPanel quartoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            quartoPanel.setOpaque(false);
            JLabel quartoLabel = createLabel("Quarto:", new Font("sansseriff", Font.PLAIN, 14), DARK_GRAY, null);
            JLabel categoriaLabel = createLabel("Categoria:", new Font("sansseriff", Font.PLAIN, 14), DARK_GRAY, null);
            JLabel categoriaDescricaoLabel = createLabel(quartosRepository.buscaQuartoPorId(reserva.quarto()).categoria().categoria().toUpperCase(), new Font("sansseriff", Font.BOLD, 14), DARK_GRAY, null);
            Vector<String> roomItems = new Vector<>();
            for (QuartoResponse q : quartosRepository.buscaTodosOsQuartos()) {
                roomItems.add(q.quarto_id() + " - " + q.quantidade_pessoas() + " - " + q.status_quarto_enum());
            }
            final JComboBox<String> quartoComboBox = new JComboBox<>(roomItems);
            quartoComboBox.setSelectedItem(roomItems.get(reserva.quarto().intValue() - 1));
            quartoPanel.add(quartoLabel);
            quartoPanel.add(quartoComboBox);
            quartoPanel.add(Box.createHorizontalStrut(5));
            quartoPanel.add(categoriaLabel);
            quartoPanel.add(categoriaDescricaoLabel);
            infoPanel.add(quartoPanel);

            JPanel mainHorizontalPanel = new GridBagLayoutPanel();

            JPanel datePanel = new JPanel(new BorderLayout());
            JPanel checkinCheckoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            checkinCheckoutPanel.setOpaque(false);
            JLabel checkinLabel = createLabel("Checkin:", new Font("sansseriff", Font.PLAIN, 14), DARK_GRAY, null);
            Dimension fieldLabelDim = new Dimension(70, 25);
            checkinLabel.setPreferredSize(fieldLabelDim);
            JFormattedTextField checkinField = new JFormattedTextField();
            checkinField.setText(reserva.data_entrada().format(df));
            checkinField.setColumns(6);
            JLabel checkoutLabel = createLabel("Checkout:", new Font("sansseriff", Font.PLAIN, 14), DARK_GRAY, null);
            checkoutLabel.setPreferredSize(fieldLabelDim);
            JFormattedTextField checkoutField = new JFormattedTextField();
            checkoutField.setText(reserva.data_saida().format(df));
            checkoutField.setColumns(6);
            checkinCheckoutPanel.add(checkinLabel);
            checkinCheckoutPanel.add(checkinField);
            checkinCheckoutPanel.add(checkoutLabel);
            checkinCheckoutPanel.add(checkoutField);
            DatePicker datePickerRange = new DatePicker();
            datePickerRange.setReservasDoQuarto(reservasDoQuarto);

            datePickerRange.setDateSelectionAble(date -> {
                for (DatasReserva reservaDatas : reservasDoQuarto) {
                    if (!date.isBefore(reservaDatas.checkin()) && !date.isAfter(reservaDatas.checkout())) {
                        return true;
                    }
                }
                return true;
            });

            datePickerRange.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
            datePickerRange.setSelectedDateRange(reserva.data_entrada(), reserva.data_saida());
            datePickerRange.setPreferredSize(new Dimension(260, 250));
            datePickerRange.setAlignmentX(Component.CENTER_ALIGNMENT);
            datePickerRange.addDateSelectionListener(e -> {
                LocalDate[] selectedDates = datePickerRange.getSelectedDateRange();
                if (selectedDates != null && selectedDates[0] != null && selectedDates[1] != null) {
                    checkinField.setText(selectedDates[0].format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    checkoutField.setText(selectedDates[1].format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            });

            datePanel.add(checkinCheckoutPanel, BorderLayout.NORTH);
            datePanel.add(datePickerRange, BorderLayout.CENTER);

            JPanel timePanel = new JPanel(new BorderLayout());
            timePanel.setOpaque(false);
            JLabel horarioTitulo = createLabel("Horário previsto de chegada:", new Font("sansseriff", Font.PLAIN, 14), DARK_GRAY, null);
            horarioTitulo.setHorizontalAlignment(SwingConstants.CENTER);
            TimePicker timePicker = new TimePicker();
            timePicker.set24HourView(true);
            timePicker.setSelectedTime(reserva.hora_prevista());
            JPanel timePickerContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            timePickerContainer.add(timePicker);
            timePickerContainer.setOpaque(false);
            timePanel.add(horarioTitulo, BorderLayout.NORTH);
            timePanel.add(timePickerContainer, BorderLayout.CENTER);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 20, 0, 20);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.gridx = 0;
            gbc.gridy = 0;
            mainHorizontalPanel.add(datePanel, gbc);
            gbc.gridx = 1;
            mainHorizontalPanel.add(timePanel, gbc);

            infoPanel.add(Box.createVerticalStrut(15));
            infoPanel.add(mainHorizontalPanel);
            infoPanel.add(Box.createVerticalStrut(15));

            Runnable updateDiariasInfo = () -> {
                try {
                    LocalDate checkinDate = LocalDate.parse(checkinField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalDate checkoutDate = LocalDate.parse(checkoutField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    int numDiarias = Period.between(checkinDate, checkoutDate).getDays();
                    int qtdPessoas = pessoasSelecionadas.size();
                    String selectedRoom = (String) quartoComboBox.getSelectedItem();
                    Long roomId = Long.parseLong(selectedRoom.split(" - ")[0]);
                    Float valorDiariaObj = quartosRepository.getValorCategoria(roomId, qtdPessoas);
                    double valorDiaria = (valorDiariaObj != null) ? valorDiariaObj : 0;
                    double total = numDiarias * valorDiaria;
                    animateLabelSpin(labelPessoasValue, parseLabelValue(labelPessoasValue.getText()), qtdPessoas, false);
                    animateLabelSpin(labelDiariasValue, parseLabelValue(labelDiariasValue.getText()), numDiarias, false);
                    animateLabelSpin(labelValorDiariaValue, parseLabelValue(labelValorDiariaValue.getText()), valorDiaria, true);
                    animateLabelSpin(labelTotalValue, parseLabelValue(labelTotalValue.getText()), total, true);
                } catch (Exception ex) {
                    labelPessoasValue.setText(String.valueOf(pessoasSelecionadas.size()));
                    labelDiariasValue.setText("...");
                    labelValorDiariaValue.setText("...");
                    labelTotalValue.setText("...");
                }
            };

            quartoComboBox.addActionListener(e -> {
                String selectedRoom = (String) quartoComboBox.getSelectedItem();
                Long roomId = Long.parseLong(Objects.requireNonNull(selectedRoom).split(" - ")[0]);
                categoriaDescricaoLabel.setText(quartosRepository.buscaQuartoPorId(roomId)
                        .categoria().categoria().toUpperCase());

                List<DatasReserva> reservasAtualizadas = reservasRepository.datasReservadasPorQuarto(roomId, reserva.reserva_id());
                datePickerRange.setReservasDoQuarto(reservasAtualizadas);
                datePickerRange.repaint();
                updateDiariasInfo.run();
            });


            checkinField.getDocument().addDocumentListener(new SimpleDocumentListener() {
                public void update() { updateDiariasInfo.run(); }
            });
            checkoutField.getDocument().addDocumentListener(new SimpleDocumentListener() {
                public void update() { updateDiariasInfo.run(); }
            });
            updateDiariasInfo.run();

            tabbedPane.addTab("Informações", infoPanel);
            JPanel pessoasTab = new JPanel();
            pessoasTab.setLayout(new BoxLayout(pessoasTab, BoxLayout.Y_AXIS));
            pessoasTab.setOpaque(false);
            tabbedPane.addTab("Pessoas", pessoasTab);
            JPanel pagamentosTab = new JPanel();
            pagamentosTab.setLayout(new BoxLayout(pagamentosTab, BoxLayout.Y_AXIS));
            pagamentosTab.setOpaque(false);
            tabbedPane.addTab("Pagamentos", pagamentosTab);

            popupContainer.add(tabbedPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton salvarButton = btn_verde("Salvar");
            JButton cancelarButton = btn_vermelho("Cancelar");
            buttonPanel.add(salvarButton);
            buttonPanel.add(cancelarButton);
            popupContainer.add(buttonPanel, BorderLayout.SOUTH);

            popupContainer.setPreferredSize(new Dimension(800, 600));
            popupContainer.setSize(new Dimension(800, 600));

            SwingUtilities.invokeLater(() -> {
                popupContainer.revalidate();
                popupContainer.repaint();
            });
        });
    }

    private void animateLabelSpin(JLabel label, double oldValue, double newValue, boolean isMoney) {
        if (oldValue == newValue) return;
        final boolean goingUp = (newValue > oldValue);
        final int totalSteps = 25;
        final double step = (newValue - oldValue) / totalSteps;
        final int delayMs = 30;
        final double[] currentValue = new double[]{oldValue};
        Timer timer = new Timer(delayMs, null);
        timer.addActionListener(e -> {
            currentValue[0] += step;
            boolean acabou = goingUp ? (currentValue[0] >= newValue) : (currentValue[0] <= newValue);
            if (acabou) {
                currentValue[0] = newValue;
                timer.stop();
            }
            if (isMoney) {
                label.setText(String.format("R$ %.2f", currentValue[0]));
            } else {
                label.setText(String.format("%.0f", currentValue[0]));
            }
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

    private static class GridBagLayoutPanel extends JPanel {
        public GridBagLayoutPanel() {
            super(new GridBagLayout());
            setOpaque(false);
        }
    }

    private void salvarReservaEditada(){

    }







}
