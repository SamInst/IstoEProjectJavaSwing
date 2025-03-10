package timePicker.time;

import lombok.Getter;
import lombok.Setter;
import timePicker.time.event.TimeSelectionModelEvent;
import timePicker.time.event.TimeSelectionModelListener;

import javax.swing.event.EventListenerList;
import java.time.LocalTime;

public class TimeSelectionModel {
    protected EventListenerList listenerList = new EventListenerList();
    @Setter
    @Getter
    private TimeSelectionAble timeSelectionAble;
    @Getter
    private int hour = -1;
    @Getter
    private int minute = -1;

    public TimeSelectionModel() {
    }

    public void setHour(int hour) {
        if (!checkSelection(hour, minute)) {
            return;
        }
        if (this.hour != hour) {
            this.hour = hour;
            fireTimePickerChanged(new TimeSelectionModelEvent(this, TimeSelectionModelEvent.HOUR));
        }
    }

    public void setMinute(int minute) {
        if (hour == -1) {
            set(getDefaultSelectionHour(minute), minute);
        } else {
            if (this.minute != minute) {
                if (!checkSelection(hour, minute)) {
                    return;
                }
                this.minute = minute;
                fireTimePickerChanged(new TimeSelectionModelEvent(this, TimeSelectionModelEvent.MINUTE));
            }
        }
    }

    public boolean isSelected() {
        return hour != -1 && minute != -1;
    }

    public LocalTime getTime() {
        if (isSelected()) {
            return LocalTime.of(hour, minute);
        }
        return null;
    }

    public void set(int hour, int minute) {
        int action = 0;
        if (this.hour != hour && this.minute != minute) {
            action = TimeSelectionModelEvent.HOUR_MINUTE;
        } else if (this.hour != hour) {
            action = TimeSelectionModelEvent.HOUR;
        } else if (this.minute != minute) {
            action = TimeSelectionModelEvent.MINUTE;
        }
        if (action != 0) {
            if (!checkSelection(hour, minute)) {
                return;
            }
            this.hour = hour;
            this.minute = minute;
            fireTimePickerChanged(new TimeSelectionModelEvent(this, action));
        }
    }

    public int getDefaultSelectionHour(int minute) {
        int defaultHour = 0;
        if (timeSelectionAble != null) {
            defaultHour = getAvailableDefaultHour(defaultHour, minute);
        }
        return defaultHour;
    }

    private int getAvailableDefaultHour(int startHour, int minute) {
        int hour = startHour;
        for (int i = 0; i < 23; i++) {
            if (checkSelection(hour, minute)) {
                return hour;
            }
            hour++;
            if (hour == 24) {
                hour = 0;
            }
        }
        return startHour;
    }

    private int getDefaultMinuteCheck() {
        return 0;
    }

    public boolean checkSelection(int hour, int minute) {
        if (timeSelectionAble == null || hour == -1 || (minute == -1 && hour == -1)) {
            return true;
        }

        boolean hourView = false;
        if (minute == -1) {
            minute = getDefaultMinuteCheck();
            hourView = true;
        }
        return timeSelectionAble.isTimeSelectedAble(LocalTime.of(hour, minute), hourView);
    }

    public void addTimePickerSelectionListener(TimeSelectionModelListener listener) {
        listenerList.add(TimeSelectionModelListener.class, listener);
    }

    public void removeTimePickerSelectionListener(TimeSelectionModelListener listener) {
        listenerList.remove(TimeSelectionModelListener.class, listener);
    }

    public void fireTimePickerChanged(TimeSelectionModelEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TimeSelectionModelListener.class) {
                ((TimeSelectionModelListener) listeners[i + 1]).timeSelectionModelChanged(event);
            }
        }
    }
}
