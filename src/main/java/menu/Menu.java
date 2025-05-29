package menu;

import lombok.SneakyThrows;
import menu.panels.DashBoardPanel;
import menu.panels.EntryPanel;
import menu.panels.ItensPanel;
import menu.panels.PricePanel;
import menu.panels.pernoitePanels.NewPernoitesPanel;
import menu.panels.pessoaPanel.PessoaEmpresaPanel;
import menu.panels.quartosPanel.RoomsPanel;
import menu.panels.relatoriosPanels.RelatoriosPanel;
import menu.panels.reservasPanel.ReservasPanel;
import repository.PernoitesRepository;
import repository.QuartosRepository;
import repository.RelatoriosRepository;
import tools.LabelArredondado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

import static tools.Icones.*;
import static tools.Resize.resizeIcon;

public class Menu extends JFrame {
    private final JPanel mainPanel;
    private final Color defaultColor = new Color(66, 75, 152);

    @SneakyThrows
    public Menu(String username) {
        setTitle("ISTO É POUSADA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(logo.getImage());
        setMinimumSize(new Dimension(600, 600));
        setLayout(new BorderLayout());
        setVisible(true);

        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setBackground(defaultColor);
        sideMenu.setPreferredSize(new Dimension(230, getHeight()));

        JPanel employeeInfoPanel = createEmployeeInfoPanel(username);
        sideMenu.add(employeeInfoPanel);
        sideMenu.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnDashboard = createMenuButton("Dashboard", resizeIcon(dashboard, 24, 24));
        JButton btnQuartos = createMenuButton("Apartamentos", resizeIcon(quartos, 24, 24));
        JButton btnEntradas = createMenuButton("Entradas", resizeIcon(entradas, 24, 24));
        JButton btnPernoites = createMenuButton("Pernoites", resizeIcon(pernoites, 24, 24));
        JButton btnRelatorio = createMenuButton("Relatorio", resizeIcon(relatorios, 24, 24));
        JButton btnClientes = createMenuButton("Clientes", resizeIcon(clientes, 24, 24));
        JButton btnItens = createMenuButton("Itens",  resizeIcon(itens, 24, 24));
        JButton btnReservas = createMenuButton("Reservas", resizeIcon(reservas, 24, 24));
        JButton btnPrice = createMenuButton("Precos",  resizeIcon(preco, 24, 24));

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

        addHoverAndSelectionEffect(btnDashboard, DashBoardPanel::new);
        addHoverAndSelectionEffect(btnQuartos, () -> new RoomsPanel(new QuartosRepository()));
        addHoverAndSelectionEffect(btnEntradas, EntryPanel::new);
        addHoverAndSelectionEffect(btnPernoites, ()-> new NewPernoitesPanel(new PernoitesRepository()));
        addHoverAndSelectionEffect(btnRelatorio, () -> new RelatoriosPanel(new RelatoriosRepository()));
        addHoverAndSelectionEffect(btnClientes, PessoaEmpresaPanel::new);
        addHoverAndSelectionEffect(btnItens, ItensPanel::new);
        addHoverAndSelectionEffect(btnReservas, ReservasPanel::new);
        addHoverAndSelectionEffect(btnPrice, PricePanel::new);

        showPanel(new ReservasPanel());
        setVisible(true);
    }



    private JPanel createEmployeeInfoPanel(String userName) {
        JPanel employeePanel = new JPanel();
        employeePanel.setLayout(new BoxLayout(employeePanel, BoxLayout.Y_AXIS));
        employeePanel.setBackground(new Color(66, 75, 152));
        employeePanel.setMaximumSize(new Dimension(500, 150));
        employeePanel.setPreferredSize(new Dimension(250, 150));
        employeePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel labelIcon = new JLabel(resizeIcon(user_funcionario, 60,60));
        labelIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelName = new JLabel(userName);
        labelName.setForeground(Color.WHITE);
        labelName.setFont(new Font("Roboto", Font.BOLD, 16));
        labelName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelRole = new JLabel("Funcionário");
        labelRole.setForeground(Color.WHITE);
        labelRole.setFont(new Font("Roboto", Font.PLAIN, 12));
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
        button.setFont(new Font("Roboto", Font.BOLD, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(15);
        button.setMinimumSize(new Dimension(800, 75));
        button.setMaximumSize(new Dimension(500, 70));
        button.setAlignmentX(10);

        return button;
    }


    private void addHoverAndSelectionEffect(JButton button, Supplier<JPanel> panelSupplier) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel(panelSupplier.get());
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

    public void showPanel(JPanel panel) {
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
        labelTitulo.setFont(new Font("Roboto", Font.BOLD, 25));
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setOpaque(true);
        labelTitulo.setBackground(new Color(66, 75, 152));
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        labelTitulo.setIconTextGap(10);

        identificadorPanel.add(labelTitulo);

        return identificadorPanel;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu("Sam Helson"));
    }
}
