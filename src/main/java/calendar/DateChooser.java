package calendar;

import buttons.ShadowButton;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static tools.Icones.forward;
import static tools.Icones.previous;

public final class DateChooser extends javax.swing.JPanel {

    public void addEventDateChooser(EventDateChooser event) {
        events.add(event);
    }

    @Getter
    private ShadowButton textReference;
    @Setter
    @Getter
    private String dateFormat = "dd-MM-yyyy";
    private final String[] MONTH_ENGLISH = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
    private int MONTH = 1;
    private int YEAR = 2021;
    private int DAY = 1;
    private int STATUS = 1;
    private int startYear;
    @Getter
    private SelectedDate selectedDate = new SelectedDate();
    private List<EventDateChooser> events;

    public DateChooser() {
        initComponents();
        execute();
    }

    private void execute() {
        setForeground(new Color(204, 93, 93));
        events = new ArrayList<>();
        popup.add(this);
        toDay(false);
    }

    public void setTextReference(ShadowButton txt, boolean showdate) {
        this.textReference = txt;
        this.textReference.setFocusPainted(false);
        this.textReference.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (textReference.isEnabled()) {
                    showPopup();
                }
            }
        });
        setText(false, 0);
    }

    private void setText(boolean runEvent, int act) {
        if (textReference != null) {
//            try {
//                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//                Date date = df.parse(DAY + "-" + MONTH + "-" + YEAR);
//                textReference.setText(new SimpleDateFormat(dateFormat).format(date));
//            } catch (ParseException e) {
//
//            }
        }
        if (runEvent) {
            runEvent(act);
        }
    }

    private void runEvent(int act) {
        SelectedAction action = new SelectedAction() {
            @Override
            public int getAction() {
                return act;
            }
        };
        for (EventDateChooser event : events) {
            event.dateSelected(action, selectedDate);
        }
    }

    private Event getEventDay(Dates dates) {
        return (MouseEvent evt, int num) -> {
            dates.clearSelected();
            dates.setSelected(num);
            DAY = num;
            selectedDate.setDay(DAY);
            selectedDate.setMonth(MONTH);
            selectedDate.setYear(YEAR);
            setText(true, 1);
            if (evt != null && evt.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(evt)) {
                popup.setVisible(false);
            }
        };
    }

    private Event getEventMonth() {
        return (MouseEvent evt, int num) -> {
            MONTH = num;
            selectedDate.setDay(DAY);
            selectedDate.setMonth(MONTH);
            selectedDate.setYear(YEAR);
            setText(true, 2);
            Dates d = new Dates();
            d.setForeground(getForeground());
            d.setEvent(getEventDay(d));
            d.showDate(MONTH, YEAR, selectedDate);
            if (slide.slide(d, Slider.Direction.DOWN)) {
                cmdMonth.setText(MONTH_ENGLISH[MONTH - 1]);
                cmdYear.setText(YEAR + "");
                STATUS = 1;
            }
        };
    }

    private Event getEventYear() {
        return (MouseEvent evt, int num) -> {
            YEAR = num;
            selectedDate.setDay(DAY);
            selectedDate.setMonth(MONTH);
            selectedDate.setYear(YEAR);
            setText(true, 3);
            Months d = new Months();
            d.setEvent(getEventMonth());
            if (slide.slide(d, Slider.Direction.DOWN)) {
                cmdMonth.setText(MONTH_ENGLISH[MONTH - 1]);
                cmdYear.setText(YEAR + "");
                STATUS = 2;
            }
        };
    }

    private void toDay(boolean runEvent) {
        Dates dates = new Dates();
        dates.setForeground(getForeground());
        dates.setEvent(getEventDay(dates));
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String toDay = df.format(date);
        DAY = Integer.parseInt(toDay.split("-")[0]);
        MONTH = Integer.parseInt(toDay.split("-")[1]);
        YEAR = Integer.parseInt(toDay.split("-")[2]);
        selectedDate.setDay(DAY);
        selectedDate.setMonth(MONTH);
        selectedDate.setYear(YEAR);
        dates.showDate(MONTH, YEAR, selectedDate);
        slide.slideNon(dates);
        cmdMonth.setText(MONTH_ENGLISH[MONTH - 1]);
        cmdYear.setText(YEAR + "");
        setText(runEvent, 0);
    }

    private void setDateNext() {
        Dates dates = new Dates();
        dates.setForeground(getForeground());
        dates.setEvent(getEventDay(dates));
        dates.showDate(MONTH, YEAR, selectedDate);
        if (slide.slide(dates, Slider.Direction.LEFT)) {
            cmdMonth.setText(MONTH_ENGLISH[MONTH - 1]);
            cmdYear.setText(YEAR + "");
        }
    }

    private void setDateBack() {
        Dates dates = new Dates();
        dates.setForeground(getForeground());
        dates.setEvent(getEventDay(dates));
        dates.showDate(MONTH, YEAR, selectedDate);
        if (slide.slide(dates, Slider.Direction.RIGHT)) {
            cmdMonth.setText(MONTH_ENGLISH[MONTH - 1]);
            cmdYear.setText(YEAR + "");
        }
    }

    public void setYearNext() {
        Years years = new Years();
        years.setEvent(getEventYear());
        startYear = years.next(startYear);
        slide.slide(years, Slider.Direction.LEFT);
    }

    private void setYearBack() {
        if (startYear >= 1000) {
            Years years = new Years();
            years.setEvent(getEventYear());
            startYear = years.back(startYear);
            slide.slide(years, Slider.Direction.LEFT);
        }
    }

    public void showPopup() {
        popup.show(textReference, 0, textReference.getHeight());
    }

    public void hidePopup() {
        popup.setVisible(false);
    }

    private void initComponents() {

        popup = new javax.swing.JPopupMenu(){
            @Override
            protected void paintComponent(Graphics grphcs) {
                grphcs.setColor(new Color(114, 113, 113));
                grphcs.fillRect(0, 0, getWidth(), getHeight());
                grphcs.setColor(Color.WHITE);
                grphcs.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
            }
        };
        header = new javax.swing.JPanel();
        cmdForward = new Button();
        MY = new javax.swing.JLayeredPane();
        cmdMonth = new Button();
        lb = new javax.swing.JLabel();
        cmdYear = new Button();
        cmdPrevious = new Button();
        slide = new Slider();

        setBackground(new Color(255, 255, 255));

        header.setBackground(new Color(204, 93, 93));
        header.setMaximumSize(new java.awt.Dimension(262, 40));

        cmdForward.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmdForward.setIcon(forward);
        cmdForward.setFocusable(true);
        cmdForward.setPaintBackground(false);
        cmdForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdForwardActionPerformed(evt);
            }
        });

        FlowLayout flowLayout1 = new FlowLayout(FlowLayout.CENTER, 5, 0);
        flowLayout1.setAlignOnBaseline(true);
        MY.setLayout(flowLayout1);

        cmdMonth.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmdMonth.setForeground(new Color(255, 255, 255));
        cmdMonth.setText("January");
        cmdMonth.setFocusPainted(false);
        cmdMonth.setFont(new java.awt.Font("Kh Content", Font.PLAIN, 14)); // NOI18N
        cmdMonth.setPaintBackground(false);
        cmdMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdMonthActionPerformed(evt);
            }
        });
        MY.add(cmdMonth);

        lb.setForeground(new Color(255, 255, 255));
        lb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb.setText("-");
        MY.add(lb);

        cmdYear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmdYear.setForeground(new Color(255, 255, 255));
        cmdYear.setText("2018");
        cmdYear.setFocusPainted(false);
        cmdYear.setFont(new java.awt.Font("Kh Content", 0, 14)); // NOI18N
        cmdYear.setPaintBackground(false);
        cmdYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdYearActionPerformed(evt);
            }
        });
        MY.add(cmdYear);

        cmdPrevious.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmdPrevious.setIcon(previous);
        cmdPrevious.setFocusable(true);
        cmdPrevious.setPaintBackground(false);
        cmdPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPreviousActionPerformed(evt);
            }
        });
        cmdPrevious.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                cmdPreviousKeyPressed(evt);
            }
        });

        GroupLayout headerLayout = new GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, headerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmdPrevious, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MY, GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdForward, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, headerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(cmdPrevious, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(MY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdForward, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        slide.setLayout(new javax.swing.BoxLayout(slide, javax.swing.BoxLayout.LINE_AXIS));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(slide, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(header, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(header, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(slide, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    private void cmdPreviousActionPerformed(java.awt.event.ActionEvent evt) {
        if (STATUS == 1) {
            if (MONTH == 1) {
                MONTH = 12;
                YEAR--;
            } else {
                MONTH--;
            }
            setDateBack();
        } else if (STATUS == 3) {
            setYearBack();
        } else {
            if (YEAR >= 1000) {
                YEAR--;
                Months months = new Months();
                months.setEvent(getEventMonth());
                slide.slide(months, Slider.Direction.LEFT);
                cmdYear.setText(YEAR + "");
            }
        }
    }


    private void cmdForwardActionPerformed(java.awt.event.ActionEvent evt) {
        if (STATUS == 1) {
            if (MONTH == 12) {
                MONTH = 1;
                YEAR++;
            } else {
                MONTH++;
            }
            setDateNext();
        } else if (STATUS == 3) {
            setYearNext();
        } else {
            YEAR++;
            Months months = new Months();
            months.setEvent(getEventMonth());
            slide.slide(months, Slider.Direction.LEFT);
            cmdYear.setText(YEAR + "");
        }
    }


    private void cmdMonthActionPerformed(java.awt.event.ActionEvent evt) {
        if (STATUS != 2) {
            STATUS = 2;
            Months months = new Months();
            months.setEvent(getEventMonth());
            slide.slide(new Months(), Slider.Direction.DOWN);
        } else {
            Dates dates = new Dates();
            dates.setForeground(getForeground());
            dates.setEvent(getEventDay(dates));
            dates.showDate(MONTH, YEAR, selectedDate);
            slide.slide(new Dates(), Slider.Direction.DOWN);
            STATUS = 1;
        }
    }


    private void cmdYearActionPerformed(java.awt.event.ActionEvent evt) {
        if (STATUS != 3) {
            STATUS = 3;
            Years years = new Years();
            years.setEvent(getEventYear());
            startYear = years.showYear(YEAR);
            slide.slide(years, Slider.Direction.DOWN);
        } else {
            Dates dates = new Dates();
            dates.setForeground(getForeground());
            dates.setEvent(getEventDay(dates));
            dates.showDate(MONTH, YEAR, selectedDate);
            slide.slide(dates, Slider.Direction.DOWN);
            STATUS = 1;
        }
    }


    private void cmdPreviousKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            Component com = slide.getComponent(0);
            if (com instanceof Dates d) {
                d.up();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            Component com = slide.getComponent(0);
            if (com instanceof Dates d) {
                d.down();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            Component com = slide.getComponent(0);
            if (com instanceof Dates d) {
                d.back();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            Component com = slide.getComponent(0);
            if (com instanceof Dates d) {
                d.next();
            }
        }
    }

    private javax.swing.JLayeredPane MY;
    private Button cmdForward;
    private Button cmdMonth;
    private Button cmdPrevious;
    private Button cmdYear;
    private javax.swing.JPanel header;
    private javax.swing.JLabel lb;
    private javax.swing.JPopupMenu popup;
    private Slider slide;

    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        if (header != null) {
            header.setBackground(color);
            toDay(false);
        }
    }
}
