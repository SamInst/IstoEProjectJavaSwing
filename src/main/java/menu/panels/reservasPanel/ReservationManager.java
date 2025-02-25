package menu.panels.reservasPanel;

import tools.Refreshable;

import java.util.ArrayList;
import java.util.List;

public class ReservationManager {
    private static final List<Refreshable> observers = new ArrayList<>();

    public static void addObserver(Refreshable observer) {
        observers.add(observer);
    }

    public static void notifyObservers() {
        observers.forEach(Refreshable::refreshPanel);
    }
}
