package calendar;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public final class Years extends JPanel {
    @Getter
    private Event event;
    private int startYear;
    private final Button[] buttons = new Button[20];

    public Years() {
        initComponents();
    }

    public int showYear(int year) {
        year = calculateYear(year);
        for (int i = 0; i < getComponentCount(); i++) {
            Button cmd = (Button) getComponent(i);
            cmd.setText(year + "");
            year++;
        }
        return startYear;
    }

    private int calculateYear(int year) {
        if(year < 0){
            throw new IllegalArgumentException("Year cannot be negative");
        }
        year -= year % 10;
        startYear = year;
        return year;
    }

    private void addEvent() {
        for (Button button : buttons) {
            button.setEvent(event);
        }
    }

    private void initComponents() {
        setBackground(new Color(255, 255, 255));
        setLayout(new GridLayout(5, 4));

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = createButton(2010 + i);
            add(buttons[i]);
        }
    }

    private Button createButton(int year) {
        Button button = new Button();
        button.setText(String.valueOf(year));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(75, 75, 75));
        button.setName("year");
        button.setOpaque(true);
        return button;
    }

    public void setEvent(Event event) {
        this.event = event;
        addEvent();
    }

    public int next(int year) {
        showYear(year + 20);
        return startYear;
    }

    public int back(int year) {
        showYear(year - 20);
        return startYear;
    }
}
