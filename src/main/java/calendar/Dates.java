package calendar;

import lombok.Getter;
import principals.tools.CorPersonalizada;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static principals.tools.CorPersonalizada.*;
import static principals.tools.CorPersonalizada.RED;
import static principals.tools.CorPersonalizada.RED_2;

public final class Dates extends JPanel {
    @Getter
    private Event event;
    private final int MONTH;
    private final int YEAR;
    private final int DAY;
    private int m, y, selectDay = 0, startDate, maxOfMonth;
    private final Button[] cmdDays;
    private final Button[] cmdWeekDays;

    public Dates() {
        cmdDays = new Button[42];
        cmdWeekDays = new Button[7];
        initComponents();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String today = df.format(date);
        DAY = Integer.parseInt(today.split("-")[0]);
        MONTH = Integer.parseInt(today.split("-")[1]);
        YEAR = Integer.parseInt(today.split("-")[2]);
    }

    public void showDate(int month, int year, SelectedDate select) {
        m = month;
        y = year;
        Calendar cd = Calendar.getInstance();
        cd.set(year, month - 1, 1);
        int start = cd.get(Calendar.DAY_OF_WEEK);
        maxOfMonth = cd.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (start == 1) start += 7;
        clear();
        startDate = start + 5;
        for (int i = 1; i <= maxOfMonth; i++) {
            if (startDate + i - 1 < cmdDays.length) {
                Button cmd = cmdDays[startDate + i - 1];
                cmd.setColorSelected(getForeground());
                cmd.setText(String.valueOf(i));
                int dayOfWeek = (startDate + i - 1) % 7;

                if (dayOfWeek == 6) {
                    cmd.setForeground(RED_2);
                } else {
                    cmd.setForeground(new Color(75, 75, 75));
                }
                if (i == DAY && month == MONTH && year == YEAR) {
                    cmd.setBackground(new Color(224, 214, 229));
                } else {
                    cmd.setBackground(Color.WHITE);
                }
                if (i == select.getDay() && month == select.getMonth() && year == select.getYear()) {
                    cmd.setBackground(getForeground());
                    cmd.setForeground(Color.WHITE);
                }
            } else {
                break;
            }
        }
    }


    private void clear() {
        for (int i = 7; i < cmdDays.length; i++) {
            cmdDays[i].setText("");
        }
    }

    public void clearSelected() {
        for (Button cmd : cmdDays) {
            if (MONTH == m && y == YEAR && !cmd.getText().isEmpty() && Integer.parseInt(cmd.getText()) == DAY) {
                cmd.setBackground(new Color(224, 214, 229));
                cmd.setForeground(new Color(75, 75, 75));
            } else {
                cmd.setBackground(Color.WHITE);
                cmd.setForeground(new Color(75, 75, 75));
            }
        }
        selectDay = 0;
    }

    private void addEvent() {
        for (Button cmd : cmdDays) {
            cmd.setEvent(event);
        }
    }

    public void setSelected(int index) {
        selectDay = index;
    }

    private void initComponents() {
        setBackground(Color.WHITE);
        setLayout(new GridLayout(7, 7));

        String[] weekDays = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
        for (int i = 0; i < weekDays.length; i++) {
            cmdWeekDays[i] = createButton(weekDays[i]);
            if (weekDays[i].equals("Dom")) cmdWeekDays[i].setForeground(RED);
            add(cmdWeekDays[i]);
        }

        for (int i = 0; i < cmdDays.length; i++) {
            cmdDays[i] = createButton("");
            cmdDays[i].setName("day");
            add(cmdDays[i]);
        }
    }

    private Button createButton(String text) {
        Button button = new Button();
        button.setText(text);
        button.setBackground(Color.WHITE);
        button.setForeground(CorPersonalizada.GRAY);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            final Color originalBackground = button.getBackground();

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!button.getText().isEmpty() && Integer.parseInt(button.getText()) != DAY) {
                    button.setBackground(BACKGROUND_GRAY);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.getText().isEmpty() && Integer.parseInt(button.getText()) != DAY) {
                    button.setBackground(originalBackground);
                }
            }
        });

        return button;
    }


    public void setEvent(Event event) {
        this.event = event;
        addEvent();
    }

    public void next() {
        if (selectDay == maxOfMonth) selectDay = 0;
        int nextIndex = startDate - 1 + selectDay + 1;
        if (nextIndex < cmdDays.length) {
            JButton cmd = cmdDays[nextIndex];
            String n = cmd.getText();
            if (!n.isEmpty() && Integer.parseInt(n) <= maxOfMonth) {
                selectDay++;
                event.execute(null, selectDay);
                cmd.setBackground(new Color(206, 110, 245));
            }
        }
    }

    public void back() {
        if (selectDay <= 1) selectDay = maxOfMonth + 1;
        int prevIndex = startDate - 1 + selectDay - 1;
        if (prevIndex < cmdDays.length) {
            JButton cmd = cmdDays[prevIndex];
            String n = cmd.getText();
            if (!n.isEmpty()) {
                selectDay--;
                event.execute(null, selectDay);
                cmd.setBackground(new Color(206, 110, 245));
            }
        }
    }

    public void up() {
        int upIndex = startDate - 1 + selectDay - 7;
        if (upIndex >= 0) {
            JButton cmd = cmdDays[upIndex];
            String n = cmd.getText();
            if (!n.isEmpty()) {
                selectDay -= 7;
                event.execute(null, selectDay);
                cmd.setBackground(new Color(206, 110, 245));
            }
        }
    }

    public void down() {
        int downIndex = startDate - 1 + selectDay + 7;
        if (downIndex < cmdDays.length) {
            JButton cmd = cmdDays[downIndex];
            String n = cmd.getText();
            if (!n.isEmpty()) {
                selectDay += 7;
                event.execute(null, selectDay);
                cmd.setBackground(new Color(206, 110, 245));
            }
        }
    }
}
