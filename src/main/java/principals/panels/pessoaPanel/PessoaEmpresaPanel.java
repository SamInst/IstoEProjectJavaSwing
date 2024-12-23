package principals.panels.pessoaPanel;

import principals.tools.BotaoArredondado;
import principals.tools.PanelArredondado;
import principals.tools.Refreshable;
import repository.PessoaRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PessoaEmpresaPanel extends JPanel implements Refreshable {

    public PessoaEmpresaPanel() {
        initializePanel();
    }

    private void initializePanel() {
        PessoaRepository pessoaRepository = new PessoaRepository();

//        var pessoasCadastradas = pessoaRepository.buscarTodasAsPessoasComPaginacao()

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.setPreferredSize(new Dimension(125, 40));
        btnAdicionar.addActionListener(e -> {});

        topPanel.add(btnAdicionar, BorderLayout.WEST);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;

        PanelArredondado leftPanel = new PanelArredondado();
        leftPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setLayout(new GridBagLayout());

        JScrollPane leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        configureLeftPanel(leftPanel);
        addHeaderToLeftPanel(leftPanel);

        PanelArredondado rightPanel = new PanelArredondado();
        rightPanel.setBackground(Color.GRAY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(leftScrollPane, gbc);

        gbc.gridx = 1;
        mainPanel.add(rightPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void configureLeftPanel(PanelArredondado leftPanel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        BotaoArredondado blocoSuperior = new BotaoArredondado("");
        blocoSuperior.setPreferredSize(new Dimension(0, 200));
        blocoSuperior.setBackground(Color.GREEN);
        blocoSuperior.setBorderPainted(false);
        blocoSuperior.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        blocoSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(blocoSuperior, gbc);

        for (int i = 0; i < 40; i++) {
            BotaoArredondado blocoPessoaButton = new BotaoArredondado("");
            blocoPessoaButton.setPreferredSize(new Dimension(0, 110));
            blocoPessoaButton.setBackground(Color.RED);
            blocoPessoaButton.setBorderPainted(false);
            blocoPessoaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            blocoPessoaButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            blocoPessoaButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((BotaoArredondado) e.getSource()).setShowBorder(true, Color.GREEN);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((BotaoArredondado) e.getSource()).setShowBorder(false, Color.WHITE);
                }
            });

            gbc.gridy = i;
            leftPanel.add(blocoPessoaButton, gbc);
        }
    }

    private void addHeaderToLeftPanel(PanelArredondado leftPanel) {
        JLabel headerLabel = new JLabel("Header for Left Panel", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(Color.GREEN);
        headerLabel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        leftPanel.add(headerLabel, gbc);
    }


    @Override
    public void refreshPanel() {
        removeAll();
        initializePanel();
        revalidate();
        repaint();
    }
}
