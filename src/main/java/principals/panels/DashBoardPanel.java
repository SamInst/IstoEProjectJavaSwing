package principals.panels;

import principals.tools.Icones;

import javax.swing.*;
import java.awt.*;

public class DashBoardPanel extends javax.swing.JPanel {
    public DashBoardPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Dashboard", Icones.dashboard);

        add(identificadorPanel, BorderLayout.NORTH);
    }
}
