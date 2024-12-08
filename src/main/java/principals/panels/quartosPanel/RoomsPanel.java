package principals.panels.quartosPanel;

import principals.tools.Icones;
import repository.QuartosRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class RoomsPanel extends JPanel {


    public RoomsPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Apartamentos", Icones.quartos);
        identificadorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.setPreferredSize(new Dimension(125, 40));
        btnAdicionar.addActionListener(e -> {});

        identificadorPanel.add(btnAdicionar, BorderLayout.EAST);
        topPanel.add(identificadorPanel, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);

        JPanel quartoPanel = new QuartoIndividualJPanel().mainPanel();

        JScrollPane scrollPane = new JScrollPane(quartoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        adjustGridLayoutColumns(quartoPanel);
    }

    private void adjustGridLayoutColumns(JPanel quartoPanel) {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int buttonWidth = 250 + 20;
                int columns = Math.max(1, width / buttonWidth);

                if (quartoPanel.getLayout() instanceof GridLayout) {
                    GridLayout layout = (GridLayout) quartoPanel.getLayout();
                    layout.setColumns(columns);
                    layout.setRows(0);
                    quartoPanel.revalidate();
                }
            }
        });
    }


}



