package customOptionPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class Message extends javax.swing.JPanel {

    public Message(String titulo, String message, String buttonTextOK, String buttonTextCancelar, Color buttonOkColor) {
        initComponents(titulo, message, buttonTextOK, buttonTextCancelar, buttonOkColor);
        setOpaque(false);
        txt.setBackground(new Color(0, 0, 0, 0));
        txt.setSelectionColor(new Color(48, 170, 63, 200));
        txt.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
        g2.dispose();
        super.paintComponent(grphcs);
    }

    private void initComponents(String titulo, String message, String buttonTextOK, String buttonTextCancel, Color buttonOkColor) {
        jLabel1 = new JLabel();
        txt = new JTextPane();
        cmdOK = new Button();
        cmdCancel = new Button();

        setBackground(new Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));

        jLabel1.setFont(new java.awt.Font("sansserif", Font.BOLD, 14));
        jLabel1.setForeground(new Color(80, 80, 80));
        jLabel1.setText(titulo);

        txt.setEditable(false);
        txt.setForeground(new Color(133, 133, 133));
        txt.setText(message);

        cmdOK.setBackground(buttonOkColor);
        cmdOK.setForeground(new Color(255, 255, 255));
        cmdOK.setText(buttonTextOK);
        cmdOK.setFocusPainted(false);
        cmdOK.setCursor(new Cursor(Cursor.HAND_CURSOR));


        cmdCancel.setBackground(new Color(233, 233, 233));
        cmdCancel.setText(buttonTextCancel);
        cmdCancel.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelActionPerformed(evt);
            }
        });
        cmdCancel.setFocusPainted(false);
        cmdCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 261, Short.MAX_VALUE))
                    .addComponent(txt, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cmdCancel, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cmdOK, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdOK, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdCancel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }

    private void cmdCancelActionPerformed(java.awt.event.ActionEvent evt) {
        GlassPanePopup.closePopupLast();
    }

    public void eventOK(ActionListener event) {
        cmdOK.addActionListener(event);
    }

    private Button cmdCancel;
    private Button cmdOK;
    private JLabel jLabel1;
    private JTextPane txt;
}
