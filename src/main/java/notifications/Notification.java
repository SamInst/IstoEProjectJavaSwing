package notifications;

import javax.swing.*;

import static notifications.Notifications.*;

public class Notification {
    public static void notification(Type type, Location location, String message) {
        getInstance().show(type, location, message);
    }
}
