package principals.panels.pernoitesSubPanels;

import enums.StatusPernoiteEnum;
import principals.tools.BotaoArredondado;
import principals.tools.Converter;
import principals.tools.Cor;
import principals.tools.Icones;
import repository.PernoitesRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

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
                cor = Cor.VERMELHO;
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
        pernoitesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pernoitesPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
//        pernoitesPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        pernoitesPanel.setPreferredSize(new Dimension(800, 800));
        pernoitesPanel.setMaximumSize(new Dimension(800, 800));

        Color finalCor = cor;
        pernoitesRepository.buscaPernoitesPorStatus(statusPernoiteEnum).forEach(pernoite -> {
            BotaoArredondado pernoiteButton = new BotaoArredondado("");
            pernoiteButton.setLayout(null);
            pernoiteButton.setPreferredSize(new Dimension(810, 150));
            pernoiteButton.setBackground(Color.WHITE);
            pernoiteButton.setBorderPainted(false);
            pernoiteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));



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

            JLabel idLabel = new JLabel("#" + pernoite.pernoite_id());
            idLabel.setFont(new Font("Inter", Font.BOLD, 17));
            idLabel.setForeground(Color.RED);

            JPanel statusPernoitePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            statusPernoitePanel.setBackground(Color.WHITE);
            statusPernoitePanel.setBorder(BorderFactory.createEmptyBorder(2, 30, 2, 5));

            JLabel statusPernoiteLabel = new JLabel(Converter.converterStatusPernoite(pernoite.status_pernoite()));
            statusPernoiteLabel.setFont(new Font("Inter", Font.BOLD, 17));
            statusPernoiteLabel.setForeground(finalCor);
            statusPernoitePanel.add(statusPernoiteLabel);

            leftPanel.add(idLabel);
            leftPanel.add(statusPernoitePanel);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            rightPanel.setOpaque(false);

            JLabel totalTextoLabel = new JLabel("Total: ");
            totalTextoLabel.setFont(new Font("Inter", Font.BOLD, 23));
            totalTextoLabel.setForeground(Cor.CINZA_ESCURO);

            JLabel totalLabel = new JLabel(String.format("%.2f", pernoite.valor_total()).replace(".",","));
            totalLabel.setFont(new Font("Inter", Font.BOLD, 23));
            totalLabel.setForeground(Cor.VERDE_ESCURO);

            rightPanel.add(totalTextoLabel);
            rightPanel.add(totalLabel);

            blocoInfoPanelSuperior.add(leftPanel, BorderLayout.WEST);
            blocoInfoPanelSuperior.add(rightPanel, BorderLayout.EAST);




            JPanel blocoInfoPanelInferior = new JPanel(new BorderLayout());
            blocoInfoPanelInferior.setBackground(Color.WHITE);
            blocoInfoPanelInferior.setBounds(100, 47, 695, 38);
            pernoiteButton.add(blocoInfoPanelInferior);

            JPanel painelEsquerdo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            painelEsquerdo.setOpaque(false);

            ImageIcon iconeCalendario = resizeIcon(Icones.calendario, 20, 20);
            JLabel labelCalendario = new JLabel(iconeCalendario);
            labelCalendario.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

            JLabel labelDataEntrada = new JLabel(pernoite.data_entrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            labelDataEntrada.setToolTipText("Data de entrada");
            labelDataEntrada.setFont(new Font("Inter", Font.BOLD, 20));
            labelDataEntrada.setForeground(Cor.CINZA_ESCURO);
            labelDataEntrada.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));

            JLabel labelDataSaida = new JLabel(pernoite.data_saida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            labelDataSaida.setToolTipText("Data de saida");
            labelDataSaida.setForeground(Cor.CINZA_ESCURO);
            labelDataSaida.setFont(new Font("Inter", Font.BOLD, 20));

            painelEsquerdo.add(labelCalendario);
            painelEsquerdo.add(labelDataEntrada);
            painelEsquerdo.add(labelDataSaida);

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




            JPanel blocoInferiorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Espaçamento de 10px entre componentes
            blocoInferiorPanel.setBackground(Color.WHITE);
            blocoInferiorPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
            blocoInferiorPanel.setBounds(10, 90, 780, 50); // Define posição e tamanho
            pernoiteButton.add(blocoInferiorPanel);

            ImageIcon iconePessoa = resizeIcon(Icones.usuarios, 20, 20); // Supondo que você tenha um método para redimensionar ícones
            JLabel iconePessoaLabel = new JLabel(iconePessoa);

            JLabel labelNumero = new JLabel("#"+pernoite.representante().id().toString());
            labelNumero.setForeground(Color.RED);
            labelNumero.setFont(new Font("Inter", Font.BOLD, 17));
            labelNumero.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Adiciona o cursor de "mão" ao passar sobre o número

            JLabel labelNome = new JLabel(pernoite.representante().nome() + "   "+ pernoite.representante().telefone());
            labelNome.setForeground(Cor.CINZA_ESCURO);
            labelNome.setFont(new Font("Inter", Font.BOLD, 17));

            blocoInferiorPanel.add(iconePessoaLabel);
            blocoInferiorPanel.add(labelNumero);
            blocoInferiorPanel.add(labelNome);

            pernoitesPanel.add(pernoiteButton);


            pernoiteButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    pernoiteButton.setBackground(Cor.CINZA_CLARO);
                    blocoInferiorPanel.setBackground(Cor.CINZA_CLARO);
                    blocoInfoPanelInferior.setBackground(Cor.CINZA_CLARO);
                    blocoInfoPanelSuperior.setBackground(Cor.CINZA_CLARO);
                    tituloStatus.setBackground(Cor.CINZA_CLARO);
                    statusPernoitePanel.setBackground(Cor.CINZA_CLARO);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    pernoiteButton.setBackground(Cor.BRANCO);
                    blocoInferiorPanel.setBackground(Cor.BRANCO);
                    blocoInfoPanelInferior.setBackground(Cor.BRANCO);
                    blocoInfoPanelSuperior.setBackground(Cor.BRANCO);
                    tituloStatus.setForeground(Cor.BRANCO);
                    statusPernoitePanel.setBackground(Cor.BRANCO);
                }
            });

        });



        statusPanel.add(pernoitesPanel, BorderLayout.CENTER);

        return statusPanel;
    }

    private static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);  // Retorna o ícone redimensionado
    }
}
