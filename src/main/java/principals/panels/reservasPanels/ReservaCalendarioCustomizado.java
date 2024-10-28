package principals.panels.reservasPanels;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDayChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReservaCalendarioCustomizado {

    private static final Color CINZA_CLARO = new Color(240, 240, 240);
    private static final Color CINZA_ESCURO = new Color(0x696363);
    private static final Color AZUL_ESCURO = new Color(0x424B98);
    private static final Color VERDE_ESCURO = new Color(0x148A20);
    private static final Color VERMELHO = new Color(0xF85A5A);

    private JButton selectedButton = null;  // Variável para manter o botão selecionado

    public JCalendar createCustomCalendar() {
        JCalendar jCalendar = new JCalendar();
        jCalendar.setLocale(new Locale("pt", "BR"));
        jCalendar.setPreferredSize(new Dimension(900, 600));
        jCalendar.setFont(new Font("Inter", Font.PLAIN, 20));

        List<LocalDate> datasOcupadas = new ArrayList<>();
        datasOcupadas.add(LocalDate.now().plusDays(2));
        datasOcupadas.add(LocalDate.now().plusDays(3));
        datasOcupadas.add(LocalDate.now().plusDays(5));

        jCalendar.getDayChooser().addPropertyChangeListener("day", evt -> applyButtonStyles(jCalendar.getDayChooser(), jCalendar, datasOcupadas));
        applyButtonStyles(jCalendar.getDayChooser(), jCalendar, datasOcupadas);

        return jCalendar;
    }

    private void applyButtonStyles(JDayChooser dayChooser, JCalendar jCalendar, List<LocalDate> datasOcupadas) {
        JPanel daysPanel = dayChooser.getDayPanel();
        int month = jCalendar.getMonthChooser().getMonth() + 1;
        int year = jCalendar.getYearChooser().getYear();
        LocalDate today = LocalDate.now();

        for (Component comp : daysPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton dayButton = (JButton) comp;
                if (isNumeric(dayButton.getText().trim())) {  // Check if the button text is numeric
                    int day = Integer.parseInt(dayButton.getText().trim());
                    LocalDate buttonDate = LocalDate.of(year, month, day);

                    // Apply initial styles
                    dayButton.setBackground(Color.WHITE);
                    dayButton.setForeground(CINZA_ESCURO);
                    dayButton.setBorder(BorderFactory.createEmptyBorder());
                    dayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    // Apply styles for occupied dates
                    if (datasOcupadas.contains(buttonDate)) {
                        dayButton.setBackground(new Color(0xFFA500));  // Orange for occupied
                        dayButton.setForeground(Color.WHITE);
                    }

                    // Apply styles for today's date
                    if (buttonDate.equals(today)) {
                        dayButton.setForeground(VERMELHO);  // Red for today
                    }

                    // Hover effects
                    addMouseEffects(dayButton, datasOcupadas, buttonDate,jCalendar);
                }
            }
        }
    }

    // Helper method to determine if a string is numeric
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Method to add mouse hover effects
    private void addMouseEffects(JButton dayButton, List<LocalDate> datasOcupadas, LocalDate buttonDate, JCalendar jCalendar) {
        dayButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                dayButton.setBackground(dayButton.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!dayButton.equals(selectedButton)) {
                    dayButton.setBackground(datasOcupadas.contains(buttonDate) ? new Color(0xFFA500) : Color.WHITE);
                }
            }
        });

        dayButton.addActionListener(e -> {
            if (selectedButton != null) {
                resetButtonColors(selectedButton, datasOcupadas, jCalendar);
            }
            selectedButton = dayButton;
            dayButton.setBackground(AZUL_ESCURO);
            dayButton.setForeground(Color.WHITE);
        });
    }

    // Reset button colors
    private void resetButtonColors(JButton button, List<LocalDate> datasOcupadas, JCalendar jCalendar) {
        int day = Integer.parseInt(button.getText().trim());
        LocalDate buttonDate = LocalDate.of(jCalendar.getYearChooser().getYear(), jCalendar.getMonthChooser().getMonth() + 1, day);
        button.setBackground(datasOcupadas.contains(buttonDate) ? new Color(0xFFA500) : Color.WHITE);
        button.setForeground(datasOcupadas.contains(buttonDate) ? Color.WHITE : CINZA_ESCURO);
    }

}
