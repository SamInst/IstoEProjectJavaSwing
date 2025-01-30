package principals.tools;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDayChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

public class CustomJCalendar {
    private static final Color CINZA_ESCURO = new Color(0x696363);

    private JButton selectedButton = null;

    public JCalendar createCustomCalendar() {
        JCalendar jCalendar = new JCalendar();
        jCalendar.setLocale(new Locale("pt", "BR"));
        jCalendar.setPreferredSize(new Dimension(300, 200));
        jCalendar.getMonthChooser().addPropertyChangeListener("month", evt -> applyButtonStyles(jCalendar.getDayChooser()));

        applyButtonStyles(jCalendar.getDayChooser());

        return jCalendar;
    }

    private void applyButtonStyles(JDayChooser dayChooser) {
        JPanel daysPanel = dayChooser.getDayPanel();

        for (Component component : daysPanel.getComponents()) {
            if (component instanceof JButton dayButton) {
                dayButton.setFont(new Font("Roboto", Font.BOLD, 16));
                dayButton.setForeground(CINZA_ESCURO);
                dayButton.setBackground(CorPersonalizada.BACKGROUND_GRAY);
                dayButton.setBorder(BorderFactory.createEmptyBorder());
                dayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                dayButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (!dayButton.equals(selectedButton)) {
                            dayButton.setBackground(new Color(0x424B98));
                            dayButton.setForeground(Color.WHITE);
                        }

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (!dayButton.equals(selectedButton)) {
                            dayButton.setBackground(CorPersonalizada.BACKGROUND_GRAY);
                            dayButton.setForeground(CINZA_ESCURO);
                        }
                    }
                });
            }
        }
    }
}
