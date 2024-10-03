package principals.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;

public class Botoes {
    public static ImageIcon dashboard_icon = new ImageIcon("src/main/resources/icons/menu/dashboard.png");
    public static ImageIcon quartos_icon = new ImageIcon("src/main/resources/icons/menu/rooms.png");
    public static ImageIcon entradas_icon = new ImageIcon("src/main/resources/icons/menu/entry.png");
    public static ImageIcon pernoites_icon = new ImageIcon("src/main/resources/icons/menu/overnight.png");
    public static ImageIcon relatorios_icon = new ImageIcon("src/main/resources/icons/menu/reports.png");
    public static ImageIcon clientes_icon = new ImageIcon("src/main/resources/icons/menu/customers.png");
    public static ImageIcon itens_icon = new ImageIcon("src/main/resources/icons/menu/itens.png");
    public static ImageIcon reservations_icon = new ImageIcon("src/main/resources/icons/menu/reservations.png");
    public static ImageIcon price_icon = new ImageIcon("src/main/resources/icons/menu/price.png");

    public static JButton botaoEstilizado(String titulo, int tamanhoTitulo, String pathIcon, int larguraIcone, int alturaIcone, Color color) {
        // Carregar o ícone do botão a partir do caminho
        ImageIcon icone = new ImageIcon(pathIcon);

        // Criar o botão e definir o ícone redimensionado
        JButton btnAdicionar = new JButton(titulo, Tool.resizeIcon(icone, larguraIcone, alturaIcone));
        btnAdicionar.setFont(new Font("Inter", Font.BOLD, tamanhoTitulo)); // Fonte do texto
        btnAdicionar.setForeground(Color.WHITE); // Cor do texto
        btnAdicionar.setBackground(color); // Cor de fundo personalizada
        btnAdicionar.setFocusPainted(false); // Remove o foco visual
        btnAdicionar.setBorderPainted(false); // Remove a borda padrão
        btnAdicionar.setContentAreaFilled(false); // Remove o preenchimento padrão
        btnAdicionar.setOpaque(false); // Tornar o botão transparente para controle personalizado de fundo
        btnAdicionar.setHorizontalTextPosition(SwingConstants.RIGHT); // Texto à direita do ícone
        btnAdicionar.setIconTextGap(7); // Espaço entre ícone e texto
        btnAdicionar.setMargin(new Insets(4, 10, 5, 10)); // Definir margens internas

        // Estilizando o botão com bordas arredondadas
        btnAdicionar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2), // Borda branca ao redor
                BorderFactory.createEmptyBorder(5, 5, 5, 15) // Espaçamento interno
        ));

        // Customizando a UI do botão para bordas arredondadas
        btnAdicionar.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Suavização

                // Cor de fundo personalizada
                g2.setColor(btnAdicionar.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40); // Desenha o fundo arredondado

                // Desenhar a borda branca arredondada
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 40, 40); // Borda arredondada com raio 40

                // Desenhar o texto e o ícone como parte do botão padrão
                super.paint(g, c);
            }
        });

        Color hoverColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 178); // 70% opaco (30% transparente)
        btnAdicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdicionar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAdicionar.setBackground(hoverColor); // Cor ao passar o mouse
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAdicionar.setBackground(color); // Cor normal quando o mouse sai
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Salvar ícone original e texto do botão
                String originalText = btnAdicionar.getText();
                Icon originalIcon = btnAdicionar.getIcon();

                // Definir o GIF animado como ícone do botão (exemplo: "loading.gif")
                Icon loadingIcon = resizeGif("src/main/resources/icons/loading2.gif", larguraIcone, alturaIcone); // Ajuste o caminho e tamanho
//                btnAdicionar.setText("Carregando...");
                btnAdicionar.setIcon(loadingIcon);
                btnAdicionar.setEnabled(false); // Desativar o botão temporariamente

                // Simular uma tarefa de carregamento (ou executar uma tarefa real)
                Executors.newSingleThreadExecutor().submit(() -> {
                    try {
                        Thread.sleep(3000); // Simular um carregamento de 3 segundos
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } finally {
                        // Voltar ao estado original após o carregamento
                        SwingUtilities.invokeLater(() -> {
                            btnAdicionar.setText(originalText);
                            btnAdicionar.setIcon(originalIcon);
                            btnAdicionar.setEnabled(true); // Reativar o botão
                        });
                    }
                });
            }

        });

        return btnAdicionar; // Retornar o botão estilizado
    }

    private static ImageIcon resizeGif(String gifPath, int width, int height) {
        // Carregar a imagem GIF
        ImageIcon gifIcon = new ImageIcon(gifPath);

        // Redimensionar a imagem
        Image gifImage = gifIcon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);

        // Retornar o novo ícone com o tamanho redefinido
        return new ImageIcon(gifImage);
    }
}
