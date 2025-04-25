package lateralMenu.form;

import javax.swing.JOptionPane;

import lateralMenu.tabbed.TabbedForm;
import lateralMenu.tabbed.WindowsTabbed;
import raven.alerts.MessageAlerts;
import raven.popup.component.PopupCallbackAction;
import raven.popup.component.PopupController;
import raven.toast.Notifications;

public class TestForm extends TabbedForm {

    public TestForm() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();

        jButton1.setText("Test");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txt.setColumns(20);
        txt.setRows(5);
        jScrollPane1.setViewportView(txt);

        jButton2.setText("Show Alerts");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(222, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(136, 136, 136))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(209, Short.MAX_VALUE))
        );
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Notifications.getInstance().show(Notifications.Type.INFO, "Test Toast Notifications");
        //WindowsTabbed.getInstance().removeTab(this);

        String[] arr = WindowsTabbed.getInstance().getTabName();
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }

        System.out.println("------------");

        System.out.println("selected: " + WindowsTabbed.getInstance().getTabSelectedIndex());
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        MessageAlerts.getInstance().showMessage("Data Saving Failure", "Oops! We encountered an issue while attempting to save your data. Please try again later or contact support for assistance. Apologies for any inconvenience caused.", MessageAlerts.MessageType.ERROR, MessageAlerts.OK_OPTION, new PopupCallbackAction() {
            @Override
            public void action(PopupController pc, int i) {
                if (i == MessageAlerts.OK_OPTION) {
                    System.out.println("Click ok");
                }
            }
        });
    }

    @Override
    public boolean formClose() {
        if (txt.getText().trim().equals("")) {
            return true;
        }
        int opt = JOptionPane.showConfirmDialog(this, "Data not save do you want to close ?", "Close", JOptionPane.YES_NO_OPTION);
        return opt == JOptionPane.YES_OPTION;
    }

    @Override
    public void formOpen() {
        System.out.println("Form open");
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txt;
}
