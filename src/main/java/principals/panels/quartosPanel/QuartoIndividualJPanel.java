package principals.panels.quartosPanel;

import enums.StatusQuartoEnum;
import principals.tools.*;
import repository.QuartosRepository;
import response.QuartoResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;

public class QuartoIndividualJPanel {
    QuartosRepository quartosRepository = new QuartosRepository();
    int largura = 30;
    int altura = 30;
    Font font = new Font("Segoe UI", Font.BOLD, 18);
    Color cor = Cor.AZUL_ESCURO;

    public JPanel mainPanel() {
        var quartos = quartosRepository.buscaTodosOsQuartos();
        quartos.sort(Comparator.comparingLong(QuartoResponse::quarto_id));

        JPanel quartoPanel = new JPanel();
        quartoPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
        quartoPanel.setBackground(Cor.CINZA_CLARO);
        quartoPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,0));

        for (QuartoResponse quarto : quartos) {
            JLabel icone_rede = new JLabel(Resize.resizeIcon(Icones.rede, largura, altura));
            JLabel icone_cama_casal = new JLabel(Resize.resizeIcon(Icones.cama_casal, largura, altura));
            JLabel icone_cama_solteiro = new JLabel(Resize.resizeIcon(Icones.cama_solteiro, largura, altura));
            JLabel icone_beliche = new JLabel(Resize.resizeIcon(Icones.beliche, largura, altura));
            JLabel icone_qtd_pessoas = new JLabel(Resize.resizeIcon(Icones.usuarios, largura, altura));

            JTextFieldComTextoFixoArredondado categoria = new JTextFieldComTextoFixoArredondado("", 0);
            categoria.setFont(new Font("Segoe UI", Font.BOLD, 15));
            categoria.setBackground(Cor.CINZA_CLARO);

            JComboBoxArredondado<StatusQuartoEnum> statusQuartoComboBox = new JComboBoxArredondado<>();
            statusQuartoComboBox.setEditable(false);
            statusQuartoComboBox.setPreferredSize(new Dimension(200, 30));
            Arrays.stream(StatusQuartoEnum.values()).forEach(statusQuartoComboBox::addItem);

            JTextFieldComTextoFixoArredondadoRelatorios tabelaPreco = new JTextFieldComTextoFixoArredondadoRelatorios("$",0);
            tabelaPreco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tabelaPreco.setFont(new Font("Segoe UI", Font.BOLD, 20));
            tabelaPreco.setBackground(Cor.VERDE_ESCURO);
            tabelaPreco.setEnabled(false);
            tabelaPreco.setForeground(Color.white);

            tabelaPreco.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    tabelaPreco.setBackground(Cor.VERDE_ESCURO.brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    tabelaPreco.setBackground(Cor.VERDE_ESCURO);
                }
            });

            BotaoArredondado quartoButton = new BotaoArredondado("");
            quartoButton.setPreferredSize(new Dimension(405, 150));
            quartoButton.setBackground(Color.WHITE);
            quartoButton.setBorderPainted(false);
            quartoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            quartoButton.setLayout(new BorderLayout());
            quartoButton.setBorder(BorderFactory.createEmptyBorder(15,15,0,15));

            quartoButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((BotaoArredondado) e.getSource()).setShowBorder(true, cor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((BotaoArredondado) e.getSource()).setShowBorder(false, Color.WHITE);
                }
            });

            JPanel panelInterno = new JPanel(new BorderLayout());
            panelInterno.setBackground(Color.WHITE);

            PanelArredondado panelVermelho = new PanelArredondado();
            panelVermelho.setBackground(Color.WHITE);
            panelVermelho.setForeground(Color.DARK_GRAY);

            JLabel numeroQuarto = new JLabel(quarto.quarto_id() < 10 ? "0" + quarto.quarto_id() : quarto.quarto_id() + "");
            numeroQuarto.setForeground(Cor.CINZA_CLARO);
            numeroQuarto.setFont(new Font("Segoe UI", Font.BOLD, 45));

            panelVermelho.setPreferredSize(new Dimension(90, 0));
            panelVermelho.add(numeroQuarto);
            panelInterno.add(panelVermelho, BorderLayout.WEST);

            JPanel panelBranco = new JPanel(new BorderLayout());
            panelBranco.setBackground(Color.WHITE);

            JPanel panelVerde = new JPanel(new BorderLayout());
            panelVerde.setBackground(Color.WHITE);
            panelVerde.setPreferredSize(new Dimension(0, 40));
            panelVerde.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
            panelBranco.add(panelVerde, BorderLayout.NORTH);

            panelVerde.add(statusQuartoComboBox,  BorderLayout.WEST);
            panelVerde.add(tabelaPreco, BorderLayout.EAST);

            JPanel panelLaranja = new JPanel(new BorderLayout());
            panelLaranja.setBackground(Color.WHITE);
            panelLaranja.setPreferredSize(new Dimension(0, 140));
            panelLaranja.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

           if (quarto.categoria() != null){

               int categoriaLength = quarto.categoria().categoria().length();
               int columnSize = (int) (categoriaLength * 0.75);
               categoria.setColumns(columnSize);

               categoria.setText(quarto.categoria().categoria());
               categoria.setColumns(columnSize);
               categoria.setForeground(Color.WHITE);
           }
            panelLaranja.add(categoria, BorderLayout.WEST);

            panelBranco.add(panelLaranja, BorderLayout.CENTER);


            panelInterno.add(panelBranco, BorderLayout.CENTER);

            JPanel panelAzul = new JPanel(new BorderLayout());
            panelAzul.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
            panelAzul.setBackground(Color.WHITE);
            panelAzul.setPreferredSize(new Dimension(0, 55));
            quartoButton.add(panelInterno, BorderLayout.CENTER);
            quartoButton.add(panelAzul, BorderLayout.SOUTH);

            JPanel panelAzulSuperior = new JPanel(new BorderLayout());
            panelAzulSuperior.setPreferredSize(new Dimension(0, 2));

            panelAzul.add(panelAzulSuperior, BorderLayout.NORTH);

            JPanel panelAzulEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            panelAzulEsquerda.setBackground(Color.WHITE);

            JPanel panelAzulDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            panelAzulDireita.setBackground(Color.WHITE);


            JLabel qtdPessoasLabel = new JLabel(quarto.quantidade_pessoas().toString());
            qtdPessoasLabel.setForeground(Cor.VERDE_ESCURO);
            qtdPessoasLabel.setFont(font);

            panelAzulEsquerda.add(icone_qtd_pessoas);
            panelAzulEsquerda.add(qtdPessoasLabel);

            if (quarto.qtd_cama_casal() > 0) {
                panelAzulDireita.add(icone_cama_casal);
                JLabel numero = new JLabel(quarto.qtd_cama_casal() + " ");
                numero.setForeground(Cor.VERDE_ESCURO);
                numero.setFont(font);
                panelAzulDireita.add(numero);
            }

            if (quarto.qtd_cama_solteiro() > 0) {
                panelAzulDireita.add(icone_cama_solteiro);
                JLabel numero = new JLabel(quarto.qtd_cama_solteiro() + " ");
                numero.setForeground(Cor.VERDE_ESCURO);
                numero.setFont(font);
                panelAzulDireita.add(numero);
            }

            if (quarto.qtd_rede() > 0) {
                panelAzulDireita.add(icone_rede);
                JLabel numero = new JLabel(quarto.qtd_rede()+ " ");
                numero.setForeground(Cor.VERDE_ESCURO);
                numero.setFont(font);
                panelAzulDireita.add(numero);
            }

            if (quarto.qtd_cama_beliche() > 0) {
                panelAzulDireita.add(icone_beliche);
                JLabel numero = new JLabel(quarto.qtd_cama_beliche()+ " ");
                numero.setForeground(Cor.VERDE_ESCURO);
                numero.setFont(font);
                panelAzulDireita.add(numero);
            }

            panelAzul.add(panelAzulEsquerda, BorderLayout.WEST);
            panelAzul.add(panelAzulDireita, BorderLayout.EAST);

            quartoPanel.add(quartoButton);

            switch (quarto.status_quarto_enum()){
                case OCUPADO -> {
                   panelVermelho.setBackground(new Color(0xF88E8E));
                    statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.OCUPADO);
                    statusQuartoComboBox.setEnabled(false);
                }
                case RESERVADO -> {
                    panelVermelho.setBackground(new Color(0xE7CE8A));
                    statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.RESERVADO);
                }
                case DISPONIVEL -> {
                    panelVermelho.setBackground(new Color(0x9EDFA5));
                    statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.DISPONIVEL);
                }
                case DIARIA_ENCERRADA -> {
                    panelVermelho.setBackground(new Color(0xB65D5D));
                    statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.DIARIA_ENCERRADA);
                }
                case LIMPEZA -> {
                    panelVermelho.setBackground(Color.ORANGE);
                    statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.LIMPEZA);
                }
                case MANUTENCAO -> {
                    panelVermelho.setBackground(new Color(0xA19D9D));
                    statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.MANUTENCAO);
                }
            }

            statusQuartoComboBox.addActionListener(e -> {
                switch (statusQuartoComboBox.getSelectedItem().toString()) {
                    case "OCUPADO":
                        panelVermelho.setBackground(new Color(0xF88E8E));
                        quartosRepository.alterarStatusQuarto(quarto.quarto_id(), StatusQuartoEnum.OCUPADO);
                        break;
                    case "RESERVADO":
                        panelVermelho.setBackground(new Color(0xE7CE8A));
                        quartosRepository.alterarStatusQuarto(quarto.quarto_id(), StatusQuartoEnum.RESERVADO);
                        break;
                    case "DISPONIVEL":
                        panelVermelho.setBackground(new Color(0x9EDFA5));
                        quartosRepository.alterarStatusQuarto(quarto.quarto_id(), StatusQuartoEnum.DISPONIVEL);
                        break;
                    case "DIARIA_ENCERRADA":
                        panelVermelho.setBackground(new Color(0xB65D5D));
                        quartosRepository.alterarStatusQuarto(quarto.quarto_id(), StatusQuartoEnum.DIARIA_ENCERRADA);
                        break;
                    case "LIMPEZA":
                        panelVermelho.setBackground(Color.ORANGE);
                        quartosRepository.alterarStatusQuarto(quarto.quarto_id(), StatusQuartoEnum.LIMPEZA);
                        break;
                    case "MANUTENCAO":
                        panelVermelho.setBackground(new Color(0xA19D9D));
                        quartosRepository.alterarStatusQuarto(quarto.quarto_id(), StatusQuartoEnum.MANUTENCAO);
                        break;
                    default:
                        panelVermelho.setBackground(Color.GRAY);
                }
            });

        }

        return quartoPanel;
    }





}

