package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PanelPopup {

    public void showPersistentPopup(Component invoker, JPanel... panels) {
        Window owner = SwingUtilities.getWindowAncestor(invoker);

        JDialog popup = new JDialog(owner);
        popup.setUndecorated(true);
        popup.setAlwaysOnTop(false);

        popup.getContentPane().setLayout(new GridLayout(0, 1));
        for (JPanel panel : panels) {
            popup.getContentPane().add(panel);
        }
        popup.pack();
//        popup.getRootPane().setBorder(new RoundedBorder(15));

        Point p = invoker.getLocationOnScreen();
        popup.setLocation(p.x, p.y + invoker.getHeight());

        popup.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                popup.dispose();
            }
        });

        popup.setVisible(true);
    }

}

