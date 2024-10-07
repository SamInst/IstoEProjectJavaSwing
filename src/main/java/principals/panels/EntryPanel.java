package principals.panels;

import principals.tools.Icones;

import javax.swing.*;
import java.awt.*;

public class EntryPanel extends javax.swing.JPanel {
    public EntryPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Entradas", Icones.entradas);
        add(identificadorPanel, BorderLayout.NORTH);
    }
}
