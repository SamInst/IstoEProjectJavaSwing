package principals.panels.pernoitePanels;

import enums.StatusPernoiteEnum;
import principals.tools.*;
import repository.PernoitesRepository;
import response.BuscaPernoiteResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

import static buttons.Botoes.*;
import static principals.tools.Resize.*;

public class BlocosPernoitesAtivos {

    public JPanel blocoPernoitesAtivos(JPanel statusPanel, PernoitesRepository pernoitesRepository, StatusPernoiteEnum statusPernoiteEnum) {
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String statusTitulo = "";
        Color cor = CorPersonalizada.AZUL_ESCURO;
        switch (statusPernoiteEnum) {
        case ATIVO -> statusTitulo = "Ativos";
        case DIARIA_ENCERRADA -> { statusTitulo = "Diária Encerrada"; cor = new Color(0xA83131);}
            default -> { statusTitulo = "Finalizados"; cor = CorPersonalizada.CINZA_ESCURO;
            }
        }

        JLabel tituloStatus = new JLabel(statusTitulo);
        tituloStatus.setForeground(Color.white);
        tituloStatus.setFont(new Font("Roboto", Font.BOLD, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cor);
        headerPanel.add(tituloStatus, BorderLayout.WEST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        statusPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel pernoitesPanel = new JPanel();
        pernoitesPanel.setLayout(new GridLayout(0, 2, 10, 10));
        pernoitesPanel.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 10));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(pernoitesPanel, BorderLayout.CENTER);

        statusPanel.add(mainPanel, BorderLayout.CENTER);

        Color finalCor = cor;
        pernoitesRepository.buscaPernoitesPorStatus(statusPernoiteEnum).forEach(pernoite -> {
            BotaoArredondado pernoiteButton = new BotaoArredondado("");
            pernoiteButton.setLayout(new BorderLayout());
            pernoiteButton.setPreferredSize(new Dimension(0, 90));
            pernoiteButton.setMinimumSize(new Dimension(0, 90));
            pernoiteButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            pernoiteButton.setBackground(Color.WHITE);
            pernoiteButton.setBorderPainted(false);
            pernoiteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            pernoiteButton.addActionListener(e ->
                    new BuscaPernoiteIndividual().buscaPernoiteIndividual(pernoitesRepository.buscaPernoite(pernoite.pernoite_id()))
            );

            JPanel panelCentral = new JPanel();
            panelCentral.setBackground(Color.WHITE);
            panelCentral.setLayout(new BorderLayout());
            panelCentral.setMinimumSize(new Dimension(0, 40));
            panelCentral.setPreferredSize(new Dimension(100, 150));
            panelCentral.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

            pernoiteButton.add(panelCentral);

            panelCentral.add(painelQuarto(new JPanel(), pernoite, finalCor), BorderLayout.WEST);

            JPanel painelSuperiorInferior = new JPanel();
            painelSuperiorInferior.setBackground(Color.BLUE);
            painelSuperiorInferior.setLayout(new GridLayout(2, 1));
            panelCentral.add(painelSuperiorInferior, BorderLayout.CENTER);

            painelSuperiorInferior.add(painelSuperior(new JPanel(), pernoite));

            JPanel quadranteAzul = new JPanel();
            quadranteAzul.setLayout(new BorderLayout());
            quadranteAzul.setBackground(CorPersonalizada.AZUL_ESCURO);
            quadranteAzul.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            painelSuperiorInferior.add(quadranteAzul);

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.setOpaque(false);

            var icone_calendario = resizeIcon(Icones.calendario, 15, 15);

            var saida = btn_branco(pernoite.data_saida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            saida.setIcon(icone_calendario);

            var entrada = btn_branco(pernoite.data_entrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            entrada.setIcon(icone_calendario);

            leftPanel.add(entrada);
            leftPanel.add(saida);

            quadranteAzul.add(leftPanel, BorderLayout.WEST);

            var btnTotal = btn_verde("R$ " + FormatarFloat.format(pernoite.valor_total()));

            quadranteAzul.add(btnTotal, BorderLayout.EAST);

            pernoitesPanel.add(pernoiteButton);

            pernoiteButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((BotaoArredondado) e.getSource()).setShowBorder(true, finalCor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((BotaoArredondado) e.getSource()).setShowBorder(false, Color.WHITE);
                }
            });

        });

        statusPanel.add(pernoitesPanel, BorderLayout.CENTER);

        return statusPanel;
    }


