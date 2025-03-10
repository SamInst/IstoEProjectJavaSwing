package timePicker.time.event;

import lombok.Getter;

import java.util.EventObject;

@Getter
public class TimeSelectionModelEvent extends EventObject {

    public static final int HOUR = 1;
    public static final int MINUTE = 2;
    public static final int HOUR_MINUTE = 3;

    protected int action;

    public TimeSelectionModelEvent(Object source, int action) {
        super(source);
        this.action = action;
    }

}
