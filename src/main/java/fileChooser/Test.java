package fileChooser;

import fileChooser.api.JnaFileChooser;

import javax.swing.*;
import java.awt.*;

public class Test extends javax.swing.JFrame {

    public Test() {
        initComponents();
    }

    private void initComponents() {
        JButton jButton1 = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Open");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(396, 396, 396)
                .addComponent(jButton1)
                .addContainerGap(422, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(199, 199, 199)
                .addComponent(jButton1)
                .addContainerGap(353, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        JnaFileChooser jnaCh = new JnaFileChooser();
        jnaCh.setDefaultFileName("gg.txt");
        boolean save = jnaCh.showSaveDialog(this);
        if (save) {
            System.out.println(jnaCh.getSelectedFile());
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Test().setVisible(true));
    }
}
