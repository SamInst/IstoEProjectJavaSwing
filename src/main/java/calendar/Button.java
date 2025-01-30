package calendar;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Setter
@Getter
public final class Button extends JButton {
    private Event event;
    private boolean paintBackground = true;
    private Color colorSelected;

    public Button() {
        setBorder(null);
        setContentAreaFilled(false);
        setFocusable(false);
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (!getText().isEmpty() && getName() != null && event != null) {
                    if (getName().equals("day") || getName().equals("year")) {
                        event.execute(me, Integer.parseInt(getText()));
                    } else {
                        event.execute(me, Integer.parseInt(getName()));
                    }
                    setBackground(getColorSelected());
                    setForeground(Color.WHITE);
                }
            }
        });
    }

    @Override
    public void paint(Graphics grphcs) {
        if (paintBackground) {
            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height);
            int x = (width - size) / 2;
            int y = (height - size) / 2;
            Graphics2D g2 = (Graphics2D) grphcs;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillOval(x, y, size, size);
        }
        super.paint(grphcs);
    }
}
