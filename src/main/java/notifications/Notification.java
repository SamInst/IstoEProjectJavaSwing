package notifications;

import javax.swing.*;

import static notifications.Notifications.*;

public class Notification {
    public static void notification(JFrame frame, Type type, Location location, String message) {
        getInstance().setJFrame(frame);
        getInstance().show(type, location, message);
    }
}
