package principals.panels.pernoitesSubPanels;

import enums.StatusPernoiteEnum;
import principals.tools.BotaoArredondado;
import principals.tools.Cor;
import principals.tools.Icones;
import repository.PernoitesRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static principals.tools.Tool.resizeIcon;

public class BlocosPernoitesAtivos {

    public JPanel blocoPernoitesAtivos(JPanel statusPanel, PernoitesRepository pernoitesRepository, StatusPernoiteEnum statusPernoiteEnum) {
        statusPanel.setBackground(Color.white);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String statusTitulo = "";
        Color cor = Cor.VERDE_ESCURO;
        switch (statusPernoiteEnum) {
        case ATIVO -> statusTitulo = "Ativo";
            case DIARIA_ENCERRADA -> {
                statusTitulo = "Diária Encerrada";
                cor = new Color(0xA83131);
            }
            default -> {
                statusTitulo = "Finalizados";
                cor = Cor.CINZA_ESCURO;
            }
        }

        JLabel tituloStatus = new JLabel(statusTitulo);
        tituloStatus.setForeground(Color.white);
        tituloStatus.setFont(new Font("Inter", Font.BOLD, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cor);
        headerPanel.add(tituloStatus, BorderLayout.WEST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        statusPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel pernoitesPanel = new JPanel();
        pernoitesPanel.setLayout(new GridLayout(0, 2, 10, 10));
        pernoitesPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        Color finalCor = cor;
        pernoitesRepository.buscaPernoitesPorStatus(statusPernoiteEnum).forEach(pernoite -> {
            BotaoArredondado pernoiteButton = new BotaoArredondado("");
            pernoiteButton.setLayout(null);
            pernoiteButton.setPreferredSize(new Dimension(810, 95));
            pernoiteButton.setBackground(Color.WHITE);
            pernoiteButton.setBorderPainted(false);
            pernoiteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            pernoiteButton.addActionListener(e ->
                    new BuscaPernoiteIndividual().buscaPernoiteIndividual(pernoitesRepository.buscaPernoite(pernoite.pernoite_id()))
            );

            BotaoArredondado botaoQuarto = new BotaoArredondado(pernoite.quarto().toString());
            botaoQuarto.setLayout(null);
            botaoQuarto.setPreferredSize(new Dimension(60, 40));
            botaoQuarto.setBackground(finalCor);
            botaoQuarto.setForeground(Color.WHITE);
            botaoQuarto.setFont(new Font("Inter", Font.BOLD, 40));
            botaoQuarto.setBounds(10, 10, 80, 75);
            pernoiteButton.add(botaoQuarto);

            JPanel blocoInfoPanelSuperior = new JPanel(new BorderLayout());
            blocoInfoPanelSuperior.setBackground(Color.WHITE);
            blocoInfoPanelSuperior.setBounds(100, 5, 695, 37);
            blocoInfoPanelSuperior.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 5));
            pernoiteButton.add(blocoInfoPanelSuperior);

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.setOpaque(false);

            JLabel idPernoiteLabel = new JLabel("#" + pernoite.pernoite_id());
            idPernoiteLabel.setFont(new Font("Inter", Font.BOLD, 17));
            idPernoiteLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
            idPernoiteLabel.setForeground(Color.RED);

            leftPanel.add(idPernoiteLabel);

            ImageIcon iconeCalendario = resizeIcon(Icones.calendario, 20, 20);
            JLabel labelCalendario = new JLabel(iconeCalendario);
            labelCalendario.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

            JLabel labelDataEntrada = new JLabel(pernoite.data_entrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            labelDataEntrada.setToolTipText("Data de entrada");
            labelDataEntrada.setFont(new Font("Inter", Font.BOLD, 20));
            labelDataEntrada.setForeground(new Color(0xF5841B));
            labelDataEntrada.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));

            JLabel labelDataSaida = new JLabel(pernoite.data_saida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            labelDataSaida.setToolTipText("Data de saida");
            labelDataSaida.setForeground(new Color(0xF5841B));
            labelDataSaida.setFont(new Font("Inter", Font.BOLD, 20));

            leftPanel.add(labelCalendario);
            leftPanel.add(labelDataEntrada);
            leftPanel.add(labelDataSaida);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            rightPanel.setOpaque(false);

            var diariaAtual = Period.between(pernoite.data_entrada(), LocalDate.now()).getDays();

            JLabel diariaAtualIcon = new JLabel(resizeIcon(Icones.diaria_atual_laranja, 20, 18));
            diariaAtualIcon.setToolTipText("Diaria atual");
            JLabel totalTextoLabel = new JLabel(diariaAtual + "  ");
            totalTextoLabel.setToolTipText("Diaria atual");
            totalTextoLabel.setFont(new Font("Inter", Font.BOLD, 23));
            totalTextoLabel.setForeground(new Color(0xF5841B));

            NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);

            JLabel totalLabel = new JLabel("R$ " + nf.format(pernoite.valor_total()));
            totalLabel.setFont(new Font("Inter", Font.BOLD, 23));
            totalLabel.setForeground(Cor.VERDE_ESCURO);

            if (pernoite.status_pernoite().equals("0")) {
                rightPanel.add(diariaAtualIcon);
                rightPanel.add(totalTextoLabel);
            }

            rightPanel.add(totalLabel, BorderLayout.EAST);

            blocoInfoPanelSuperior.add(leftPanel, BorderLayout.WEST);
            blocoInfoPanelSuperior.add(rightPanel, BorderLayout.EAST);

            JPanel blocoInfoPanelInferior = new JPanel(new BorderLayout());
            blocoInfoPanelInferior.setBackground(Color.WHITE);
            blocoInfoPanelInferior.setBounds(100, 47, 695, 38);
            pernoiteButton.add(blocoInfoPanelInferior);

            JPanel painelEsquerdo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            painelEsquerdo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            painelEsquerdo.setOpaque(false);

            ImageIcon iconePessoa = resizeIcon(Icones.usuarios, 20, 20);
            JLabel iconePessoaLabel = new JLabel(iconePessoa);

            JLabel labelNumero = new JLabel("#"+pernoite.representante().id().toString());
            labelNumero.setForeground(Color.RED);
            labelNumero.setBorder(BorderFactory.createEmptyBorder(0, 33, 0, 10));
            labelNumero.setFont(new Font("Inter", Font.BOLD, 17));
            labelNumero.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel labelNome = new JLabel(pernoite.representante().nome() + "   "+ pernoite.representante().telefone());
            labelNome.setForeground(Cor.CINZA_ESCURO);
            labelNome.setFont(new Font("Inter", Font.BOLD, 17));

            painelEsquerdo.add(iconePessoaLabel);
            painelEsquerdo.add(labelNumero);
            painelEsquerdo.add(labelNome);




            JPanel painelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            painelDireito.setOpaque(false);

            ImageIcon iconeDiarias = resizeIcon(Icones.diarias_quantidade, 20, 20);
            JLabel labelDiarias = new JLabel(iconeDiarias);

            JLabel labelQuantidadeDiarias = new JLabel(pernoite.quantidade_diarias().toString());
            labelQuantidadeDiarias.setFont(new Font("Inter", Font.BOLD, 20));
            labelQuantidadeDiarias.setForeground(Cor.CINZA_ESCURO);
            labelQuantidadeDiarias.setToolTipText("quantidade de diarias");

            ImageIcon iconeConsumo = resizeIcon(Icones.sacola, 20, 20);

            JLabel labelConsumo = new JLabel(iconeConsumo);
            labelConsumo.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

            JLabel labelQuantidadeConsumo = new JLabel(pernoite.quantidade_consumo().toString());
            labelQuantidadeConsumo.setFont(new Font("Inter", Font.BOLD, 20));
            labelQuantidadeConsumo.setForeground(Cor.CINZA_ESCURO);
            labelQuantidadeConsumo.setToolTipText("quantidade de itens consumidos");

            ImageIcon iconePessoas = resizeIcon(Icones.usuarios, 20, 20);

            JLabel labelPessoas = new JLabel(iconePessoas);
            labelPessoas.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

            JLabel labelQuantidadePessoas = new JLabel(pernoite.quantidade_pessoas().toString());
            labelQuantidadePessoas.setFont(new Font("Inter", Font.BOLD, 20));
            labelQuantidadePessoas.setForeground(Cor.CINZA_ESCURO);
            labelQuantidadePessoas.setToolTipText("quantidade de pessoas");

            painelDireito.add(labelDiarias);
            painelDireito.add(labelQuantidadeDiarias);
            painelDireito.add(labelConsumo);
            painelDireito.add(labelQuantidadeConsumo);
            painelDireito.add(labelPessoas);
            painelDireito.add(labelQuantidadePessoas);

            blocoInfoPanelInferior.add(painelEsquerdo, BorderLayout.WEST);
            blocoInfoPanelInferior.add(painelDireito, BorderLayout.EAST);

            pernoitesPanel.add(pernoiteButton);

            pernoiteButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    pernoiteButton.setBackground(Cor.CINZA_CLARO);
                    blocoInfoPanelInferior.setBackground(Cor.CINZA_CLARO);
                    blocoInfoPanelSuperior.setBackground(Cor.CINZA_CLARO);
                    tituloStatus.setBackground(Cor.CINZA_CLARO);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    pernoiteButton.setBackground(Cor.BRANCO);
                    blocoInfoPanelInferior.setBackground(Cor.BRANCO);
                    blocoInfoPanelSuperior.setBackground(Cor.BRANCO);
                    tituloStatus.setForeground(Cor.BRANCO);
                }
            });
        });

        statusPanel.add(pernoitesPanel, BorderLayout.CENTER);

        return statusPanel;
    }


}
