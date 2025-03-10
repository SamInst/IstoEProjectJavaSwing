package timePicker;

import calendar2.TestFrame;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import net.miginfocom.swing.MigLayout;
import timePicker.time.TimePicker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TestTime extends TestFrame {

    private final TimePicker timePicker;

    public TestTime() {
        setLayout(new MigLayout("wrap"));
        timePicker = new TimePicker();
        timePicker.setOrientation(SwingConstants.VERTICAL);
        timePicker.set24HourView(true);
        timePicker.addTimeSelectionListener(timeEvent -> {
            if (timePicker.isTimeSelected()) {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("hh:mm");
                System.out.println("event selected : " + timePicker.getSelectedTime().format(df));
            } else {
                System.out.println("event selected : null");
            }
        });
        timePicker.now();

        JFormattedTextField editor = new JFormattedTextField();
        timePicker.setEditor(editor);
        add(editor, "width 200");
//        createTimeOption();
    }

    private void createTimeOption() {
        JPanel panel = new JPanel(new MigLayout("wrap", "[150]"));
        panel.setBorder(new TitledBorder("Option"));
        JCheckBox ch24hourView = new JCheckBox("24 hour view");
        JCheckBox chHorizontal = new JCheckBox("Horizontal");
        JCheckBox chDisablePast = new JCheckBox("Disable past");
        JCheckBox chEditorValidation = new JCheckBox("Editor validation", timePicker.isEditorValidation());
        JCheckBox chValidationOnNull = new JCheckBox("Validation on null");
        ch24hourView.addActionListener(e -> {
            timePicker.set24HourView(ch24hourView.isSelected());
        });
        chHorizontal.addActionListener(e -> timePicker.setOrientation(chHorizontal.isSelected() ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL));
        chDisablePast.addActionListener(e -> {
            boolean disable = chDisablePast.isSelected();
            if (disable) {
                disablePast();
            } else {
                timePicker.setTimeSelectionAble(null);
            }
        });
        chEditorValidation.addActionListener(e -> {
            timePicker.setEditorValidation(chEditorValidation.isSelected());
            chValidationOnNull.setEnabled(chEditorValidation.isSelected());
        });

        chValidationOnNull.addActionListener(e -> timePicker.setValidationOnNull(chValidationOnNull.isSelected()));

        panel.add(ch24hourView);
        panel.add(chHorizontal);
        panel.add(chDisablePast);
        panel.add(chEditorValidation);
        panel.add(chValidationOnNull);
        add(panel);
    }

    private void disablePast() {
        timePicker.setTimeSelectionAble((time, hourView) -> {
            LocalTime now = LocalTime.now().withSecond(0).withNano(0);
            if (hourView) {
                return time.getHour() >= now.getHour();
            }
            return !time.isBefore(now);
        });
    }

    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("themes");
        FlatMacLightLaf.setup();
        EventQueue.invokeLater(() -> new TestTime().setVisible(true));
    }
}
