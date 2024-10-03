package principals.panels;

import principals.tools.Botoes;

import javax.swing.*;
import java.awt.*;

public class CustomersPanel  extends javax.swing.JPanel {
    public CustomersPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Clientes", Botoes.clientes_icon);
        add(identificadorPanel, BorderLayout.NORTH);
    }
}
