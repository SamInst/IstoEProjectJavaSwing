package tools;

import javax.swing.*;
import java.awt.*;

public class TruncateText {
    public static String truncateText(String text, JComponent reference, int maxWidth) {
        FontMetrics fm = reference.getFontMetrics(reference.getFont());
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        int ellipsisWidth = fm.stringWidth(ellipsis);
        int end = text.length();
        while (end > 0) {
            if (fm.stringWidth(text.substring(0, end)) + ellipsisWidth <= maxWidth) {
                return text.substring(0, end) + ellipsis;
            }
            end--;
        }
        return ellipsis;
    }
}
