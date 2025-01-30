package calendar;

import javax.swing.*;
import java.awt.*;

public class Slider extends JPanel {

    private boolean sliding = false;

    public Slider() {
        initComponents();
    }

    public boolean slide(Component com, Direction direction) {
        if (sliding) return false;

        sliding = true;
        new Thread(() -> {
            if (getComponentCount() > 0) {
                Component old = getComponent(0);
                add(com);
                com.setLocation(getStartPosition(direction));
                slideComponent(com, old, direction);
            } else {
                add(com);
                finalizeSlide(com, null);
            }
        }).start();

        return true;
    }

    private Point getStartPosition(Direction direction) {
        return switch (direction) {
            case LEFT -> new Point(getWidth(), 0);
            case RIGHT -> new Point(-getWidth(), 0);
            case UP -> new Point(0, getHeight());
            case DOWN -> new Point(0, -getHeight());
        };
    }

    private void slideComponent(Component com, Component old, Direction direction) {
        int steps = (direction == Direction.LEFT || direction == Direction.RIGHT ? getWidth() : getHeight()) / 50;
        Point endPosition = new Point(0, 0);
        Point oldEndPosition = direction == Direction.LEFT ? new Point(-getWidth(), 0) :
                direction == Direction.RIGHT ? new Point(getWidth(), 0) :
                        direction == Direction.UP ? new Point(0, -getHeight()) :
                                new Point(0, getHeight());

        for (int i = 0; i <= steps; i++) {
            com.setLocation(getInterpolatedPoint(getStartPosition(direction), endPosition, i, steps));
            old.setLocation(getInterpolatedPoint(new Point(0, 0), oldEndPosition, i, steps));
            sleep();
        }
        finalizeSlide(com, old);
    }

    private Point getInterpolatedPoint(Point start, Point end, int step, int totalSteps) {
        double fraction = (double) step / totalSteps;
        int x = (int) (start.x + fraction * (end.x - start.x));
        int y = (int) (start.y + fraction * (end.y - start.y));
        return new Point(x, y);
    }

    private void finalizeSlide(Component com, Component old) {
        com.setLocation(0, 0);
        if (old != null) remove(old);
        repaint();
        revalidate();
        sliding = false;
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {}
    }

    public void slideNon(Component com) {
        removeAll();
        add(com);
        repaint();
        revalidate();
    }

    private void initComponents() {
        setBackground(new Color(255, 255, 255));
        setLayout(new BorderLayout());
    }

    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
}
