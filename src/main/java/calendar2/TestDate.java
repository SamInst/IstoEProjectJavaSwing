package calendar2;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestDate extends TestFrame {

    private final DatePicker datePicker;

    public TestDate() {
        setLayout(new MigLayout("wrap"));
        datePicker = new DatePicker();
        datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);

        datePicker.addDateSelectionListener(dateEvent -> {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate[] dates = datePicker.getSelectedDateRange();
            System.out.println("date change " + df.format(dates[0]) + " to " + df.format(dates[1]));
        });

        datePicker.setDateSelectionAble((date) -> !date.isBefore(LocalDate.now()));
        datePicker.setAnimationEnabled(true);

        datePicker.now();
        JFormattedTextField editor = new JFormattedTextField();
        datePicker.setEditor(editor);
        add(editor, "width 250");
    }

    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("themes");
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new TestDate().setVisible(true));
    }
}
