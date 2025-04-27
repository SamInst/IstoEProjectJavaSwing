package menu.panels.reservasPanel;

import request.BuscaReservasResponse;

import javax.swing.*;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

class AnimationManager {
    public double previousDiariasValue = 0.0;
    public double previousValorDiariaValue = 0.0;
    public double previousTotalValue = 0.0;

    public void animateDiariasLabel(JLabel label, double newValue) {
        if (previousDiariasValue == newValue) return;
        animateLabelSpin(label, previousDiariasValue, newValue, false);
        previousDiariasValue = newValue;
    }

    public void animateValorDiariaLabel(JLabel label, double newValue) {
        if (previousValorDiariaValue == newValue) return;
        animateLabelSpin(label, previousValorDiariaValue, newValue, true);
        previousValorDiariaValue = newValue;
    }

    public void animateTotalLabel(JLabel label, double newValue) {
        if (previousTotalValue == newValue) return;
        animateLabelSpin(label, previousTotalValue, newValue, true);
        previousTotalValue = newValue;
    }

    private void animateLabelSpin(JLabel label, double oldValue, double newValue, boolean isMoney) {
        final boolean goingUp = (newValue > oldValue);
        final int totalSteps = 25;
        final double step = (newValue - oldValue) / totalSteps;
        final int delayMs = 30;
        final double[] currentValue = {oldValue};

        Timer timer = new Timer(delayMs, null);
        timer.addActionListener(e -> {
            currentValue[0] += step;
            boolean acabou = goingUp ? (currentValue[0] >= newValue) : (currentValue[0] <= newValue);
            if (acabou) {
                currentValue[0] = newValue;
                timer.stop();
            }
            label.setText(isMoney ? String.format("R$ %.2f", currentValue[0]) : String.format("%.0f", currentValue[0]));
        });

        timer.start();
    }

    public double parseLabelValue(String text) {
        text = text.replace("R$", "").replace(",", ".").trim();
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }
}