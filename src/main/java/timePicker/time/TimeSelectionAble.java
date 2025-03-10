package timePicker.time;

import java.time.LocalTime;

public interface TimeSelectionAble {

    boolean isTimeSelectedAble(LocalTime time, boolean hourView);
}
