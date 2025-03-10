package menu.panels.quartosPanel;

import buttons.ShadowButton;
import repository.QuartosRepository;
import response.QuartoResponse;
import tools.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static buttons.Botoes.btn_backgroung;
import static buttons.Botoes.btn_branco;
import static java.lang.String.valueOf;
import static tools.CorPersonalizada.BACKGROUND_GRAY;
import static tools.CorPersonalizada.WHITE;
import static tools.Icones.*;
import static tools.Resize.resizeIcon;

public class ListaDeQuartosJPanel {
    int largura = 15;
    int altura = 15;
    ShadowButton ocupado = btn_branco("OCUPADO");
    ShadowButton disponivel = btn_branco("DISPONIVEL");
    ShadowButton reservado = btn_branco("RESERVADO");
    ShadowButton manutencao = btn_branco("MANUTENCAO");
    ShadowButton diaria_encerrada = btn_branco("DIARIA ENCERRADA");
    ShadowButton limpeza = btn_branco("LIMPEZA");

    public JPanel mainPanel(QuartosRepository quartosRepository, RoomsPanel roomsPanel) {
        var quartos = quartosRepository.buscaTodosOsQuartos();
        quartos.sort(Comparator.comparingLong(QuartoResponse::quarto_id));

        JPanel quartoBackgroundPanel = new JPanel();
        quartoBackgroundPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
        quartoBackgroundPanel.setBackground(BACKGROUND_GRAY);
        quartoBackgroundPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,0));

        for (QuartoResponse quarto : quartos) {
            var numero = quarto.quarto_id();
            var status_quarto = quarto.status_quarto_enum();
            var checkin = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            var checkout = LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            var nome_pessoa = "Pessoa Teste";
            var categoria = quarto.categoria();

            ShadowButton quartoButton = new ShadowButton();
            quartoButton.setPreferredSize(new Dimension(400, 150));
            quartoButton.setBorderPainted(false);
            quartoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            quartoButton.setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setPreferredSize(new Dimension(405, 40));
            topPanel.setOpaque(true);
            topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 10));

            switch (status_quarto) {
                case OCUPADO: topPanel.setBackground(new Color(0xC9625A));
                    break;
                case DISPONIVEL: topPanel.setBackground(new Color(0x18A68C));
                    break;
                case RESERVADO: topPanel.setBackground(new Color(0xFEE189));
                    break;
                case LIMPEZA: topPanel.setBackground(Color.ORANGE);
                    break;
                case DIARIA_ENCERRADA: topPanel.setBackground(Color.BLUE);
                    break;
                case MANUTENCAO: topPanel.setBackground(Color.GRAY);
                    break;
                default: topPanel.setBackground(Color.WHITE);
            }

            JLabel numeroLabel = new JLabel("Quarto " + (numero < 10 ? "0" + numero : numero));
            numeroLabel.setForeground(Color.WHITE);
            numeroLabel.setFont(new Font("Inter", Font.PLAIN, 16));
            numeroLabel.setVerticalAlignment(SwingConstants.CENTER);
            numeroLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel numeroPanel = new JPanel();
            numeroPanel.setPreferredSize(new Dimension(70,45));
            numeroPanel.setBackground(topPanel.getBackground());
            numeroPanel.add(numeroLabel, BorderLayout.WEST);

            topPanel.add(numeroPanel, BorderLayout.WEST);

            ShadowButton statusButton = btn_branco(status_quarto.toString());
            statusButton.enableHoverEffect();
            statusButton.setIcon(resizeIcon(select, largura, altura));

            topPanel.add(statusButton, BorderLayout.EAST);

            quartoButton.add(topPanel, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setOpaque(false);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 5));

            var nomeLabel = btn_branco(" " + nome_pessoa);
            nomeLabel.setIcon(resizeIcon(usuarios, largura, altura));
            nomeLabel.setFont(new Font("Inter", Font.PLAIN, 14));
            nomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            var datasLabel = btn_branco(" " + checkin + " - " + checkout);
            datasLabel.setIcon(resizeIcon(calendario, largura, altura));
            datasLabel.setFont(new Font("Inter", Font.PLAIN, 14));
            datasLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            switch (status_quarto) {
                case OCUPADO:
                    contentPanel.add(nomeLabel);
                    contentPanel.add(datasLabel);
                    break;

                case DISPONIVEL:
                    JPanel panelUp = new JPanel();
                    panelUp.setLayout(new BoxLayout(panelUp, BoxLayout.X_AXIS));
                    panelUp.add(cama_casal(quarto.qtd_cama_casal()));
                    panelUp.add(cama_solteiro(quarto.qtd_cama_solteiro()));
                    panelUp.add(beliche(quarto.qtd_cama_beliche()));
                    panelUp.add(rede(quarto.qtd_rede()));
                    panelUp.setBackground(WHITE);

                    JPanel panelDown = new JPanel();
                    panelDown.setBackground(WHITE);
                    panelDown.setLayout(new BoxLayout(panelDown, BoxLayout.X_AXIS));

                    JLabel categoriaLabel = new JLabel("Categoria: ");
                    panelDown.add(categoriaLabel);
                    panelDown.add(btn_backgroung(categoria.categoria()));

                    contentPanel.add(panelUp, BorderLayout.WEST);
                    contentPanel.add(panelDown, BorderLayout.EAST);
                    statusButton.addActionListener(e-> {
                        statusButton.showPopupWithButtons(ocupado, disponivel, reservado);

                    });
                    break;

                case RESERVADO:
                    contentPanel.add(nomeLabel);
                    contentPanel.add(datasLabel);
                    break;
            }

            quartoButton.add(contentPanel, BorderLayout.CENTER);
            quartoBackgroundPanel.add(quartoButton);
        }
        return quartoBackgroundPanel;
    }

    public ShadowButton rede(int quantidade){
        var btn = btn_branco(valueOf(quantidade));
        btn.setIcon(resizeIcon(rede, largura, altura));
        return btn;
    }

    public ShadowButton cama_casal(int quantidade){
        var btn = btn_branco(valueOf(quantidade));
        btn.setIcon(resizeIcon(cama_casal, largura, altura));
        return btn;
    }

    public ShadowButton cama_solteiro(int quantidade){
        var btn = btn_branco(valueOf(quantidade));
        btn.setIcon(resizeIcon(cama_solteiro, largura, altura));
        return btn;
    }

    public ShadowButton beliche(int quantidade){
        var btn = btn_branco(valueOf(quantidade));
        btn.setIcon(resizeIcon(beliche, largura, altura));
        return btn;
    }
}

