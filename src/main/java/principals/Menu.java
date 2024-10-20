package principals;



import principals.panels.*;
import principals.panels.pernoitePanels.PernoitePanel;
import principals.panels.reservasPanels.ReservationPanel;
import principals.tools.Icones;
import principals.tools.LabelArredondado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Menu extends JFrame {

    private final JPanel mainPanel;
    private final Color defaultColor = new Color(66, 75, 152);

    public Menu() {
        setTitle("ISTO E POUSADA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(600, 600));
        setLayout(new BorderLayout());
        setVisible(true);

        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setBackground(defaultColor);
        sideMenu.setPreferredSize(new Dimension(230, getHeight()));

        JPanel employeeInfoPanel = createEmployeeInfoPanel();
        sideMenu.add(employeeInfoPanel);
        sideMenu.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnDashboard = createMenuButton("Dashboard", resizeIcon(Icones.dashboard, 24, 24));
        JButton btnQuartos = createMenuButton("Quartos", resizeIcon(Icones.quartos, 24, 24));
        JButton btnEntradas = createMenuButton("Entradas", resizeIcon(Icones.entradas, 24, 24));
        JButton btnPernoites = createMenuButton("Pernoites", resizeIcon(Icones.pernoites, 24, 24));
        JButton btnRelatorio = createMenuButton("Relatorio", resizeIcon(Icones.relatorios, 24, 24));
        JButton btnClientes = createMenuButton("Clientes", resizeIcon(Icones.clientes, 24, 24));
        JButton btnItens = createMenuButton("Itens",  resizeIcon(Icones.itens, 24, 24));
        JButton btnReservas = createMenuButton("Reservas", resizeIcon(Icones.reservas, 24, 24));
        JButton btnPrice = createMenuButton("Precos",  resizeIcon(Icones.preco, 24, 24));

        sideMenu.add(btnDashboard);
        sideMenu.add(btnQuartos);
        sideMenu.add(btnEntradas);
        sideMenu.add(btnPernoites);
        sideMenu.add(btnRelatorio);
        sideMenu.add(btnClientes);
        sideMenu.add(btnItens);
        sideMenu.add(btnReservas);
        sideMenu.add(btnPrice);
        sideMenu.add(Box.createVerticalGlue());

        mainPanel = new JPanel();
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setLayout(new CardLayout());

        add(sideMenu, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        addHoverAndSelectionEffect(btnDashboard, new DashBoardPanel());
        addHoverAndSelectionEffect(btnQuartos, new RoomsPanel());
        addHoverAndSelectionEffect(btnEntradas, new EntryPanel());
//
        addHoverAndSelectionEffect(btnRelatorio, new RelatoriosPanel());
        addHoverAndSelectionEffect(btnClientes, new CustomersPanel());
        addHoverAndSelectionEffect(btnItens, new ItensPanel());
        addHoverAndSelectionEffect(btnReservas, new ReservationPanel());
        addHoverAndSelectionEffect(btnPrice, new PricePanel());

        btnPernoites.addActionListener(a-> addHoverAndSelectionEffect(btnPernoites, new PernoitePanel()));

        showPanel(new PernoitePanel());
        setVisible(true);
    }



    private JPanel createEmployeeInfoPanel() {
        JPanel employeePanel = new JPanel();
        employeePanel.setLayout(new BoxLayout(employeePanel, BoxLayout.Y_AXIS));
        employeePanel.setBackground(new Color(66, 75, 152));
        employeePanel.setMaximumSize(new Dimension(500, 150));
        employeePanel.setPreferredSize(new Dimension(250, 150));
        employeePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        ImageIcon icon = new ImageIcon(new ImageIcon("src/main/resources/icons/menu/user.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        JLabel labelIcon = new JLabel(icon);
        labelIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelName = new JLabel("Sam Helson");
        labelName.setForeground(Color.WHITE);
        labelName.setFont(new Font("Inter", Font.BOLD, 16));
        labelName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelRole = new JLabel("Funcionário");
        labelRole.setForeground(Color.WHITE);
        labelRole.setFont(new Font("Inter", Font.PLAIN, 12));
        labelRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        employeePanel.add(labelIcon);
        employeePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        employeePanel.add(labelName);
        employeePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        employeePanel.add(labelRole);

        return employeePanel;
    }



    private JButton createMenuButton(String text, ImageIcon icon) {
        JButton button = new JButton(text, icon);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(defaultColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Inter", Font.BOLD, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(15);
        button.setMinimumSize(new Dimension(800, 75));
        button.setMaximumSize(new Dimension(500, 70));
        button.setAlignmentX(10);

        return button;
    }

    private static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private void addHoverAndSelectionEffect(JButton button, JPanel panel) {

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(new Font("Inter", Font.BOLD, 25));
                button.setBackground(new Color(70, 130, 180));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(new Font("Inter", Font.BOLD, 20));
                button.setBackground(defaultColor);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel(panel);
                resetButtonColors();
                button.setBackground(new Color(100, 75, 237));
            }
        });
    }

    private void resetButtonColors() {
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                for (Component button : ((JPanel) comp).getComponents()) {
                    if (button instanceof JButton) {
                        button.setBackground(defaultColor);
                    }
                }
            }
        }
    }

    private void showPanel(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }





    public static JPanel createIdentificadorPanel(String titulo, ImageIcon icon) {
        JPanel identificadorPanel = new JPanel();
        identificadorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        identificadorPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));

        LabelArredondado labelTitulo = new LabelArredondado(titulo);
        labelTitulo.setIcon(resizeIcon(icon, 40, 40));
        labelTitulo.setFont(new Font("Inter", Font.BOLD, 25));
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setOpaque(true);
        labelTitulo.setBackground(new Color(66, 75, 152));
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        labelTitulo.setIconTextGap(10);

        identificadorPanel.add(labelTitulo);

        return identificadorPanel;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu::new);
    }
}
