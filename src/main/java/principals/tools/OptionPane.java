package principals.tools;

import javax.swing.*;

import static principals.tools.CorPersonalizada.*;
import static principals.tools.Icones.*;
import static principals.tools.Resize.resizeIcon;

public class OptionPane {
    public static void ok(JFrame menu, String message){
        new Toast(menu, message, GREEN, resizeIcon(check, 30,30));
    }

    public static void warning(JFrame menu, String message){
        new Toast(menu, message, YELLOW, resizeIcon(warning, 25, 25));
    }

    public static void error(JFrame menu, String message){
        new Toast(menu, message, RED_2, resizeIcon(error, 30, 30));
    }
}
