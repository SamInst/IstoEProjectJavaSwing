package principals.panels;

import com.toedter.calendar.JCalendar;
import principals.panels.reservasPanels.ReservaCalendarioCustomizado;
import principals.tools.CustomJCalendar;

import javax.swing.*;

public class ReservasPanel {

    public JPanel jPanel (){
        ReservaCalendarioCustomizado customJCalendar = new ReservaCalendarioCustomizado();
        JCalendar jCalendar = customJCalendar.createCustomCalendar();

        JPanel painelPesquisa = new JPanel();
        painelPesquisa.add(jCalendar);
        return painelPesquisa;
    }
}
