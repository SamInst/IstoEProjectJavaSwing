package menu.panels;

import tools.Icones;

import javax.swing.*;
import java.awt.*;

public class ItensPanel extends javax.swing.JPanel {
    public ItensPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = menu.Menu.createIdentificadorPanel("Itens", Icones.itens);

        add(identificadorPanel, BorderLayout.NORTH);
    }
}
