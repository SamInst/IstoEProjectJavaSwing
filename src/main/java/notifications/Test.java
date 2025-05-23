package notifications;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Random;

public class Test extends JFrame {
    public Test() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 768);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new FlowLayout(FlowLayout.LEADING));
        JButton button = new JButton("Show");
        Notifications.getInstance().setJFrame(this);

        button.addActionListener(e -> Notifications.getInstance().show(getRandomType(), Notifications.Location.TOP_RIGHT, getRandomText()));

        getContentPane().add(button);
    }


    private void testMemory() {
        new Thread(
                () -> {
                    while (true) {
                        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                        System.out.println(formatSize(memoryUsage.getUsed()) + " of " + formatSize(memoryUsage.getCommitted()));
                        sleep();
                    }
                }
        ).start();
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {

        }
    }

    public String formatSize(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    private Notifications.Location getRandomLocation() {
        Random ran = new Random();
        int a = ran.nextInt(6);
        if (a == 0) {
            return Notifications.Location.TOP_LEFT;
        } else if (a == 1) {
            return Notifications.Location.TOP_CENTER;
        } else if (a == 2) {
            return Notifications.Location.TOP_RIGHT;
        } else if (a == 3) {
            return Notifications.Location.BOTTOM_LEFT;
        } else if (a == 4) {
            return Notifications.Location.BOTTOM_CENTER;
        } else if (a == 5) {
            return Notifications.Location.BOTTOM_RIGHT;
        } else {
            return Notifications.Location.BOTTOM_RIGHT;
        }
    }

    private String getRandomText() {
        Random ran = new Random();
        int a = ran.nextInt(5);
        if (a == 0) {
            return "Toast Notifications notify the user of a system occurrence";
        } else if (a == 1) {
            return "The notifications should have a consistent location in each application.\nWe recommend the top-right of the application";
        } else if (a == 2) {
            return "Toast Notifications notify the user of a system occurrence." +
                    "\nThe notifications should have a consistent location in each application." +
                    "\nWe recommend the top-right";
        } else if (a == 3) {
            return "Success";
        } else {
            return "Hello";
        }
    }

    private Notifications.Type getRandomType() {
        Random ran = new Random();
        int a = ran.nextInt(4);
        if (a == 0) {
            return Notifications.Type.SUCCESS;
        } else if (a == 1) {
            return Notifications.Type.INFO;
        } else if (a == 2) {
            return Notifications.Type.WARNING;
        } else {
            return Notifications.Type.ERROR;
        }
    }


    private void changeMode(boolean dark) {
        if (FlatLaf.isLafDark() != dark) {
            if (dark) {
                EventQueue.invokeLater(() -> {
                    FlatAnimatedLafChange.showSnapshot();
                    FlatMacDarkLaf.setup();
                    FlatLaf.updateUI();
                    FlatAnimatedLafChange.hideSnapshotWithAnimation();
                });
            } else {
                EventQueue.invokeLater(() -> {
                    FlatAnimatedLafChange.showSnapshot();
                    FlatMacLightLaf.setup();
                    FlatLaf.updateUI();
                    FlatAnimatedLafChange.hideSnapshotWithAnimation();
                    ;
                });
            }
        }
    }

    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("raven.toast");
        FlatMacLightLaf.setup();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Test().setVisible(true);
            }
        });
    }
}
