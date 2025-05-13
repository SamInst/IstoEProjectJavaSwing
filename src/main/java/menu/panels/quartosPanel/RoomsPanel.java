package menu.panels.quartosPanel;

import buttons.ShadowButton;
import lateralMenu.tabbed.TabbedForm;
import repository.QuartosRepository;
import tools.Refreshable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static buttons.Botoes.btn_verde;

public class RoomsPanel extends TabbedForm implements Refreshable {
    private final QuartosRepository quartosRepository;
    ShadowButton btnAdicionar = new ShadowButton();

    public RoomsPanel(QuartosRepository quartosRepository) {
        this.quartosRepository = quartosRepository;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel identificadorPanel = new JPanel();
        identificadorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

        btnAdicionar = btn_verde("Adicionar Quarto");

        identificadorPanel.add(btnAdicionar, BorderLayout.EAST);
        topPanel.add(identificadorPanel, BorderLayout.NORTH);

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



