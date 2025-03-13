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
    ShadowButton pernoiteButton = btn_azul("Pernoite");
    ShadowButton cancelarButton = btn_vermelho("Cancelar");
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
    private List<PessoaResponse> selectedPeople = new ArrayList<>();

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

    public ReservasPanel(JFrame menu) {
        this.menu = menu;
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
        int numRooms = quartos.size();
        daysHeader = createDaysHeaderPanel(daysInMonth);
        roomsPanel = createRoomsPanel(quartos, numRooms);
        JLayeredPane layeredPane = createLayeredPane(quartos, daysInMonth, numRooms);
        JScrollPane scrollPane = createScrollPane(layeredPane);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        String yearStr = String.valueOf(currentMonth.getYear());
        JLabel yearLabel = createLabel(yearStr, new Font("Inter", Font.BOLD, 16), WHITE, BLUE);
        yearLabel.setHorizontalAlignment(SwingConstants.LEFT);
        yearLabel.setPreferredSize(new Dimension(70, 30));
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));
        navPanel.setOpaque(false);
        navPanel.setPreferredSize(new Dimension(260, 30));
        JButton btnPrev = createButton(" < ", BLUE, WHITE, e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshPanel();
        });
        btnPrev.setPreferredSize(new Dimension(50, 30));
        String monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).toUpperCase();
        JLabel monthLabel = createLabel(monthName, new Font("Inter", Font.BOLD, 16), WHITE, BLUE);
        monthLabel.setPreferredSize(new Dimension(150, 30));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JButton btnNext = createButton(" > ", BLUE, WHITE, e -> {
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

    private JPanel createDaysHeaderPanel(int daysInMonth) {
        JPanel daysHeader = new JPanel(new GridLayout(1, daysInMonth, 0, 0));
        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate tmpDate = currentMonth.withDayOfMonth(d);
            String dayStr = String.format("%02d/%02d", d, currentMonth.getMonthValue());
            String dayOfWeek = tmpDate.getDayOfWeek().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("pt", "BR"));
            String labelText = "<html><center>" + dayStr + "<br>" + dayOfWeek + "</center></html>";
            CalendarLabel dayLabel = new CalendarLabel(labelText, d);
            dayLabel.setFont(new Font("Inter", Font.PLAIN, 14));
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
        for (QuartoResponse quarto : quartos) {
            Long roomId = quarto.quarto_id();
            JLabel roomLabel = createLabel(roomId < 10 ? "0" + roomId : roomId.toString(), new Font("Inter", Font.BOLD, 14), BLUE, BACKGROUND_GRAY);
            roomLabel.setPreferredSize(roomCellSize);
            roomLabel.setBorder(defaultCellBorder);
            roomsPanel.add(roomLabel);
        }
        return roomsPanel;
    }

    private JLayeredPane createLayeredPane(List<QuartoResponse> quartos, int daysInMonth, int numRooms) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        int totalWidth = daysInMonth * cellSize.width;
        int totalHeight = numRooms * cellSize.height;
        layeredPane.setPreferredSize(new Dimension(totalWidth, totalHeight));
        backgroundPanel = new JPanel(new GridLayout(numRooms, daysInMonth, 0, 0));
        backgroundPanel.setBounds(0, 0, totalWidth, totalHeight);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);
        for (int row = 0; row < numRooms; row++) {
            Long roomId = quartos.get(row).quarto_id();
            for (int d = 1; d <= daysInMonth; d++) {
                LocalDate date = currentMonth.withDayOfMonth(d);
                CalendarCell cell = new CalendarCell(roomId, date, row, d);
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
        LocalDate monthStart = currentMonth.withDayOfMonth(1);
        LocalDate monthEnd = currentMonth.withDayOfMonth(daysInMonth);
        for (BuscaReservasResponse reserva : currentReservations) {
            reservasDoQuarto = reservasRepository.datasReservadasPorQuarto(reserva.quarto(), reserva.reserva_id());
            LocalDate resStartDate = reserva.data_entrada();
            LocalDate resEndDate = reserva.data_saida();
            if (resEndDate.isBefore(monthStart) || resStartDate.isAfter(monthEnd))
                continue;
            int rowIndex = encontrarIndiceDoQuarto(quartos, reserva.quarto());
            if (rowIndex < 0) continue;
            LocalDate clampedStartDate = resStartDate.isBefore(monthStart) ? monthStart : resStartDate;
            LocalDate clampedEndDate = resEndDate.isAfter(monthEnd) ? monthEnd : resEndDate;
            int startDayIndex = clampedStartDate.getDayOfMonth() - 1;
            int endDayIndex = clampedEndDate.getDayOfMonth() - 1;
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
            String nome = reserva.pessoas().isEmpty() ? "RESERVADO" : reserva.pessoas().get(0).nome();
            JLabel labelNome = new JLabel(truncateText(nome, qtdPessoa, faixa.getWidth() - 90));
            labelNome.setFont(new Font("Inter", Font.PLAIN, 13));
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
        frame.add(createLabel("Quarto: " + roomId, new Font("Inter", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE));
        frame.add(createLabel("Data de Entrada: " + checkIn, new Font("Inter", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE));
        frame.add(createLabel("Data de Saída: " + checkOut, new Font("Inter", Font.PLAIN, 14), CorPersonalizada.BLACK, WHITE));
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
        popupContainer.setPreferredSize(new Dimension(650, 580));
        shadowButton.showPopupWithButtons(popupContainer);
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

    private void cancelarReserva(BuscaReservasResponse reserva) {
        for (ActionListener al : pernoiteButton.getActionListeners()) {
            pernoiteButton.removeActionListener(al);
        }
        cancelarButton.addActionListener(e -> {
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
            Message obj = new Message("Deseja cancelar a reserva?",
                    "ID #" + reserva.reserva_id() + " - " + quartoInfo + "\n" +
                            "Check-in: " + reserva.data_entrada() + "  |  Checkout: " + reserva.data_saida() + "\n" +
                            "\n👥 Pessoas:\n" + pessoasFormatadas +
                            "\n💳 Pagamentos:\n" + pagamentosFormatados,
                    "Cancelar", "Não", RED_2);
            obj.eventOK(ae -> {
                reservasRepository.desativarReserva(reserva.reserva_id());
                GlassPanePopup.closePopupLast();
                refreshPanel();
                notification(menu, Type.SUCCESS, TOP_CENTER,
                        "Reserva cancelada com sucesso!\n#" + reserva.reserva_id() + " - " + quartoInfo + "\n" +
                                reserva.pessoas().get(0).nome());
            });
            GlassPanePopup.showPopup(obj);
        });
    }

    private void novoPernoite(BuscaReservasResponse reserva) {
        for (ActionListener al : pernoiteButton.getActionListeners()) {
            pernoiteButton.removeActionListener(al);
        }
        pernoiteButton.addActionListener(e -> {
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
            Message obj = new Message("Deseja mover para pernoites?",
                    "ID #" + reserva.reserva_id() + " - " + quartoInfo + "\n" +
                            "Check-in: " + reserva.data_entrada() + "  |  Checkout: " + reserva.data_saida() + "\n" +
                            "\n👥 Pessoas:\n" + pessoasFormatadas +
                            "\n💳 Pagamentos:\n" + pagamentosFormatados,
                    "Novo Pernoite", "Cancelar", BLUE);
            obj.eventOK(ae -> {
                mudarReservaParaPernoite(reserva);
                GlassPanePopup.closePopupLast();
                refreshPanel();
                notification(menu, Type.SUCCESS, TOP_CENTER,
                        "Pernoite adicionado com sucesso!\n#" + reserva.reserva_id() + " - " + quartoInfo + "\n" +
                                reserva.pessoas().get(0).nome());
            });
            GlassPanePopup.showPopup(obj);
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
        GlassPanePopup.install(menu);
        FlatLaf.registerCustomDefaultsSource("themes");
        FlatMacLightLaf.setup();
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
        JLabel titleLabel = createLabel("Detalhes da reserva # " + reserva.reserva_id(), new Font("Inter", Font.BOLD, 17), DARK_GRAY, null);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        ShadowButton closeButton = Botoes.btn_backgroung("");
        closeButton.setIcon(Resize.resizeIcon(close, 15, 15));
        closeButton.enableHoverEffect();
        headerPanel.add(closeButton, BorderLayout.EAST);
        topPanel.add(headerPanel);
        JPanel infoGridPanel = createInfoGridPanel(reserva);
        topPanel.add(infoGridPanel);
        popupContainer.add(topPanel, BorderLayout.NORTH);
        MaterialTabbed tabbedPane = new MaterialTabbed();
        tabbedPane.setForeground(GRAY);
        JPanel infoPanel = createInfoPanel(reserva);
        tabbedPane.addTab("Informações", infoPanel);
        JPanel pessoasTab = new JPanel();
        pessoasTab.setLayout(new BoxLayout(pessoasTab, BoxLayout.Y_AXIS));
        pessoasTab.setOpaque(false);
        pessoasTab.add(Box.createVerticalStrut(10));
        createGoogleStyleBuscaPessoaPanel(pessoasTab, reserva);
        tabbedPane.addTab("Pessoas", pessoasTab);
        JPanel pagamentosTab = new JPanel();
        pagamentosTab.setLayout(new BoxLayout(pagamentosTab, BoxLayout.Y_AXIS));
        pagamentosTab.setOpaque(false);
        tabbedPane.addTab("Pagamentos", pagamentosTab);
        popupContainer.add(tabbedPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton salvarButton = btn_verde("Salvar");
        salvarReservaEditada(salvarButton, reserva);
        cancelarButton = btn_vermelho("Cancelar");
        cancelarButton.addActionListener(e -> {
            shadowButton.closeJDialog();
            popUp(shadowButton, reserva);
        });
        pernoiteButton = btn_azul("Pernoite");
        leftButtons.add(cancelarButton);
        rightButtons.add(salvarButton);
        rightButtons.add(pernoiteButton);
        buttonPanel.add(leftButtons, BorderLayout.WEST);
        buttonPanel.add(rightButtons, BorderLayout.EAST);
        cancelarReserva(reserva);
        novoPernoite(reserva);
        popupContainer.add(buttonPanel, BorderLayout.SOUTH);
        popupContainer.setPreferredSize(new Dimension(800, 600));
        SwingUtilities.invokeLater(() -> {
            popupContainer.revalidate();
            popupContainer.repaint();
        });
        closeButton.addActionListener(e -> shadowButton.closeJDialog());
    }

    private JPanel createInfoGridPanel(BuscaReservasResponse reserva) {
        JPanel infoGridPanel = new JPanel(new GridLayout(2, 2, 15, 5));
        infoGridPanel.setOpaque(false);
        infoGridPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        labelPessoasValue = createLabel(String.valueOf(reserva.pessoas().size()), new Font("Inter", Font.BOLD, 14), DARK_GRAY, null);
        labelValorDiariaValue = createLabel("R$ 0,00", new Font("Inter", Font.BOLD, 14), DARK_GRAY, null);
        labelDiariasValue = createLabel("0", new Font("Inter", Font.BOLD, 14), DARK_GRAY, null);
        labelTotalValue = createLabel("R$ 0,00", new Font("Inter", Font.BOLD, 14), GREEN, null);
        infoGridPanel.add(createFlowPanel("Pessoas:", labelPessoasValue));
        infoGridPanel.add(createFlowPanel("Valor diária:", labelValorDiariaValue));
        infoGridPanel.add(createFlowPanel("Diárias:", labelDiariasValue));
        infoGridPanel.add(createFlowPanel("Total:", labelTotalValue));
        return infoGridPanel;
    }

    private JPanel createFlowPanel(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        panel.add(createLabel(labelText, new Font("Inter", Font.PLAIN, 14), DARK_GRAY, null));
        panel.add(valueLabel);
        return panel;
    }

    private JPanel createInfoPanel(BuscaReservasResponse reserva) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JPanel quartoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quartoPanel.setOpaque(false);
        JLabel quartoLabel = createLabel("Quarto:", new Font("Inter", Font.PLAIN, 14), DARK_GRAY, null);
        JLabel categoriaLabel = createLabel("Categoria:", new Font("Inter", Font.PLAIN, 14), DARK_GRAY, null);
        JLabel categoriaDescricaoLabel = createLabel(quartosRepository.buscaQuartoPorId(reserva.quarto()).categoria().categoria().toUpperCase(), new Font("Inter", Font.BOLD, 14), DARK_GRAY, null);
        Vector<String> roomItems = new Vector<>();
        quartosRepository.buscaTodosOsQuartos().forEach(q -> roomItems.add(q.quarto_id() + " - " + q.status_quarto_enum()));
        quartoComboBox = new JComboBox<>(roomItems);
        quartoComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        quartoComboBox.setLightWeightPopupEnabled(false);
        quartoComboBox.setSelectedItem(roomItems.get(reserva.quarto().intValue() - 1));
        quartoPanel.add(quartoLabel);
        quartoPanel.add(quartoComboBox);
        quartoPanel.add(Box.createHorizontalStrut(5));
        quartoPanel.add(categoriaLabel);
        quartoPanel.add(categoriaDescricaoLabel);
        infoPanel.add(quartoPanel);
        JPanel mainHorizontalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainHorizontalPanel.setBackground(BACKGROUND_GRAY);
        JPanel datePanel = new JPanel(new BorderLayout());
        JPanel checkinCheckoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        checkinCheckoutPanel.setOpaque(false);
        JLabel checkinLabel = createLabel("Checkin:", new Font("Inter", Font.PLAIN, 14), DARK_GRAY, null);
        checkinLabel.setPreferredSize(new Dimension(70, 25));
        checkinField = new JFormattedTextField();
        checkinField.setText(reserva.data_entrada().format(df));
        checkinField.setColumns(6);
        JLabel checkoutLabel = createLabel("Checkout:", new Font("Inter", Font.PLAIN, 14), DARK_GRAY, null);
        checkoutLabel.setPreferredSize(new Dimension(70, 25));
        checkoutField = new JFormattedTextField();
        checkoutField.setText(reserva.data_saida().format(df));
        checkoutField.setColumns(6);
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
        JLabel horarioTitulo = createLabel("Horário previsto de chegada:", new Font("Inter", Font.PLAIN, 14), DARK_GRAY, null);
        horarioTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        TimePicker timePicker = new TimePicker();
        timePicker.set24HourView(true);
        timePicker.setSelectedTime(reserva.hora_prevista());
        JPanel timePickerContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timePickerContainer.add(timePicker);
        timePickerContainer.setOpaque(false);
        timePanel.add(horarioTitulo, BorderLayout.NORTH);
        timePanel.add(timePickerContainer, BorderLayout.CENTER);
        mainHorizontalPanel.add(datePanel);
        mainHorizontalPanel.add(timePanel);
        infoPanel.add(mainHorizontalPanel);
        Runnable updateDiariasInfo = () -> {
            try {
                LocalDate newCheckin = LocalDate.parse(checkinField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate newCheckout = LocalDate.parse(checkoutField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                checkinDate = newCheckin;
                checkoutDate = newCheckout;
                final int numDiarias = Period.between(newCheckin, newCheckout).getDays();
                int qtdPessoas = selectedPeople.size();
                String selectedRoomStr = (String) quartoComboBox.getSelectedItem();
                selectedRoom = selectedRoomStr;
                Long roomId = Long.parseLong(selectedRoomStr.split(" - ")[0]);
                Float valorDiariaObj = quartosRepository.getValorCategoria(roomId, qtdPessoas);
                double valorDiaria = (valorDiariaObj != null) ? valorDiariaObj : 0;
                double total = numDiarias * valorDiaria;
                animateLabelSpin(labelPessoasValue, parseLabelValue(labelPessoasValue.getText()), qtdPessoas, false);
                animateLabelSpin(labelDiariasValue, parseLabelValue(labelDiariasValue.getText()), numDiarias, false);
                animateLabelSpin(labelValorDiariaValue, parseLabelValue(labelValorDiariaValue.getText()), valorDiaria, true);
                animateLabelSpin(labelTotalValue, parseLabelValue(labelTotalValue.getText()), total, true);
                datePickerRange.setSelectedDateRange(newCheckin, newCheckout);
                datePickerRange.repaint();
            } catch (Exception ex) {
                labelPessoasValue.setText(String.valueOf(selectedPeople.size()));
                labelDiariasValue.setText("...");
                labelValorDiariaValue.setText("...");
                labelTotalValue.setText("...");
            }
        };
        datePickerRange.addDateSelectionListener(e -> {
            LocalDate[] selectedDates = datePickerRange.getSelectedDateRange();
            if (selectedDates != null && selectedDates[0] != null && selectedDates[1] != null) {
                checkinField.setText(selectedDates[0].format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                checkoutField.setText(selectedDates[1].format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                updateDiariasInfo.run();
            }
        });
        checkinField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            public void update() { updateDiariasInfo.run(); }
        });
        checkoutField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            public void update() { updateDiariasInfo.run(); }
        });
        quartoComboBox.addActionListener(e -> {
            String selectedRoomStr = (String) quartoComboBox.getSelectedItem();
            Long roomId = Long.parseLong(Objects.requireNonNull(selectedRoomStr).split(" - ")[0]);
            categoriaDescricaoLabel.setText(quartosRepository.buscaQuartoPorId(roomId).categoria().categoria().toUpperCase());
            List<DatasReserva> reservasAtualizadas = reservasRepository.datasReservadasPorQuarto(roomId, reserva.reserva_id());
            datePickerRange.setReservasDoQuarto(reservasAtualizadas);
            datePickerRange.repaint();
            updateDiariasInfo.run();
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

    private void salvarReservaEditada(JButton salvarButton, BuscaReservasResponse response) {
        salvarButton.addActionListener(a ->
                notification(menu, Type.SUCCESS, TOP_CENTER, "Reserva adicionada com sucesso!\n" + response.pessoas().get(0).nome()));
    }

    public BotaoArredondado adicionarBlocoPessoa(PessoaResponse pessoa) {
        BufferedImage pessoaFoto = null;
        try {
            pessoaFoto = pessoaRepository.buscarFotoBufferedPessoaPorId(pessoa.id());
        } catch (SQLException | IOException ignored) { }
        LabelArredondado labelFotoPessoa = new LabelArredondado("");
        labelFotoPessoa.setBackground(BACKGROUND_GRAY);
        int larguraFoto = 50, alturaFoto = 50;
        ImageIcon icon;
        if (pessoaFoto != null) {
            icon = resizeIcon(new ImageIcon(arredondar(pessoaFoto)), larguraFoto, alturaFoto);
        } else {
            BufferedImage img = pessoa.sexo().equals(FEMININO.ordinal())
                    ? arredondar(convertImageIconToBufferedImage(user_sem_foto_feminino))
                    : arredondar(convertImageIconToBufferedImage(user_sem_foto));
            icon = resizeIcon(new ImageIcon(img), larguraFoto, alturaFoto);
        }
        labelFotoPessoa.setIcon(icon);
        BotaoArredondado blocoPessoaButton = new BotaoArredondado("");
        blocoPessoaButton.setBorderPainted(false);
        blocoPessoaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        blocoPessoaButton.setLayout(new BorderLayout());
        blocoPessoaButton.setOpaque(false);
        blocoPessoaButton.setContentAreaFilled(false);
        blocoPessoaButton.setFocusPainted(false);
        blocoPessoaButton.setBackground(BACKGROUND_GRAY);
        blocoPessoaButton.setPreferredSize(new Dimension(0, 60));
        blocoPessoaButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        ShadowButton removeButton = btn_backgroung("");
        removeButton.setIcon(Resize.resizeIcon(close, 15, 15));
        removeButton.setPreferredSize(new Dimension(40, 30));
        removeButton.setFocusPainted(false);
        removeButton.enableHoverEffect();
        JLabel nomeTelefoneLabel = new JLabel("<html>" + pessoa.nome() + "<br>" + pessoa.telefone() + "</html>");
        nomeTelefoneLabel.setForeground(GRAY);
        nomeTelefoneLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        nomeTelefoneLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        blocoPessoaButton.add(labelFotoPessoa, BorderLayout.WEST);
        blocoPessoaButton.add(removeButton, BorderLayout.EAST);
        blocoPessoaButton.add(nomeTelefoneLabel, BorderLayout.CENTER);
        return blocoPessoaButton;
    }

    private void createGoogleStyleBuscaPessoaPanel(JPanel pessoasTab, BuscaReservasResponse reserva) {
        reserva.pessoas().forEach(r -> selectedPeople.add(pessoaRepository.buscarPessoaPorID(r.pessoa_id())));
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        JPanel buscarPessoaPanel = new JPanel(new BorderLayout());
        buscarPessoaPanel.setBackground(BACKGROUND_GRAY);
        JTextField buscarPessoaField = new JTextField(40);
        JPanel buscarPessoaInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscarPessoaInputPanel.setBackground(BACKGROUND_GRAY);
        buscarPessoaInputPanel.add(new JLabel(Resize.resizeIcon(search, 15, 15)));
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
        Runnable atualizarCalculos = () -> {
            try {
                LocalDate newCheckin = LocalDate.parse(checkinField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate newCheckout = LocalDate.parse(checkoutField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                checkinDate = newCheckin;
                checkoutDate = newCheckout;
                final int numDiarias = Period.between(newCheckin, newCheckout).getDays();
                int qtdPessoas = selectedPeople.size();
                String selectedRoomStr = (String) quartoComboBox.getSelectedItem();
                Long roomId = Long.parseLong(selectedRoomStr.split(" - ")[0]);
                Float valorDiariaObj = quartosRepository.getValorCategoria(roomId, qtdPessoas);
                double valorDiaria = (valorDiariaObj != null) ? valorDiariaObj : 0;
                double total = numDiarias * valorDiaria;

                animateLabelSpin(labelPessoasValue, parseLabelValue(labelPessoasValue.getText()), qtdPessoas, false);
                animateLabelSpin(labelDiariasValue, parseLabelValue(labelDiariasValue.getText()), numDiarias, false);
                animateLabelSpin(labelValorDiariaValue, parseLabelValue(labelValorDiariaValue.getText()), valorDiaria, true);
                animateLabelSpin(labelTotalValue, parseLabelValue(labelTotalValue.getText()), total, true);
            } catch (Exception ex) {
                labelPessoasValue.setText(String.valueOf(selectedPeople.size()));
                labelDiariasValue.setText("...");
                labelValorDiariaValue.setText("...");
                labelTotalValue.setText("...");
            }
        };

        Runnable atualizarListaPessoas = () -> {
            pessoasContainer.removeAll();
            for (PessoaResponse pessoa : selectedPeople) {
                BotaoArredondado bloco = adicionarBlocoPessoa(pessoa);
                bloco.setAlignmentX(Component.LEFT_ALIGNMENT);
                for (Component comp : bloco.getComponents()) {
                    if (comp instanceof ShadowButton) {
                        ((ShadowButton) comp).addActionListener(e -> {
                            selectedPeople.remove(pessoa);
                            pessoasContainer.remove(bloco);
                            pessoasContainer.revalidate();
                            pessoasContainer.repaint();
                            reservasRepository.removerPessoaReserva(pessoa.id(), reserva.reserva_id());
                            atualizarCalculos.run();
                            notification(menu, Type.SUCCESS, TOP_CENTER, "Pessoa removida com sucesso: " + pessoa.nome());
                        });
                    }
                }
                pessoasContainer.add(bloco);
            }
            pessoasContainer.revalidate();
            pessoasContainer.repaint();
            atualizarCalculos.run();
        };
        atualizarListaPessoas.run();
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
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
                    resultados.removeAll(selectedPeople);
                    sugestaoModel.clear();
                    if (!resultados.isEmpty()) {
                        resultados.forEach(sugestaoModel::addElement);
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
                        selectedPeople.add(pessoaSelecionada);
                        atualizarListaPessoas.run();
                        atualizarCalculos.run();
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
}
