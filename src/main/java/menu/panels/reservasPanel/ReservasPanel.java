package menu.panels.reservasPanel;

import buttons.Botoes;
import buttons.ShadowButton;
import repository.QuartosRepository;
import repository.ReservasRepository;
import request.BuscaReservasResponse;
import response.QuartoResponse;
import tools.CorPersonalizada;
import tools.Refreshable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

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

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton btnPrev = btn_azul(" < ");
        JButton btnNext = btn_azul(" > ");

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

        daysHeader = new JPanel(new GridLayout(1, daysInMonth, 0, 0));
        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate tmpDate = currentMonth.withDayOfMonth(d);
            String dayStr = String.format("%02d/%02d", d, currentMonth.getMonthValue());
            String dayOfWeek = tmpDate.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("pt", "BR"));
            String labelText = "<html><center>" + dayStr + "<br>" + dayOfWeek + "</center></html>";

            CalendarLabel dayLabel = new CalendarLabel(labelText, d);
            dayLabel.setFont(new Font("Inter", Font.PLAIN, 14));
            dayLabel.setForeground(WHITE);
            dayLabel.setBackground(tmpDate.isEqual(now()) ? GRAY : BLUE);
            daysHeader.add(dayLabel);
        }

        roomsPanel = new JPanel(new GridLayout(numRooms, 1, 0, 0));
        roomsPanel.setBackground(BACKGROUND_GRAY);
        Dimension roomCellSize = new Dimension(60, cellSize.height);

        for (int row = 0; row < numRooms; row++) {
            QuartoResponse quarto = quartos.get(row);
            Long roomId = quarto.quarto_id();
            JLabel roomLabel = new JLabel(roomId < 10 ? "0" + roomId : roomId.toString(),
                    SwingConstants.CENTER);
            roomLabel.setFont(new Font("Inter", Font.BOLD, 14));
            roomLabel.setForeground(BLUE);
            roomLabel.setPreferredSize(roomCellSize);
            roomLabel.setBorder(defaultCellBorder);
            roomsPanel.add(roomLabel);
        }

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        int totalWidth = daysInMonth * cellSize.width;
        int totalHeight = numRooms * cellSize.height;
        layeredPane.setPreferredSize(new Dimension(totalWidth, totalHeight));

        backgroundPanel = new JPanel(new GridLayout(numRooms, daysInMonth, 0, 0));
        backgroundPanel.setBounds(0, 0, totalWidth, totalHeight);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        for (int row = 0; row < numRooms; row++) {
            QuartoResponse quarto = quartos.get(row);
            Long roomId = quarto.quarto_id();

            for (int d = 1; d <= daysInMonth; d++) {
                LocalDate date = currentMonth.withDayOfMonth(d);
                CalendarCell cell = new CalendarCell(roomId, date, row, d);

                if (date.isEqual(now())) {
                    cell.setBackground(LIGHT_GRAY_2);
                } else {
                    cell.setBackground(WHITE);
                }

                BuscaReservasResponse reserva = findReservationForDate(roomId, date);

                cell.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if (checkInDateMap.containsKey(cell.roomId)) {
                            updateSelectionForRoom(cell.roomId, cell.date);
                        }
                    }
                });

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (reserva == null) {
                            cell.setBackground(BACKGROUND_GRAY);
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (reserva == null) {
                            boolean isSelected = isSelectedRange(cell.roomId, cell.date);
                            if (isSelected) {
                                cell.setBackground(selectedColor);
                            } else if (cell.date.isEqual(now())) {
                                cell.setBackground(LIGHT_GRAY_2);
                            } else {
                                cell.setBackground(WHITE);
                            }
                        }
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
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
                                notification(menu, Type.ERROR, TOP_CENTER,
                                        "Período já reservado para este quarto.");
                                checkInDateMap.remove(cell.roomId);
                            } else {
                                showReservationFrame(cell.roomId, checkIn, checkOut);
                            }
                            revalidate();
                            repaint();
                        }
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
            LocalDate reservationStartDate = reserva.data_entrada();
            LocalDate reservationEndDate   = reserva.data_saida();

            if (reservationEndDate.isBefore(monthStart) || reservationStartDate.isAfter(monthEnd)) continue;
            int rowIndex = encontrarIndiceDoQuarto(quartos, reserva.quarto());
            if (rowIndex < 0) continue;

            LocalDate clampedStartDate = reservationStartDate.isBefore(monthStart)
                    ? monthStart
                    : reservationStartDate;
            LocalDate clampedEndDate   = reservationEndDate.isAfter(monthEnd)
                    ? monthEnd
                    : reservationEndDate;

            int startDayIndex = clampedStartDate.getDayOfMonth() - 1;
            int endDayIndex   = clampedEndDate.getDayOfMonth() - 1;

            int checkInX  = startDayIndex * cellSize.width + (cellSize.width / 2);
            int checkOutX = endDayIndex   * cellSize.width + (cellSize.width / 2);

            int reservationWidth = checkOutX - checkInX;

            if (endDayIndex == startDayIndex) {
                reservationWidth = cellSize.width / 2;
                checkOutX = checkInX + reservationWidth;
            }

            int roomY             = rowIndex * cellSize.height;
            int reservationHeight = cellSize.height;

            Color reservationColor = reservationColors[random.nextInt(reservationColors.length)];
            ShadowButton faixa = Botoes.btn_verde("");
            faixa.setBackground(reservationColor);
            faixa.setHoverEffect(true);
            faixa.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
            faixa.setLayout(new BoxLayout(faixa, BoxLayout.X_AXIS));

            faixa.setBounds(
                    checkInX,
                    roomY + 6,
                    reservationWidth,
                    reservationHeight - 6
            );

            ShadowButton qtdPessoa = btn_branco(" " + reserva.pessoas().size() + " ");
            String nome = reserva.pessoas().isEmpty() ? "RESERVADO" : reserva.pessoas().get(0).nome();
            JLabel labelNome = new JLabel(truncateText(nome, qtdPessoa, faixa.getWidth() - 90));
            labelNome.setFont(new Font("Inter", Font.PLAIN, 13));
            labelNome.setForeground(WHITE);

            qtdPessoa.setAlignmentY(Component.CENTER_ALIGNMENT);
            labelNome.setAlignmentY(0.65f);

            faixa.add(qtdPessoa);
            faixa.add(Box.createHorizontalStrut(5));
            faixa.add(labelNome);

            overlayPanel.add(faixa);
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(daysHeader, BorderLayout.NORTH);
        mainPanel.add(layeredPane, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(
                mainPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setRowHeaderView(roomsPanel);
        scrollPane.setColumnHeaderView(daysHeader);
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

    private int encontrarIndiceDoQuarto(List<QuartoResponse> quartos, Long quartoId) {
        for (int i = 0; i < quartos.size(); i++) {
            if (quartos.get(i).quarto_id().equals(quartoId)) {
                return i;
            }
        }
        return -1;
    }

    private BuscaReservasResponse findReservationForDate(Long roomId, LocalDate date) {
        for (BuscaReservasResponse reserva : currentReservations) {
            if (reserva.quarto().equals(roomId)) {
                LocalDate start = reserva.data_entrada();
                LocalDate end = reserva.data_saida();
                if (!date.isBefore(start) && !date.isAfter(end)) {
                    return reserva;
                }
            }
        }
        return null;
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
    }

    private void updateSelectionForRoom(Long roomId, LocalDate hoveredDate) {
        LocalDate checkIn = checkInDateMap.get(roomId);
        if (checkIn == null) return;

        LocalDate start = checkIn.isBefore(hoveredDate) ? checkIn : hoveredDate;
        LocalDate end = checkIn.isAfter(hoveredDate) ? checkIn : hoveredDate;

        for (Component comp : backgroundPanel.getComponents()) {
            if (comp instanceof CalendarCell cell) {
                if (cell.roomId.equals(roomId)) {
                    if (!cell.date.isBefore(start) && !cell.date.isAfter(end)) {
                        cell.setBackground(selectedColor);
                    } else {
                        if (cell.date.isEqual(now())) {
                            cell.setBackground(LIGHT_GRAY_2);
                        } else {
                            cell.setBackground(WHITE);
                        }
                    }
                }
            }
        }
    }

    private boolean isSelectedRange(Long roomId, LocalDate date) {
        LocalDate checkIn = checkInDateMap.get(roomId);
        if (checkIn == null) return false;
        return date.equals(checkIn);
    }

    public void popUp(ShadowButton shadowButton){
        ShadowButton buttonPanel = new ShadowButton();
        buttonPanel.setBackground(GRAY);
        buttonPanel.setPreferredSize(new Dimension(300, 200));

        shadowButton.showPopupWithButtons(buttonPanel);
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
                            !field.getName().equals("BACKGROUND_GRAY")) {
                        colors.add(color);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return colors.toArray(new Color[0]);
    }


}
