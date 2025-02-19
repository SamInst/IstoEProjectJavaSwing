package menu.panels.reservasPanel;

import repository.QuartosRepository;
import repository.ReservasRepository;
import request.BuscaReservasResponse;
import tools.Refreshable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static buttons.Botoes.btn_backgroung;
import static notifications.Notfication.notification;
import static notifications.Notifications.Location.TOP_CENTER;
import static notifications.Notifications.Type;
import static tools.CorPersonalizada.*;

public class ReservasPanel extends JPanel implements Refreshable {
    private final QuartosRepository quartosRepository = new QuartosRepository();
    private final ReservasRepository reservasRepository = new ReservasRepository();
    private final JFrame menu;

    private LocalDate currentMonth;
    private JPanel cellsPanel;    // Painel com as células dos dias (grid de datas)
    private JPanel daysHeader;    // Cabeçalho dos dias
    private JPanel roomsPanel;    // Painel fixo com os números dos quartos

    // Define tamanho fixo para cada célula de data (ex.: 100x50)
    private final Dimension cellSize = new Dimension(100, 50);

    private final Border defaultCellBorder = BorderFactory.createLineBorder(LIGHT_GRAY_2);
    private final Border highlightBorder = new CompoundBorder(new LineBorder(LIGHT_GRAY_2, 1), defaultCellBorder);
    private final Color selectedColor = GREEN;

    private final Map<Long, LocalDate> checkInDateMap = new HashMap<>();
    private final Map<Long, CalendarCell> checkInCellMap = new HashMap<>();

    private List<BuscaReservasResponse> currentReservations;

    public ReservasPanel(JFrame menu) {
        this.menu = menu;
        currentMonth = LocalDate.now();
        initializePanel();
    }

