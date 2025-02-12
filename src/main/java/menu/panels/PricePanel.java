package menu.panels;

import tools.Icones;

import javax.swing.*;
import java.awt.*;

public class PricePanel extends javax.swing.JPanel {
    public PricePanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = menu.Menu.createIdentificadorPanel("Precos", Icones.preco);

        add(identificadorPanel, BorderLayout.NORTH);
    }
}