    public JPanel painelSuperior(JPanel panelSuperior, BuscaPernoiteResponse pernoite) {
        panelSuperior.setLayout(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));

        ImageIcon iconePessoa = resizeIcon(Icones.usuarios, 20, 20);
        JLabel iconePessoaLabel = new JLabel(iconePessoa);

        JLabel labelNome = new JLabel(" " + (pernoite.representante() == null ? null : pernoite.representante().nome()));
        labelNome.setForeground(CorPersonalizada.VERMELHO.darker());
        labelNome.setFont(new Font("Roboto", Font.BOLD, 18));

        JPanel panelEsquerdo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEsquerdo.setBackground(Color.WHITE);
        panelEsquerdo.add(iconePessoaLabel);
        panelEsquerdo.add(labelNome);

        JPanel panelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDireito.setBackground(Color.WHITE);

        ImageIcon iconeDiarias = resizeIcon(Icones.diarias_quantidade, 20, 20);
        JLabel labelDiarias = new JLabel(iconeDiarias);

        JLabel labelQuantidadeDiarias = new JLabel(pernoite.quantidade_diarias().toString());
        labelQuantidadeDiarias.setFont(new Font("Roboto", Font.BOLD, 20));
        labelQuantidadeDiarias.setForeground(CorPersonalizada.CINZA_ESCURO);
        labelQuantidadeDiarias.setToolTipText("quantidade de diarias");

        ImageIcon iconeConsumo = resizeIcon(Icones.sacola, 20, 20);
        JLabel labelConsumo = new JLabel(iconeConsumo);
        labelConsumo.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JLabel labelQuantidadeConsumo = new JLabel(pernoite.quantidade_consumo().toString());
        labelQuantidadeConsumo.setFont(new Font("Roboto", Font.BOLD, 20));
        labelQuantidadeConsumo.setForeground(CorPersonalizada.CINZA_ESCURO);
        labelQuantidadeConsumo.setToolTipText("quantidade de itens consumidos");

        ImageIcon iconePessoas = resizeIcon(Icones.usuarios, 20, 20);
        JLabel labelPessoas = new JLabel(iconePessoas);
        labelPessoas.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JLabel labelQuantidadePessoas = new JLabel(pernoite.quantidade_pessoas().toString());
        labelQuantidadePessoas.setFont(new Font("Roboto", Font.BOLD, 20));
        labelQuantidadePessoas.setForeground(CorPersonalizada.CINZA_ESCURO);
        labelQuantidadePessoas.setToolTipText("quantidade de pessoas");

        panelDireito.add(labelDiarias);
        panelDireito.add(labelQuantidadeDiarias);
        panelDireito.add(labelConsumo);
        panelDireito.add(labelQuantidadeConsumo);
        panelDireito.add(labelPessoas);
        panelDireito.add(labelQuantidadePessoas);

        panelSuperior.add(panelEsquerdo, BorderLayout.WEST);
        panelSuperior.add(panelDireito, BorderLayout.EAST);

        return panelSuperior;
    }

    public JPanel painelQuarto(JPanel painelQuarto, BuscaPernoiteResponse pernoite, Color finalCor) {
        painelQuarto.setBackground(Color.WHITE);

        JButton botaoQuarto = new BotaoArredondado(pernoite.quarto() < 10L ? "0" + pernoite.quarto() : pernoite.quarto().toString());
        botaoQuarto.setLayout(null);
        botaoQuarto.setPreferredSize(new Dimension(80, 70));
        botaoQuarto.setBackground(finalCor);
        botaoQuarto.setForeground(Color.WHITE);
        botaoQuarto.setFont(new Font("Roboto", Font.BOLD, 40));

        JPanel panelQUarto = new JPanel();
        panelQUarto.setBackground(Color.WHITE);
        panelQUarto.setLayout(new BorderLayout());
        panelQUarto.setPreferredSize(new Dimension(90, 80));
        panelQUarto.setMinimumSize(new Dimension(90, 80));
        panelQUarto.setMaximumSize(new Dimension(90, 80));
        panelQUarto.add(botaoQuarto, BorderLayout.CENTER);

        painelQuarto.add(botaoQuarto, BorderLayout.CENTER);

        return painelQuarto;
    }
}
