package calendar2;

import java.time.LocalTime;

public interface TimeSelectionAble {

    boolean isTimeSelectedAble(LocalTime time, boolean hourView);
}
