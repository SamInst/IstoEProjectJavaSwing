package menu.panels.reservasPanel;

import buttons.ShadowButton;
import repository.ReservasRepository;
import request.BuscaReservasResponse;
import response.DatasReserva;
import response.QuartoResponse;
import tools.PanelArredondado;
import tools.TruncateText;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static buttons.Botoes.*;
import static tools.CorPersonalizada.*;

public class CalendarioPanel {
    private final ReservasPanel mainPanel;
    private final Dimension cellSize = new Dimension(300, 60);
    private final Border defaultCellBorder = BorderFactory.createLineBorder(BACKGROUND_GRAY);
    private final Color selectedColor = new Color(0x5E9984);
    private JPanel daysHeader;
    private JPanel roomsPanel;
    private JPanel backgroundPanel;
    private JPanel occupancyPanel;
    private JLabel currentDateLabel;
    private JLabel occupancyPercentLabel;
    private JLabel occupancyCountReservadosLabel;
    private JLabel occupancyCountOcupadosLabel;
    private JLabel peopleHospedadasLabel;

    public CalendarioPanel(ReservasPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public class CalendarCell extends JPanel {
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
            setMinimumSize(cellSize);
            setMaximumSize(cellSize);
            setBorder(BorderFactory.createCompoundBorder(
                    defaultCellBorder,
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
            ));
            setLayout(new BorderLayout());
        }
    }

    private class CalendarLabel extends JLabel {
        int col;
        LocalDate date;

        CalendarLabel(String text, int col, LocalDate date) {
            super(text, SwingConstants.CENTER);
            this.col = col;
            this.date = date;
            setBorder(BorderFactory.createCompoundBorder(
                    defaultCellBorder,
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
            ));
            setPreferredSize(cellSize);
            setMinimumSize(cellSize);
            setMaximumSize(cellSize);
            setOpaque(true);
        }
    }

    public JPanel createOccupancyPanel() {
        PanelArredondado panel = new PanelArredondado();
        panel.setArredondamento(10);
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(0x5E9984));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setPreferredSize(new Dimension(0, 80));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleRow.setOpaque(false);
        currentDateLabel = createLabel("Ocupação para: Hoje", new Font("Roboto", Font.BOLD, 18), WHITE, null);
        titleRow.add(currentDateLabel);

        JPanel dataRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dataRow.setOpaque(false);

        dataRow.add(Box.createHorizontalStrut(30));
        occupancyCountReservadosLabel = createLabel("Quartos: 0/0(0%)", new Font("Roboto", Font.PLAIN, 16), WHITE, null);
        dataRow.add(occupancyCountReservadosLabel);

        dataRow.add(Box.createHorizontalStrut(30));
        occupancyCountOcupadosLabel = createLabel("Ocupados: 0/0(0%)", new Font("Roboto", Font.PLAIN, 16), WHITE, null);
        dataRow.add(occupancyCountOcupadosLabel);

        dataRow.add(Box.createHorizontalStrut(30));
        peopleHospedadasLabel = createLabel("Hospedados: 0/0(0%)", new Font("Roboto", Font.PLAIN, 16), WHITE, null);
        dataRow.add(peopleHospedadasLabel);

        contentPanel.add(titleRow);
        contentPanel.add(dataRow);
        panel.add(contentPanel, BorderLayout.CENTER);

        LocalDate today = LocalDate.now();
        updateOccupancyPanel(today);

