package menu.panels.reservasPanel;

import buttons.ShadowButton;
import repository.QuartosRepository;
import repository.ReservasRepository;
import request.BuscaReservasResponse;
import response.DatasReserva;
import response.QuartoResponse;
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
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static buttons.Botoes.btn_branco;
import static buttons.Botoes.btn_cinza;
import static tools.CorPersonalizada.*;

public class RoomPanel {
    private final ReservasPanel mainPanel;
    private final QuartosRepository quartosRepository;
    private final Dimension cellSize = new Dimension(300, 60);
    private final Border defaultCellBorder = BorderFactory.createLineBorder(BACKGROUND_GRAY);
    private final Color selectedColor = GREEN;
    private JPanel daysHeader;
    private JPanel roomsPanel;
    private JPanel backgroundPanel;

    public RoomPanel(ReservasPanel mainPanel, QuartosRepository quartosRepository) {
        this.mainPanel = mainPanel;
        this.quartosRepository = quartosRepository;
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

        CalendarLabel(String text, int col) {
            super(text, SwingConstants.CENTER);
            this.col = col;
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

    public JPanel createDaysHeaderPanel(int daysToShow, int startDay, LocalDate currentMonth) {
        JPanel daysHeader = new JPanel(new GridLayout(1, daysToShow, 0, 0));

        for (int d = startDay; d <= currentMonth.lengthOfMonth(); d++) {
            LocalDate tmpDate = currentMonth.withDayOfMonth(d);
            String dayStr = String.format("%02d/%02d", d, currentMonth.getMonthValue());
            String dayOfWeek = tmpDate.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("pt", "BR"));

            ReservasRepository.OcupacaoDia ocupacao = ReservasRepository.buscarOcupacaoPorDia(tmpDate);

            String labelText = String.format(
                    "<html>" +
                            "<div style='width: 100%%; padding: 0 5px;'>" +
                            "<table width='100%%'>" +
                            "<tr>" +
                            "<td align='left' style='font-size: 14px;'>%s<br>%s</td>" +
                            "<td align='right' style='font-size: 14px;'>%d%%<br>%d/%d</td>" +
                            "</tr>" +
                            "</table>" +
                            "</div>" +
                            "</html>",
                    dayStr, dayOfWeek, ocupacao.percentual(), ocupacao.ocupados(), ocupacao.total()
            );


            CalendarLabel dayLabel = new CalendarLabel(labelText, d);
            dayLabel.setFont(new Font("Roboto", Font.PLAIN, 13));
            dayLabel.setForeground(tmpDate.isEqual(LocalDate.now()) ? DARK_GRAY : WHITE);
            dayLabel.setBackground(tmpDate.isEqual(LocalDate.now()) ? new Color(0xEBEBEB) : BLUE.brighter());
            daysHeader.add(dayLabel);
        }

        daysHeader.setPreferredSize(
                new Dimension(daysToShow * cellSize.width, cellSize.height)
        );

        this.daysHeader = daysHeader;
        return daysHeader;
    }

    public JPanel createRoomsPanel(List<QuartoResponse> quartos, int numRooms) {
        JPanel roomsPanel = new JPanel(new GridLayout(numRooms, 1, 0, 0));
        roomsPanel.setBackground(BACKGROUND_GRAY);
        Dimension roomCellSize = new Dimension(60, cellSize.height);

        for (QuartoResponse quarto : quartos) {
            Long roomId = quarto.quarto_id();
            JLabel roomLabel = createLabel(roomId < 10 ? "0" + roomId : roomId.toString(),
                    new Font("Roboto", Font.BOLD, 14), BLUE, BACKGROUND_GRAY);
            roomLabel.setPreferredSize(roomCellSize);
            roomLabel.setBorder(defaultCellBorder);
            roomsPanel.add(roomLabel);
        }

        this.roomsPanel = roomsPanel;
        return roomsPanel;
    }

    public JLayeredPane createLayeredPane(List<QuartoResponse> quartos, int daysToShow, int startDay, int numRooms) {
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
                int dayOfMonth = startDay + colIndex;
                LocalDate date = mainPanel.getCurrentMonth().withDayOfMonth(dayOfMonth);
                CalendarCell cell = new CalendarCell(roomId, date, row, colIndex);
                cell.setBackground(date.isEqual(LocalDate.now()) ?  new Color(0xF8F8FA) : WHITE);

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
                        if (reserva == null)
                            cell.setBackground(BACKGROUND_GRAY);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        boolean isSelected = isSelectedRange(cell.roomId, cell.date);
                        cell.setBackground(isSelected ? selectedColor : (cell.date.isEqual(LocalDate.now()) ? new Color(0xF8F8FA) : WHITE));
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
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

            int checkInX = startDayIndex * cellSize.width + (cellSize.width / 2);
            int checkOutX = endDayIndex * cellSize.width + (cellSize.width / 2);
            int reservationWidth = (endDayIndex == startDayIndex) ? cellSize.width / 2 : checkOutX - checkInX;
            int roomY = rowIndex * cellSize.height;

            ShadowButton faixa = btn_cinza("");
            faixa.setBackground(randomCorClara());
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

            JLabel labelNome = new JLabel(TruncateText.truncateText(nome, qtdPessoa, faixa.getWidth() - 90));
            labelNome.setFont(new Font("Roboto", Font.PLAIN, 14));
            qtdPessoa.setAlignmentY(Component.CENTER_ALIGNMENT);
            labelNome.setAlignmentY(0.65f);

            faixa.add(qtdPessoa);
            faixa.add(Box.createHorizontalStrut(5));
            faixa.add(labelNome);

            faixa.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mainPanel.popUp(faixa, reserva);
                }
            });

            overlayPanel.add(faixa);
        }

        return layeredPane;
    }

    public JScrollPane createScrollPane(JLayeredPane layeredPane, JPanel roomsPanel, JPanel daysHeader) {
        JScrollPane scrollPane = new JScrollPane(layeredPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setRowHeaderView(roomsPanel);
        scrollPane.setColumnHeaderView(daysHeader);

        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
        return scrollPane;
    }

    private BuscaReservasResponse findReservationForDate(Long roomId, LocalDate date) {
        return mainPanel.getCurrentReservations().stream()
                .filter(reserva -> reserva.quarto().equals(roomId))
                .filter(reserva -> !date.isBefore(reserva.data_entrada()) && !date.isAfter(reserva.data_saida()))
                .findFirst().orElse(null);
    }

    private void updateSelectionForRoom(Long roomId, LocalDate hoveredDate) {
        LocalDate checkIn = mainPanel.getCheckInDateMap().get(roomId);
        if (checkIn == null) return;

        LocalDate start = checkIn.isBefore(hoveredDate) ? checkIn : hoveredDate;
        LocalDate end = checkIn.isAfter(hoveredDate) ? checkIn : hoveredDate;

        for (Component comp : mainPanel.getBackgroundPanel().getComponents()) {
            if (comp instanceof CalendarCell cell && cell.roomId.equals(roomId)) {
                cell.setBackground(!cell.date.isBefore(start) && !cell.date.isAfter(end)
                        ? selectedColor
                        : (cell.date.isEqual(LocalDate.now()) ? LIGHT_GRAY_2 : WHITE));
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
                cell.setBackground(cell.date.isEqual(LocalDate.now()) ? LIGHT_GRAY_2 : WHITE);
                cell.repaint();
            }
        }
    }

    public JLabel createLabel(String text, Font font, Color foreground, Color background) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(foreground);
        if (background != null) {
            label.setBackground(background);
            label.setOpaque(true);
        }
        return label;
    }

    public JButton createButton(String text, Color background, Color foreground, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.addActionListener(listener);
        return button;
    }

    private int encontrarIndiceDoQuarto(List<QuartoResponse> quartos, Long quartoId) {
        for (int i = 0; i < quartos.size(); i++) {
            if (quartos.get(i).quarto_id().equals(quartoId))
                return i;
        }
        return -1;
    }

    public String getMonthName(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).toUpperCase();
    }

    public static int randomNumber() {
        Random random = new Random();
        return random.nextInt(255) + 1;
    }

    public static Color corMuitoClara() {
        Random random = new Random();
        int r = random.nextInt(16) + 240;
        int g = random.nextInt(16) + 240;
        int b = random.nextInt(16) + 240;
        return new Color(r, g, b);
    }

    public static int randomNumberClaro() {
        Random random = new Random();
        return random.nextInt(90) + 160; // Sorteiando de 180 até 255
    }

    public static Color randomCorClara() {
        return new Color(randomNumberClaro(), randomNumberClaro(), randomNumberClaro());
    }
}