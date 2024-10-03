package principals.panels;

import principals.tools.Botoes;

import javax.swing.*;
import java.awt.*;

public class ReservationPanel extends javax.swing.JPanel {
    public ReservationPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Reservas", Botoes.reservations_icon);

        add(identificadorPanel, BorderLayout.NORTH);
    }
}
