package principals.panels;

import principals.tools.Icones;

import javax.swing.*;
import java.awt.*;

public class ReservationPanel extends javax.swing.JPanel {
    public ReservationPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Reservas", Icones.reservas);

        add(identificadorPanel, BorderLayout.NORTH);
    }
}
