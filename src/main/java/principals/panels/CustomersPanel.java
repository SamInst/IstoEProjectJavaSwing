package principals.panels;

import principals.panels.pessoaPanel.IdentificacaoPessoaFrame;
import principals.tools.Icones;

import javax.swing.*;
import java.awt.*;

public class CustomersPanel  extends javax.swing.JPanel {
    public CustomersPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Clientes", Icones.clientes);
        add(identificadorPanel, BorderLayout.NORTH);
        JButton butao = new JButton("Buscar pessoa");
        butao.addActionListener(e -> {
            new IdentificacaoPessoaFrame ("050.432.263-07");
        });
    }
}
