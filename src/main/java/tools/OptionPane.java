package tools;

import javax.swing.*;

import static tools.CorPersonalizada.*;
import static tools.Icones.*;
import static tools.Resize.resizeIcon;

public class OptionPane {
    public static void ok(JFrame jframe, String message){
        new Toast(jframe, message, GREEN, resizeIcon(check, 30,30));
    }

    public static void warning(JFrame jframe, String message){
        Toast a = new Toast(jframe, message, YELLOW, resizeIcon(warning, 25, 25));
        a.setForeground(DARK_GRAY);    }

    public static void error(JFrame jframe, String message){
        new Toast(jframe, message, RED_2, resizeIcon(error, 30, 30));
    }


    public static void ok(JLayeredPane layeredPane, String message){
        new Toast(layeredPane, message, GREEN, resizeIcon(check, 30,30));
    }

    public static void warning(JLayeredPane layeredPane, String message){
        Toast a = new Toast(layeredPane, message, YELLOW, resizeIcon(warning, 25, 25));
        a.setForeground(DARK_GRAY);
    }

    public static void error(JLayeredPane layeredPane, String message){
        new Toast(layeredPane, message, RED_2, resizeIcon(error, 30, 30));
    }
}
