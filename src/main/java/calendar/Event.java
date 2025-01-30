package calendar;

import java.awt.event.MouseEvent;

public interface Event {
    void execute(MouseEvent evt, int num);
}
