package menu.panels.quartosPanel;

import tools.Icones;
import tools.Refreshable;
import repository.QuartosRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoomsPanel extends JPanel implements Refreshable {
    private final QuartosRepository quartosRepository;
    public RoomsPanel(QuartosRepository quartosRepository1) {
        this.quartosRepository = quartosRepository1;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel identificadorPanel = menu.Menu.createIdentificadorPanel("Apartamentos", Icones.quartos);
        identificadorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.setPreferredSize(new Dimension(125, 40));
        btnAdicionar.addActionListener(e -> {});

        identificadorPanel.add(btnAdicionar, BorderLayout.EAST);
        topPanel.add(identificadorPanel, BorderLayout.NORTH);


        btnAdicionar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new AdicionarQuartoFrame(quartosRepository, null, RoomsPanel.this);
            }
        });

        add(topPanel, BorderLayout.NORTH);

        JPanel quartoPanel = new ListaDeQuartosJPanel().mainPanel(quartosRepository, RoomsPanel.this);

        JScrollPane scrollPane = new JScrollPane(quartoPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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


    @Override
    public void refreshPanel() {
        removeAll();
        initializePanel();
        revalidate();
        repaint();
    }


}



