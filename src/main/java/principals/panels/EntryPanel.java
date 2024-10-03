package principals.panels;

import principals.tools.Botoes;

import javax.swing.*;
import java.awt.*;

public class EntryPanel extends javax.swing.JPanel {
    public EntryPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Entradas", Botoes.entradas_icon);
        add(identificadorPanel, BorderLayout.NORTH);
    }
}