        this.occupancyPanel = panel;
        return panel;
    }

    public void updateOccupancyPanel(LocalDate date) {
        ReservasRepository.OcupacaoDia ocupacao = ReservasRepository.buscarOcupacaoPorDia(date);
        Integer reservasHospedadas = ReservasRepository.buscarReservasHospedadasPorDia(date);

        String dayStr = String.format("%02d/%02d/%d",
                date.getDayOfMonth(), date.getMonthValue(), date.getYear());

        String dayOfWeek = date.getDayOfWeek()
                .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("pt", "BR"));

        currentDateLabel.setText(dayStr + " (" + dayOfWeek + ")");

        occupancyCountReservadosLabel.setText(
                "Quartos reservados: " +
                ocupacao.ocupados() + "/" + ocupacao.total()
        );

        occupancyCountOcupadosLabel.setText(
                "Quartos ocupados: " +
                        reservasHospedadas + "/" + ocupacao.ocupados()
        );

        int hospedadas = ReservasRepository.contarPessoasHospedadasPorData(date);
        int totalAtivas = ReservasRepository.contarPessoasReservasAtivasPorData(date);
        int percPessoas = totalAtivas > 0 ? hospedadas * 100 / totalAtivas : 0;

        peopleHospedadasLabel.setText(
                "Quantidade de pessoas hospedadas: " +
                hospedadas + "/" + totalAtivas
        );
    }

    public JPanel createDaysHeaderPanel(int daysToShow, int startDay, LocalDate currentMonth) {
        JPanel daysHeader = new JPanel();
        daysHeader.setLayout(new BoxLayout(daysHeader, BoxLayout.X_AXIS));

        LocalDate today = LocalDate.now();
        Color todayBg = new Color(0xE3F2FD).darker();

        for (int col = 0; col < daysToShow; col++) {
            LocalDate date = currentMonth.withDayOfMonth(startDay + col);

            ShadowButton cellPanel = new ShadowButton();
            cellPanel.enableHoverEffect();
            cellPanel.setLayout(new BorderLayout());
            cellPanel.setPreferredSize(cellSize);
            cellPanel.setMaximumSize(cellSize);

            JLabel dayNumber = new JLabel(String.valueOf(date.getDayOfMonth()));
            dayNumber.setFont(new Font("Roboto", Font.BOLD, 35));
            dayNumber.setForeground(new Color(0x444444));
            dayNumber.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            cellPanel.add(dayNumber, BorderLayout.WEST);

            JPanel rightPanel = new JPanel();
            rightPanel.setOpaque(false);
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 10));

            JLabel weekdayLabel = new JLabel(
                    date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"))
            );
            weekdayLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
            weekdayLabel.setForeground(new Color(0x888888));
            weekdayLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

            rightPanel.add(weekdayLabel);
            cellPanel.add(rightPanel, BorderLayout.EAST);

            cellPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    updateOccupancyPanel(date);
                }
            });

            if (date.isEqual(today)) {
                cellPanel.setBackground(todayBg);
                cellPanel.setShadowColor(todayBg);
                dayNumber.setForeground(WHITE);
                weekdayLabel.setForeground(WHITE);
            }

            daysHeader.add(cellPanel);
        }

        daysHeader.setPreferredSize(
                new Dimension(daysToShow * cellSize.width, cellSize.height)
        );
        return daysHeader;
    }

    public JPanel createRoomsPanel(List<QuartoResponse> quartos, int numRooms) {
        PanelArredondado roomsPanel = new PanelArredondado();
        roomsPanel.setArredondamento(15);
        roomsPanel.setLayout(new GridLayout(numRooms, 1, 0, 0));
        roomsPanel.setBackground(BACKGROUND_GRAY);
        Dimension roomCellSize = new Dimension(60, cellSize.height);

        List<JLabel> roomLabels = new ArrayList<>();

        for (QuartoResponse quarto : quartos) {
            Long roomId = quarto.quarto_id();
            JLabel roomLabel = createLabel(
                    roomId < 10 ? "0" + roomId : roomId.toString(),
                    new Font("Roboto", Font.BOLD, 16), WHITE, new Color(0x5E9984)
            );
            roomLabel.setPreferredSize(roomCellSize);
            roomLabel.setBorder(defaultCellBorder);
            roomsPanel.add(roomLabel);
            roomLabels.add(roomLabel);
        }

        mainPanel.setRoomLabels(roomLabels);
        this.roomsPanel = roomsPanel;
        return roomsPanel;
    }

    public JLayeredPane createLayeredPane(List<QuartoResponse> quartos,
                                          int daysToShow, int startDay, int numRooms) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        int totalWidth = daysToShow * cellSize.width;
        int totalHeight = numRooms * cellSize.height;
        layeredPane.setPreferredSize(new Dimension(totalWidth + 2, totalHeight));

        backgroundPanel = new JPanel(new GridLayout(numRooms, daysToShow, 0, 0));
        backgroundPanel.setBounds(0, 0, totalWidth, totalHeight);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);
        mainPanel.setBackgroundPanel(backgroundPanel);

        for (int row = 0; row < numRooms; row++) {
            Long roomId = quartos.get(row).quarto_id();
            for (int colIndex = 0; colIndex < daysToShow; colIndex++) {
                LocalDate date = mainPanel.getCurrentMonth().withDayOfMonth(startDay + colIndex);
                CalendarCell cell = new CalendarCell(roomId, date, row, colIndex);
                cell.setBackground(WHITE);

                BuscaReservasResponse reserva = findReservationForDate(roomId, date);

                cell.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if (mainPanel.getCheckInDateMap().containsKey(cell.roomId))
                            updateSelectionForRoom(cell.roomId, cell.date);
                    }
                });

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (reserva == null) {
                            cell.setBackground(new Color(0xE3F2FD).darker());

                            List<JLabel> roomLabels = mainPanel.getRoomLabels();
                            if (roomLabels != null && cell.row < roomLabels.size()) {
                                JLabel roomLabel = roomLabels.get(cell.row);

                                Color originalColor = roomLabel.getBackground();
                                roomLabel.putClientProperty("originalColor", originalColor);
                                roomLabel.setBackground(new Color(0xE3F2FD).darker());
                            }
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        boolean isSelected = isSelectedRange(cell.roomId, cell.date);
                        cell.setBackground(WHITE);

                        List<JLabel> roomLabels = mainPanel.getRoomLabels();
                        if (roomLabels != null && cell.row < roomLabels.size()) {
                            JLabel roomLabel = roomLabels.get(cell.row);
                            Color originalColor = (Color) roomLabel.getClientProperty("originalColor");
                            if (originalColor != null) {
                                roomLabel.setBackground(originalColor);
                            } else {
                                roomLabel.setBackground(new Color(0x5E9984));
                            }
                        }
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        updateOccupancyPanel(cell.date);
                        mainPanel.handleCellClick(cell);
                    }
                });

                backgroundPanel.add(cell);
            }
        }

        JPanel overlayPanel = new JPanel(null);
        overlayPanel.setOpaque(false);
        overlayPanel.setBounds(0, 0, totalWidth, totalHeight);
        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);

        LocalDate visibleStart = mainPanel.getCurrentMonth().withDayOfMonth(startDay);
        LocalDate monthEnd = mainPanel.getCurrentMonth().withDayOfMonth(mainPanel.getCurrentMonth().lengthOfMonth());

        for (BuscaReservasResponse reserva : mainPanel.getCurrentReservations()) {
            List<DatasReserva> reservasDoQuarto = mainPanel.getReservasRepository().datasReservadasPorQuarto(reserva.quarto(), reserva.reserva_id());
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

            boolean reservaAntesDoInicio = resStartDate.isBefore(visibleStart);
            boolean reservaDepoisDoFim = resEndDate.isAfter(monthEnd);

            int checkInX = startDayIndex * cellSize.width + (cellSize.width / 2);
            int checkOutX = endDayIndex * cellSize.width + (cellSize.width / 2);

            if (reservaAntesDoInicio) {
                checkInX = startDayIndex * cellSize.width;
            }

            if (reservaDepoisDoFim) {
                checkOutX = (endDayIndex + 1) * cellSize.width;
            }

            int reservationWidth = checkOutX - checkInX;
            int roomY = rowIndex * cellSize.height;

            ShadowButton faixa = btn_cinza("");
            faixa.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            if (reserva.hospedado()) faixa = btn_verde("");

            String tooltipPessoas = reserva.pessoas().stream()
                    .map(p -> p.nome())
                    .collect(Collectors.joining("\n"));

            faixa.setBackground(randomCorClara());
            faixa.setForeground(LIGHT_GRAY_2);
            faixa.enableHoverEffect();
            faixa.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
            faixa.setLayout(new BoxLayout(faixa, BoxLayout.X_AXIS));
            faixa.setBounds(checkInX, roomY + 6, reservationWidth, cellSize.height - 6);

            ShadowButton qtdPessoa = btn_branco(" " + reserva.pessoas().size() + " ");
            qtdPessoa.setToolTipText(tooltipPessoas);
//            ShadowButton pagamento_ok = btn_verde("$");
//            pagamento_ok.setToolTipText(reserva.pagamentos().size() + " Pagamentos Adicionado(s)");
//            ShadowButton hopedado = btn_azul("H");
//            hopedado.setToolTipText("Hospedado");

            String nome = reserva.pessoas().stream()
                    .filter(p -> p.representante())
                    .findFirst()
                    .map(p -> reserva.hospedado() ? "[HOSPEDADO] " + p.nome() : p.nome()).orElse("Reservado (sem pessoa definida)");

            JLabel labelNome = new JLabel(TruncateText.truncateText(nome, qtdPessoa, faixa.getWidth() - 90));
            labelNome.setFont(new Font("Roboto", Font.PLAIN, 14));
            qtdPessoa.setAlignmentY(Component.CENTER_ALIGNMENT);
            labelNome.setAlignmentY(0.65f);

            faixa.add(qtdPessoa);
//            if (reserva.hospedado()) faixa.add(hopedado);
//            if (reserva.pagamentos().size() > 0) faixa.add(pagamento_ok);
            faixa.add(Box.createHorizontalStrut(5));
            faixa.add(labelNome);

            ShadowButton finalFaixa = faixa;
            faixa.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mainPanel.popUp(finalFaixa, reserva);
                }
            });

            overlayPanel.add(faixa);
        }

        return layeredPane;
    }

    public JScrollPane createScrollPane(JLayeredPane layeredPane,
                                        JPanel roomsPanel, JPanel daysHeader) {
        JViewport headerViewport = new JViewport();
        headerViewport.setView(daysHeader);
        headerViewport.setPreferredSize(daysHeader.getPreferredSize());

        JScrollPane scrollPane = new JScrollPane(
                layeredPane,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setRowHeaderView(roomsPanel);
        scrollPane.setColumnHeader(headerViewport);
        scrollPane.getHorizontalScrollBar().addAdjustmentListener(e ->
                headerViewport.setViewPosition(new Point(e.getValue(), 0))
        );
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
        return scrollPane;
    }

    private BuscaReservasResponse findReservationForDate(Long roomId, LocalDate date) {
        return mainPanel.getCurrentReservations().stream()
                .filter(r -> r.quarto().equals(roomId))
                .filter(r -> !date.isBefore(r.data_entrada()) && !date.isAfter(r.data_saida()))
                .findFirst().orElse(null);
    }

    private void updateSelectionForRoom(Long roomId, LocalDate hoveredDate) {
        LocalDate checkIn = mainPanel.getCheckInDateMap().get(roomId);
        if (checkIn == null) return;
        LocalDate start = checkIn.isBefore(hoveredDate) ? checkIn : hoveredDate;
        LocalDate end   = checkIn.isAfter(hoveredDate)  ? checkIn : hoveredDate;
        for (Component comp : mainPanel.getBackgroundPanel().getComponents()) {
            if (comp instanceof CalendarCell cell && cell.roomId.equals(roomId)) {
                cell.setBackground(!cell.date.isBefore(start) && !cell.date.isAfter(end)
                        ? selectedColor
                        : new Color(0xFAFBFA));
            }
        }
    }

    private boolean isSelectedRange(Long roomId, LocalDate date) {
        LocalDate checkIn = mainPanel.getCheckInDateMap().get(roomId);
        return date.equals(checkIn);
    }

    public void resetCellsForRoom(Long roomId) {
        for (Component comp : mainPanel.getBackgroundPanel().getComponents()) {
            if (comp instanceof CalendarCell cell && cell.roomId.equals(roomId)) {
                cell.setBackground(WHITE);
                cell.repaint();
            }
        }
    }

    public JLabel createLabel(String text, Font font, Color fg, Color bg) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(font);
        lbl.setForeground(fg);
        if (bg != null) {
            lbl.setBackground(bg);
            lbl.setOpaque(true);
        }
        return lbl;
    }

    public JButton createButton(String text, Color bg, Color fg, ActionListener lst) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.addActionListener(lst);
        return btn;
    }

    private int encontrarIndiceDoQuarto(List<QuartoResponse> quartos, Long quartoId) {
        for (int i = 0; i < quartos.size(); i++) {
            if (quartos.get(i).quarto_id().equals(quartoId))
                return i;
        }
        return -1;
    }

    public String getMonthName(LocalDate date) {
        return date.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("pt", "BR"))
                .toUpperCase();
    }

    public static int randomNumber() {
        return new Random().nextInt(255) + 1;
    }

    public static int randomNumberClaro() {
        return new Random().nextInt(90) + 160;
    }

    public static Color randomCorClara() {
        return new Color(randomNumberClaro(), randomNumberClaro(), randomNumberClaro());
    }
}
