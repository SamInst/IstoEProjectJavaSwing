package principals;



import principals.panels.*;
import principals.tools.Botoes;

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
        setLayout(new BorderLayout());
        setVisible(true);

        // Menu lateral (painel esquerdo)
        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setBackground(defaultColor);
        sideMenu.setPreferredSize(new Dimension(230, getHeight()));  // Ajustar largura do painel lateral

        // Caixa de informações do funcionário
        JPanel employeeInfoPanel = createEmployeeInfoPanel();
        sideMenu.add(employeeInfoPanel);
        sideMenu.add(Box.createRigidArea(new Dimension(0, 15)));  // Espaço entre o painel do funcionário e os botões


        // Botões do menu com ícones e fonte ajustada
        JButton btnDashboard = createMenuButton("Dashboard", resizeIcon(Botoes.dashboard_icon, 24, 24));
        JButton btnQuartos = createMenuButton("Quartos", resizeIcon(Botoes.quartos_icon, 24, 24));
        JButton btnEntradas = createMenuButton("Entradas", resizeIcon(Botoes.entradas_icon, 24, 24));
        JButton btnPernoites = createMenuButton("Pernoites", resizeIcon(Botoes.pernoites_icon, 24, 24));
        JButton btnRelatorio = createMenuButton("Relatorio", resizeIcon(Botoes.relatorios_icon, 24, 24));
        JButton btnClientes = createMenuButton("Clientes", resizeIcon(Botoes.clientes_icon, 24, 24));
        JButton btnItens = createMenuButton("Itens",  resizeIcon(Botoes.itens_icon, 24, 24));
        JButton btnReservas = createMenuButton("Reservas", resizeIcon(Botoes.reservations_icon, 24, 24));
        JButton btnPrice = createMenuButton("Precos",  resizeIcon(Botoes.price_icon, 24, 24));

        // Adiciona botões ao menu
        sideMenu.add(btnDashboard);
        sideMenu.add(btnQuartos);
        sideMenu.add(btnEntradas);
        sideMenu.add(btnPernoites);
        sideMenu.add(btnRelatorio);
        sideMenu.add(btnClientes);
        sideMenu.add(btnItens);
        sideMenu.add(btnReservas);
        sideMenu.add(btnPrice);

        // Adiciona espaço flexível para ajustar o layout
        sideMenu.add(Box.createVerticalGlue());

        // Painel principal para trocar as telas
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setLayout(new CardLayout());  // Usando CardLayout para trocar as telas

        // Adicionando os componentes à janela principal
        add(sideMenu, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Efeito de hover e seleção para os botões
        addHoverAndSelectionEffect(btnDashboard, new DashBoardPanel());
        addHoverAndSelectionEffect(btnQuartos, new RoomsPanel());
        addHoverAndSelectionEffect(btnEntradas, new EntryPanel());
        addHoverAndSelectionEffect(btnPernoites, new OvernightPanel());
        addHoverAndSelectionEffect(btnRelatorio, new RelatoriosPanel());
        addHoverAndSelectionEffect(btnClientes, new CustomersPanel());
        addHoverAndSelectionEffect(btnItens, new ItensPanel());
        addHoverAndSelectionEffect(btnReservas, new ReservationPanel());
        addHoverAndSelectionEffect(btnPrice, new PricePanel());

        showPanel(new DashBoardPanel());
        setVisible(true);
    }

    private JPanel createEmployeeInfoPanel() {
        JPanel employeePanel = new JPanel();
        employeePanel.setLayout(new BoxLayout(employeePanel, BoxLayout.Y_AXIS));  // Centralizando o conteúdo verticalmente
        employeePanel.setBackground(new Color(66, 75, 152));

        // Ajustar o tamanho do painel para coincidir com o menu lateral
        employeePanel.setMaximumSize(new Dimension(500, 150));  // Definir a largura para 250 pixels (igual ao menu lateral)
        employeePanel.setPreferredSize(new Dimension(250, 150));  // Definir a largura preferida para 250 pixels

        // Adicionar um espaçamento no topo
        employeePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));  // Adiciona 20px de espaço no topo

        // Ícone do funcionário
        ImageIcon icon = new ImageIcon(new ImageIcon("src/main/resources/icons/menu/user.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        JLabel labelIcon = new JLabel(icon);
        labelIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nome do funcionário
        JLabel labelName = new JLabel("Sam Helson");
        labelName.setForeground(Color.WHITE);
        labelName.setFont(new Font("Inter", Font.BOLD, 16));
        labelName.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centralizar nome

        // Cargo do funcionário
        JLabel labelRole = new JLabel("Funcionário");
        labelRole.setForeground(Color.WHITE);
        labelRole.setFont(new Font("Inter", Font.PLAIN, 12));
        labelRole.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centralizar cargo

        // Adicionando os componentes ao painel
        employeePanel.add(labelIcon);
        employeePanel.add(Box.createRigidArea(new Dimension(0, 10)));  // Espaço entre ícone e nome
        employeePanel.add(labelName);
        employeePanel.add(Box.createRigidArea(new Dimension(0, 5)));   // Espaço entre nome e cargo
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
        button.setHorizontalAlignment(SwingConstants.LEFT);  // Alinha o conteúdo à esquerda
        button.setHorizontalTextPosition(SwingConstants.RIGHT);  // Texto à direita do ícone
        button.setIconTextGap(15);  // Espaçamento entre o ícone e o texto
        button.setMinimumSize(new Dimension(800, 75));
        button.setMaximumSize(new Dimension(500, 70));
        button.setAlignmentX(10);

        return button;
    }

    // Método para redimensionar o ícone
    private static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);  // Retorna o ícone redimensionado
    }

    private void addHoverAndSelectionEffect(JButton button, JPanel panel) {

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(new Font("Inter", Font.BOLD, 25));
                button.setBackground(new Color(70, 130, 180));  // Cor de hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(new Font("Inter", Font.BOLD, 20));
                button.setBackground(defaultColor);  // Retorna à cor padrão
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel(panel);  // Mostra o painel correspondente
                resetButtonColors();
                button.setBackground(new Color(100, 75, 237));  // Cor de seleção
            }
        });
    }

    private void resetButtonColors() {
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                for (Component button : ((JPanel) comp).getComponents()) {
                    if (button instanceof JButton) {
                        button.setBackground(defaultColor);  // Reseta a cor dos botões
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
        identificadorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  // Alinhar à esquerda
        identificadorPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        identificadorPanel.setBackground(Color.BLUE);  // Cor de fundo do painel principal

        // Criação do JLabel com ícone e texto dentro do mesmo componente
        JLabel labelTitulo = new JLabel(titulo, resizeIcon(icon, 50, 50), JLabel.LEFT);
        labelTitulo.setFont(new Font("Inter", Font.BOLD, 30));
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setOpaque(true);
        labelTitulo.setBackground(new Color(66, 75, 152));  // Cor de fundo
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));  // Espaçamento interno
        labelTitulo.setIconTextGap(10);  // Espaço entre ícone e texto

        // Adicionando o JLabel ao painel
        identificadorPanel.add(labelTitulo);

        return identificadorPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu::new);
    }
}
