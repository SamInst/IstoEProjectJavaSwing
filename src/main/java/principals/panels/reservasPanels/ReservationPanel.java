package principals.panels.reservasPanels;

import principals.tools.Icones;
import repository.ReservasRepository;
import javax.swing.*;
import java.awt.*;

public class ReservationPanel extends javax.swing.JPanel {
    ReservasRepository reservasRepository = new ReservasRepository();
    public ReservationPanel() {


        JPanel reservasPanel = new JPanel();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Reservas", Icones.reservas);

        topPanel.add(identificadorPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttonPanel.setMinimumSize(new Dimension(20, 20));

        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.setPreferredSize(new Dimension(125, 40));
        btnAdicionar.addActionListener(e -> {});

        buttonPanel.add(btnAdicionar);
        identificadorPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        reservasPanel.setLayout(new BoxLayout(reservasPanel, BoxLayout.Y_AXIS));

        add(topPanel, BorderLayout.NORTH);

        reservasRepository.todasReservas().forEach(reserva -> {
            reservasPanel.add(new BlocoReservasAtivas().blocoReservasPanel(new JPanel(), reserva));

        });

        JScrollPane scrollPane = new JScrollPane(reservasPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);


    }
}