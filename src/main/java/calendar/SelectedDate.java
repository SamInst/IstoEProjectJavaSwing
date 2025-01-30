package calendar;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SelectedDate {
    public SelectedDate() {}

    private int day;
    private int month;
    private int year;
}
