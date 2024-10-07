package principals.panels;

import principals.tools.Icones;

import javax.swing.*;
import java.awt.*;

public class ItensPanel extends javax.swing.JPanel {
    public ItensPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Itens", Icones.itens);

        add(identificadorPanel, BorderLayout.NORTH);
    }
}
