package principals.panels;

import principals.tools.Botoes;

import javax.swing.*;
import java.awt.*;

public class DashBoardPanel extends javax.swing.JPanel {
    public DashBoardPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Dashboard", Botoes.dashboard_icon);

        add(identificadorPanel, BorderLayout.NORTH);
    }
}
