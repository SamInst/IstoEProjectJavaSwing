package lateralMenu.main;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import lateralMenu.drawer.MyDrawerBuilder;
import lateralMenu.login.Login;
import lateralMenu.tabbed.WindowsTabbed;
import raven.drawer.Drawer;

import raven.popup.GlassPanePopup;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    public static Main main;
    private Login loginForm;

    public Main() {
        initComponents();
        init();
    }

    private void init() {
        GlassPanePopup.install(this);
        Notifications.getInstance().setJFrame(this);
        MyDrawerBuilder myDrawerBuilder = new MyDrawerBuilder();
        Drawer.getInstance().setDrawerBuilder(myDrawerBuilder);
        WindowsTabbed.getInstance().install(this, body);
        login();
    }

    public void login() {
        if (loginForm == null) {
            loginForm = new Login();
        }
        WindowsTabbed.getInstance().showTabbed(false);
        loginForm.applyComponentOrientation(getComponentOrientation());
        setContentPane(loginForm);
        revalidate();
        repaint();
    }

    public void showMainForm() {
        WindowsTabbed.getInstance().showTabbed(true);
        WindowsTabbed.getInstance().removeAllTabbed();
        setContentPane(body);
        revalidate();
        repaint();
    }

    private void initComponents() {
        body = new JPanel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        body.setLayout(new BorderLayout());

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(body, GroupLayout.DEFAULT_SIZE, 1188, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(body, GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("raven.themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacLightLaf.setup();
        Notifications.getInstance().setJFrame(Main.main);
        EventQueue.invokeLater(() -> {
            main = new Main();
            main.setVisible(true);
        });
    }

    private JPanel body;

}
