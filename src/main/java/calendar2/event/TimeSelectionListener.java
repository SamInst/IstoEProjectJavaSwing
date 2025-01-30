package calendar2.event;

import java.util.EventListener;

public interface TimeSelectionListener extends EventListener {

    void timeSelected(TimeSelectionEvent timeSelectionEvent);
}
