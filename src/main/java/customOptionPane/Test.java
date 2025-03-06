package customOptionPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Test extends javax.swing.JFrame {
    public Test() {
        initComponents();
        GlassPanePopup.install(this);
    }

    private void initComponents() {
        JButton jButton1 = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Show Message");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
//                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(313, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(305, 305, 305))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(240, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(226, 226, 226))
        );

        pack();
        setLocationRelativeTo(null);
    }

//    private void jButton1ActionPerformed(ActionEvent evt) {
//        Message obj = new Message();
//        obj.eventOK(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                System.out.println("Click OK");
//                GlassPanePopup.closePopupLast();
//            }
//        });
//        GlassPanePopup.showPopup(obj);
//    }
//
//    public static void main(String args[]) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new Test().setVisible(true);
//            }
//        });
//    }

}