    // Cada célula representa um dia para um quarto (somente para a área de datas)
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
            setBorder(defaultCellBorder);
            setLayout(new BorderLayout());
            setPreferredSize(cellSize);
        }
    }

    // Rótulo para o cabeçalho dos dias
    private class CalendarLabel extends JLabel {
        int col;
        CalendarLabel(String text, int col) {
            super(text, SwingConstants.CENTER);
            this.col = col;
            setBorder(defaultCellBorder);
            setPreferredSize(cellSize);
        }
    }

    private void initializePanel() {
        removeAll();

        var quartos = quartosRepository.buscaTodosOsQuartos();
        currentReservations = reservasRepository.buscaReservasAtivas();

        setLayout(new BorderLayout());

        // Painel superior com navegação (mês/ano)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton btnPrev = btn_backgroung("<");
        JButton btnNext = btn_backgroung(">");
        String monthName = currentMonth.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("pt", "BR"))
                .toUpperCase();
        String monthYear = monthName + " " + currentMonth.getYear();
        JLabel monthLabel = new JLabel(monthYear, SwingConstants.CENTER);
        monthLabel.setFont(new Font("Inter", Font.BOLD, 16));
        headerPanel.add(btnPrev);
        headerPanel.add(monthLabel);
        headerPanel.add(btnNext);
        add(headerPanel, BorderLayout.NORTH);

        int daysInMonth = currentMonth.lengthOfMonth();
        int numRooms = quartos.size();

        // Cabeçalho dos dias (apenas as células de data)
        daysHeader = new JPanel(new GridLayout(1, daysInMonth, 1, 1));
        for (int d = 1; d <= daysInMonth; d++) {
            String dayStr = String.format("%02d/%02d", d, currentMonth.getMonthValue());
            CalendarLabel dayLabel = new CalendarLabel(dayStr, d);
            dayLabel.setFont(new Font("Inter", Font.PLAIN, 14));
            dayLabel.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    updateHighlight(-1, dayLabel.col);
                }
            });
            dayLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    clearHighlight();
                }
            });
            daysHeader.add(dayLabel);
        }

        // Painel fixo para os números dos quartos (row header)
        roomsPanel = new JPanel(new GridLayout(numRooms, 1, 1, 1));
        Dimension roomCellSize = new Dimension(60, cellSize.height);
        for (int row = 0; row < numRooms; row++) {
            var quarto = quartos.get(row);
            Long roomId = quarto.quarto_id();
            JLabel roomLabel = new JLabel(roomId < 10 ? "0" + roomId : roomId.toString(), SwingConstants.CENTER);
            roomLabel.setFont(new Font("Inter", Font.PLAIN, 14));
            roomLabel.setPreferredSize(roomCellSize);
            roomLabel.setBorder(defaultCellBorder);
            roomsPanel.add(roomLabel);
        }

        // Painel com as células dos dias (grid de datas para cada quarto)
        cellsPanel = new JPanel(new GridLayout(numRooms, daysInMonth, 1, 1));
        for (int row = 0; row < numRooms; row++) {
            var quarto = quartos.get(row);
            Long roomId = quarto.quarto_id();
            for (int d = 1; d <= daysInMonth; d++) {
                LocalDate date = LocalDate.of(currentMonth.getYear(), currentMonth.getMonthValue(), d);
                CalendarCell cell = new CalendarCell(roomId, date, row, d);
                Optional<String> reservaNome = getReservedNameForRoomOnDate(roomId, date, currentReservations);
                if (reservaNome.isPresent()) {
                    cell.setBackground(new Color(255, 102, 102));
                    JLabel nameLabel = new JLabel(reservaNome.get(), SwingConstants.CENTER);
                    nameLabel.setFont(new Font("Inter", Font.BOLD, 10));
                    cell.add(nameLabel, BorderLayout.CENTER);
                } else {
                    cell.setBackground(Color.WHITE);
                }
                cell.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if (checkInDateMap.containsKey(cell.roomId)) {
                            updateSelectionForRoom(cell.roomId, cell.date);
                        }
                        if (cell.col > 0) {
                            updateHighlight(cell.row, cell.col);
                        }
                    }
                });
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        clearHighlight();
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (reservaNome.isPresent()) return;
                        if (!checkInDateMap.containsKey(cell.roomId)) {
                            checkInDateMap.put(cell.roomId, cell.date);
                            checkInCellMap.put(cell.roomId, cell);
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
                                checkInCellMap.remove(cell.roomId);
                                refreshPanel();
                            } else {
                                showReservationFrame(cell.roomId, checkIn, checkOut);
                                refreshPanel();
                            }
                        }
                    }
                });
                cellsPanel.add(cell);
            }
        }

        // Junta o cabeçalho e o grid em um painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(daysHeader, BorderLayout.NORTH);
        mainPanel.add(cellsPanel, BorderLayout.CENTER);

        // Cria o JScrollPane, fixando o row header (números dos quartos) e o column header (dias)
        JScrollPane scrollPane = new JScrollPane(mainPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setRowHeaderView(roomsPanel);
        scrollPane.setColumnHeaderView(daysHeader);

        // Aumenta a velocidade do scroll
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(50);

        add(scrollPane, BorderLayout.CENTER);

        btnPrev.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshPanel();
        });
        btnNext.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshPanel();
        });

        revalidate();
        repaint();
    }

    private void updateHighlight(int hoveredRow, int hoveredCol) {
        clearHighlight();
        if (hoveredRow == -1) {
            // Se no cabeçalho, destaca somente o cabeçalho da coluna de data
            for (Component comp : daysHeader.getComponents()) {
                if (comp instanceof CalendarLabel cl && cl.col == hoveredCol) {
                    cl.setBorder(highlightBorder);
                }
            }
        } else {
            if (hoveredCol > 0) {
                // Destaca todas as células da coluna correspondente
                for (Component comp : cellsPanel.getComponents()) {
                    if (comp instanceof CalendarCell cc && cc.col == hoveredCol) {
                        cc.setBorder(highlightBorder);
                    }
                }
                // Destaca o cabeçalho correspondente
                for (Component comp : daysHeader.getComponents()) {
                    if (comp instanceof CalendarLabel cl && cl.col == hoveredCol) {
                        cl.setBorder(highlightBorder);
                    }
                }
            }
        }
    }

    private void clearHighlight() {
        for (Component comp : cellsPanel.getComponents()) {
            if (comp instanceof CalendarCell cell) {
                cell.setBorder(defaultCellBorder);
            }
        }
        for (Component comp : daysHeader.getComponents()) {
            if (comp instanceof CalendarLabel cl) {
                cl.setBorder(defaultCellBorder);
            }
        }
        for (Map.Entry<Long, CalendarCell> entry : checkInCellMap.entrySet()) {
            entry.getValue().setBorder(defaultCellBorder);
            entry.getValue().setBackground(selectedColor);
        }
    }

    private void updateSelectionForRoom(Long roomId, LocalDate hoveredDate) {
        LocalDate checkIn = checkInDateMap.get(roomId);
        if (checkIn == null) return;
        LocalDate start = checkIn.isBefore(hoveredDate) ? checkIn : hoveredDate;
        LocalDate end = checkIn.isAfter(hoveredDate) ? checkIn : hoveredDate;
        for (Component comp : cellsPanel.getComponents()) {
            if (comp instanceof CalendarCell cell) {
                if (cell.roomId.equals(roomId) && cell.date != null) {
                    Optional<String> reserva = getReservedNameForRoomOnDate(roomId, cell.date, currentReservations);
                    if (!cell.date.isBefore(start) && !cell.date.isAfter(end)) {
                        if (reserva.isEmpty()) {
                            cell.setBackground(selectedColor);
                        } else {
                            cell.setBackground(new Color(255, 102, 102));
                        }
                    } else {
                        if (reserva.isEmpty()) {
                            cell.setBackground(Color.WHITE);
                        } else {
                            cell.setBackground(new Color(255, 102, 102));
                        }
                    }
                }
            }
        }
    }

    private boolean isOverlappingExistingReservation(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        for (BuscaReservasResponse reserva : currentReservations) {
            if (reserva.quarto().equals(roomId)) {
                LocalDate resStart = reserva.data_entrada();
                LocalDate resEnd = reserva.data_saida();
                boolean overlaps = !checkOut.isBefore(resStart) && !checkIn.isAfter(resEnd);
                if (overlaps) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showReservationFrame(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        JFrame frame = new JFrame("Nova Reserva");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new GridLayout(3, 1));
        JLabel lblRoom = new JLabel("Quarto: " + roomId, SwingConstants.CENTER);
        JLabel lblCheckIn = new JLabel("Data de Entrada: " + checkIn, SwingConstants.CENTER);
        JLabel lblCheckOut = new JLabel("Data de Saída: " + checkOut, SwingConstants.CENTER);
        frame.add(lblRoom);
        frame.add(lblCheckIn);
        frame.add(lblCheckOut);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        checkInDateMap.remove(roomId);
        checkInCellMap.remove(roomId);
    }

    private Optional<String> getReservedNameForRoomOnDate(Long roomId, LocalDate date, List<BuscaReservasResponse> responses) {
        for (BuscaReservasResponse reserva : responses) {
            if (reserva.quarto().equals(roomId)
                    && !date.isBefore(reserva.data_entrada())
                    && !date.isAfter(reserva.data_saida())) {
                if (reserva.pessoas().isEmpty()) {
                    return Optional.of("RESERVADO");
                } else {
                    return Optional.of(reserva.pessoas().get(0).nome());
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void refreshPanel() {
        removeAll();
        initializePanel();
        revalidate();
        repaint();
    }
}
