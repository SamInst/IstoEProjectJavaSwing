package calendar;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public final class Months extends JPanel {
    @Getter
    private Event event;
    private final Button[] buttons = new Button[12];

    public Months() {
        initComponents();
    }

    private void addEvent() {
        for (Button button : buttons) {
            button.setEvent(event);
        }
    }

    private void initComponents() {
        setBackground(new Color(255, 255, 255));
        setLayout(new GridLayout(4, 3));

        String[] monthNames = {
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = createButton(monthNames[i], String.valueOf(i + 1));
            add(buttons[i]);
        }
    }

    private Button createButton(String text, String name) {
        Button button = new Button();
        button.setText(text);
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(75, 75, 75));
        button.setName(name);
        button.setOpaque(true);
        return button;
    }

    public void setEvent(Event event) {
        this.event = event;
        addEvent();
    }
}
