package principals.tools;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDayChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

public class CustomJCalendar {

    private static final Color CINZA_CLARO = new Color(240, 240, 240);
    private static final Color CINZA_ESCURO = new Color(0x696363);
    private static final Color AZUL_ESCURO = new Color(0x424B98);
    private static final Color VERDE_ESCURO = new Color(0x148A20);
    private static final Color VERMELHO = new Color(0xF85A5A);

    private JButton selectedButton = null;  // Variável para manter o botão selecionado

    public JCalendar createCustomCalendar() {
        // Criar o JCalendar
        JCalendar jCalendar = new JCalendar();

        // Definir o idioma para português
        jCalendar.setLocale(new Locale("pt", "BR"));

        // Aumentar o tamanho do JCalendar
        jCalendar.setPreferredSize(new Dimension(300, 200));  // Aumentar o tamanho do calendário

        // Evento para tratar a troca de mês e garantir o reset das cores e eventos
        jCalendar.getMonthChooser().addPropertyChangeListener("month", evt -> applyButtonStyles(jCalendar.getDayChooser()));

        // Aplicar os estilos iniciais
        applyButtonStyles(jCalendar.getDayChooser());

        // Retornar o calendário customizado
        return jCalendar;
    }

    // Método para aplicar o estilo de hover e seleção nos botões de dias
    private void applyButtonStyles(JDayChooser dayChooser) {
        // Painel de dias do JCalendar
        JPanel daysPanel = dayChooser.getDayPanel();

        // Resetar todos os botões para seu estado inicial
        for (Component component : daysPanel.getComponents()) {
            if (component instanceof JButton) {
                JButton dayButton = (JButton) component;
                dayButton.setFont(new Font("Inter", Font.BOLD, 16));
                dayButton.setForeground(CINZA_ESCURO);
                dayButton.setBackground(Color.WHITE); // Inicialmente, todos os botões em branco
                dayButton.setBorder(BorderFactory.createEmptyBorder()); // Remover bordas
                dayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // Adicionar efeito hover ao passar o mouse (mudança de cor)
                dayButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // Mudar a cor ao passar o mouse apenas se o botão não estiver selecionado
                        if (!dayButton.equals(selectedButton)) {  // Apenas mudar a cor se não estiver selecionado
                            dayButton.setBackground(new Color(0x424B98));  // Cor ao passar o mouse
                            dayButton.setForeground(Color.WHITE);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        // Restaurar a cor original ao sair, se não estiver selecionado
                        if (!dayButton.equals(selectedButton)) {  // Restaurar a cor original se não estiver selecionado
                            dayButton.setBackground(Color.WHITE);  // Reseta ao branco
                            dayButton.setForeground(CINZA_ESCURO);
                        }
                    }
                });

                // Garantir que a seleção restaura a cor correta
                dayButton.addActionListener(e -> {
                    resetAllButtons(daysPanel);
                });
            }
        }
    }

    // Método para resetar todos os botões para branco
    private void resetAllButtons(JPanel daysPanel) {
        for (Component component : daysPanel.getComponents()) {
            if (component instanceof JButton dayButton) {
                dayButton.setBackground(Color.WHITE);  // Reseta todos os botões para branco
            }
        }
        selectedButton = null;  // Limpa o botão selecionado anterior
    }

}
